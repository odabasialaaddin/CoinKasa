package com.example.coinkasa.presentation.portfolio

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coinkasa.databinding.FragmentPortfolioBinding
import com.example.coinkasa.presentation.add_transaction.AddTransactionBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class PortfolioFragment : Fragment() {

    private var _binding: FragmentPortfolioBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PortfolioViewModel by viewModels()
    private lateinit var portfolioAdapter: PortfolioAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPortfolioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSwipeRefresh()
        observeViewModel()
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshPortfolio()
        }
    }

    private fun setupRecyclerView() {
        portfolioAdapter = PortfolioAdapter { portfolioItem ->
            val addTransactionSheet = AddTransactionBottomSheetFragment.newInstance(
                coinId = portfolioItem.coinId,
                coinName = portfolioItem.coinName,
                coinSymbol = portfolioItem.coinSymbol,
                isFromPortfolio = true
            )
            addTransactionSheet.show(childFragmentManager, addTransactionSheet.tag)
        }

        binding.rvPortfolio.apply {
            adapter = portfolioAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(rV: RecyclerView, vH: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val item = portfolioAdapter.currentList[position]
                viewModel.onSwipe(item, position)
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.rvPortfolio)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect { state ->
                        binding.progressBar.visibility = if (state.isLoading && !binding.swipeRefreshLayout.isRefreshing) View.VISIBLE else View.GONE
                        if (!state.isLoading) {
                            binding.swipeRefreshLayout.isRefreshing = false
                            binding.tvTotalBalance.text = String.format(Locale.US, "$%.2f", state.totalBalance)
                            val profitLossSign = if (state.totalProfitLoss >= 0) "+" else ""
                            binding.tvTotalProfitLoss.text = String.format(Locale.US, "%s$%.2f (%s%.2f%%)", profitLossSign, state.totalProfitLoss, profitLossSign, state.totalProfitLossPercentage)
                            binding.tvTotalProfitLoss.setTextColor(if (state.totalProfitLoss >= 0) Color.parseColor("#00C853") else Color.parseColor("#D50000"))
                            portfolioAdapter.submitList(state.portfolioItems)
                            binding.tvEmptyPortfolio.visibility = if (state.portfolioItems.isEmpty()) View.VISIBLE else View.GONE
                        }
                    }
                }

                launch {
                    viewModel.eventFlow.collectLatest { event ->
                        when (event) {
                            is PortfolioViewModel.UiEvent.ShowDeleteConfirmation -> {
                                AlertDialog.Builder(requireContext())
                                    .setTitle("İşlemi Sil")
                                    .setMessage("${event.coinName} varlığına ait tüm geçmişi silmek istediğinizden emin misiniz?")
                                    .setPositiveButton("Evet") { _, _ -> viewModel.confirmDelete(event.coinId) }
                                    .setNegativeButton("Hayır") { d, _ ->
                                        portfolioAdapter.notifyItemChanged(event.position)
                                        d.dismiss()
                                    }
                                    .setOnCancelListener { portfolioAdapter.notifyItemChanged(event.position) }
                                    .show()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}