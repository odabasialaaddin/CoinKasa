package com.example.coinkasa.presentation.coin_list

import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coinkasa.R
import com.example.coinkasa.core.base.BaseFragment
import com.example.coinkasa.core.delegate.viewBinding
import com.example.coinkasa.core.state.UiState
import com.example.coinkasa.databinding.FragmentCoinListBinding
import com.example.coinkasa.presentation.add_transaction.AddTransactionBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CoinListFragment : BaseFragment<FragmentCoinListBinding, CoinListViewModel>() {

    override val binding by viewBinding(FragmentCoinListBinding::bind)
    override val viewModel: CoinListViewModel by viewModels()

    private var coinPagingAdapter: CoinPagingAdapter? = null
    private var coinSearchAdapter: CoinSearchAdapter? = null

    override fun getLayoutRes(): Int = R.layout.fragment_coin_list

    override fun setupViews() {
        createAdapters()
        setupRecyclerViews()
        setupSearch()
        setupSwipeRefresh()
        setupScrollToTopFab()
    }

    private fun createAdapters() {
        coinPagingAdapter = CoinPagingAdapter { coin ->
            openAddTransactionSheet(coin.id, coin.name, coin.symbol)
        }
        coinSearchAdapter = CoinSearchAdapter { coin ->
            openAddTransactionSheet(coin.id, coin.name, coin.symbol)
        }
    }

    private fun openAddTransactionSheet(id: String, name: String, symbol: String) {
        val bottomSheet = AddTransactionBottomSheetFragment.newInstance(
            coinId = id,
            coinName = name,
            coinSymbol = symbol
        )
        bottomSheet.show(childFragmentManager, "AddTransactionBottomSheet")
    }

    private fun setupRecyclerViews() {
        binding.rvCoins.apply {
            adapter = coinPagingAdapter?.withLoadStateFooter(
                footer = CoinLoadStateAdapter { coinPagingAdapter?.retry() }
            )
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = null
        }

        binding.rvSearch.apply {
            adapter = coinSearchAdapter
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = null
        }

        coinPagingAdapter?.addLoadStateListener { loadState ->
            if (binding.rvCoins.isVisible) {
                binding.progressBar.isVisible = loadState.refresh is LoadState.Loading && !binding.swipeRefresh.isRefreshing

                if (loadState.refresh !is LoadState.Loading) {
                    binding.swipeRefresh.isRefreshing = false
                }

                val errorState = loadState.source.refresh as? LoadState.Error
                    ?: loadState.mediator?.refresh as? LoadState.Error
                binding.tvError.isVisible = errorState != null && coinPagingAdapter?.itemCount == 0
            }
        }
    }

    private fun setupSearch() {
        binding.apply {
            etSearch.addTextChangedListener { editable ->
                val query = editable.toString()
                ivClearSearch.isVisible = query.isNotEmpty()
                ivBack.isVisible = query.isNotEmpty()

                if (query.isNotEmpty()) {
                    rvCoins.isVisible = false
                    rvSearch.isVisible = true
                    viewModel.onSearchQueryChange(query)
                } else {
                    rvSearch.isVisible = false
                    rvCoins.isVisible = true
                    coinSearchAdapter?.submitList(emptyList())
                    viewModel.onSearchQueryChange("")
                }
            }

            ivClearSearch.setOnClickListener {
                etSearch.text?.clear()
            }

            ivBack.setOnClickListener {
                etSearch.text?.clear()
                etSearch.clearFocus()
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            if (binding.rvSearch.isVisible) {
                val query = binding.etSearch.text.toString()
                viewModel.onSearchQueryChange(query)
            } else {
                coinPagingAdapter?.refresh()
            }
        }
    }

    private fun setupScrollToTopFab() {
        binding.rvCoins.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

                if (firstVisibleItem > 10) {
                    binding.fabScrollToTop.show()
                } else {
                    binding.fabScrollToTop.hide()
                }
            }
        })

        binding.fabScrollToTop.setOnClickListener {
            binding.rvCoins.scrollToPosition(0)
            binding.fabScrollToTop.hide()
        }
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.getCoins().collectLatest { pagingData ->
                        coinPagingAdapter?.submitData(pagingData)
                    }
                }

                launch {
                    viewModel.searchState.collectLatest { state ->
                        if (!binding.rvSearch.isVisible) return@collectLatest

                        when (state) {
                            is UiState.Idle -> {
                                binding.progressBar.isVisible = false
                                binding.tvError.isVisible = false
                            }
                            is UiState.Loading -> {
                                binding.progressBar.isVisible = true
                                binding.tvError.isVisible = false
                            }
                            is UiState.Success -> {
                                binding.progressBar.isVisible = false
                                binding.swipeRefresh.isRefreshing = false
                                coinSearchAdapter?.submitList(state.data)
                            }
                            is UiState.Error -> {
                                binding.progressBar.isVisible = false
                                binding.swipeRefresh.isRefreshing = false
                                binding.tvError.text = state.message
                                binding.tvError.isVisible = true
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        coinPagingAdapter = null
        coinSearchAdapter = null
        super.onDestroyView()
    }
}