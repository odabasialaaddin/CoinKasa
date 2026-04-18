package com.example.coinkasa.presentation.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coinkasa.domain.model.PortfolioItem
import com.example.coinkasa.domain.use_case.CoinUseCases
import com.example.coinkasa.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PortfolioState(
    val isLoading: Boolean = false,
    val portfolioItems: List<PortfolioItem> = emptyList(),
    val totalBalance: Double = 0.0,
    val totalProfitLoss: Double = 0.0,
    val totalProfitLossPercentage: Double = 0.0,
    val error: String = ""
)

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val coinUseCases: CoinUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(PortfolioState())
    val state = _state.asStateFlow()

    init {
        loadPortfolio()
    }

    private fun loadPortfolio() {
        viewModelScope.launch {
            coinUseCases.getTransactions().collectLatest { transactions ->
                if (transactions.isEmpty()) {
                    _state.value = PortfolioState(portfolioItems = emptyList())
                    return@collectLatest
                }

                _state.value = _state.value.copy(isLoading = true)

                try {
                    val groupedTransactions = transactions.groupBy { it.coinId }
                    val coinIds = groupedTransactions.keys.joinToString(",")

                    val apiResult = coinUseCases.getCoinsByIds(coinIds).first { it !is Resource.Loading }

                    val currentCoinsMap = if (apiResult is Resource.Success) {
                        apiResult.data?.associateBy { it.id } ?: emptyMap()
                    } else {
                        emptyMap()
                    }

                    val portfolioItems = mutableListOf<PortfolioItem>()
                    var totalBalance = 0.0
                    var totalInvestment = 0.0

                    for ((coinId, coinTransactions) in groupedTransactions) {
                        val firstTx = coinTransactions.first()
                        val currentCoin = currentCoinsMap[coinId]

                        val currentPrice = currentCoin?.currentPrice ?: 0.0
                        val imageUrl = currentCoin?.imageUrl ?: ""

                        val profitLossResult = coinUseCases.calculateProfitLoss(coinTransactions, currentPrice)

                        if (profitLossResult.totalAmount > 0) {
                            portfolioItems.add(
                                PortfolioItem(
                                    coinId = coinId,
                                    coinName = firstTx.coinName,
                                    coinSymbol = firstTx.coinSymbol,
                                    imageUrl = imageUrl,
                                    amount = profitLossResult.totalAmount,
                                    currentPrice = currentPrice,
                                    totalValue = profitLossResult.currentValue,
                                    profitLoss = profitLossResult.profitLoss,
                                    profitLossPercentage = profitLossResult.profitLossPercentage
                                )
                            )

                            totalBalance += profitLossResult.currentValue
                            totalInvestment += profitLossResult.totalInvestment
                        }
                    }

                    val totalProfitLoss = totalBalance - totalInvestment
                    val totalProfitLossPercentage = if (totalInvestment > 0) {
                        (totalProfitLoss / totalInvestment) * 100
                    } else {
                        0.0
                    }

                    _state.value = PortfolioState(
                        isLoading = false,
                        portfolioItems = portfolioItems,
                        totalBalance = totalBalance,
                        totalProfitLoss = totalProfitLoss,
                        totalProfitLossPercentage = totalProfitLossPercentage
                    )

                } catch (e: Exception) {
                    _state.value = PortfolioState(
                        isLoading = false,
                        error = "Portföy hesaplanırken bir hata oluştu."
                    )
                }
            }
        }
    }
}