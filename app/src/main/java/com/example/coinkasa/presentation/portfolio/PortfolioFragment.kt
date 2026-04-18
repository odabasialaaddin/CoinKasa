package com.example.coinkasa.presentation.portfolio

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coinkasa.databinding.FragmentPortfolioBinding
import com.example.coinkasa.presentation.add_transaction.AddTransactionBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class PortfolioFragment : Fragment() {

    private var _binding: FragmentPortfolioBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PortfolioViewModel by viewModels()
    private lateinit var portfolioAdapter: PortfolioAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPortfolioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        portfolioAdapter = PortfolioAdapter { portfolioItem ->
            val addTransactionSheet = AddTransactionBottomSheetFragment.newInstance(
                coinId = portfolioItem.coinId,
                coinName = portfolioItem.coinName,
                coinSymbol = portfolioItem.coinSymbol
            )
            addTransactionSheet.show(childFragmentManager, addTransactionSheet.tag)
        }
        binding.rvPortfolio.apply {
            adapter = portfolioAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                    if (!state.isLoading) {
                        binding.tvTotalBalance.text = String.format(Locale.US, "$%.2f", state.totalBalance)

                        val profitLossSign = if (state.totalProfitLoss >= 0) "+" else ""
                        binding.tvTotalProfitLoss.text = String.format(
                            Locale.US,
                            "%s$%.2f (%s%.2f%%)",
                            profitLossSign, state.totalProfitLoss,
                            profitLossSign, state.totalProfitLossPercentage
                        )

                        if (state.totalProfitLoss >= 0) {
                            binding.tvTotalProfitLoss.setTextColor(Color.parseColor("#00C853"))
                        } else {
                            binding.tvTotalProfitLoss.setTextColor(Color.parseColor("#D50000"))
                        }

                        portfolioAdapter.submitList(state.portfolioItems)

                        binding.tvEmptyPortfolio.visibility = if (state.portfolioItems.isEmpty()) View.VISIBLE else View.GONE
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