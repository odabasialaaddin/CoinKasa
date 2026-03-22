package com.example.coinkasa.domain.use_case

import com.example.coinkasa.data.local.entity.TransactionEntity
import com.example.coinkasa.domain.model.ProfitLossResult
import javax.inject.Inject

class CalculateProfitLossUseCase @Inject constructor() {

    operator fun invoke(transactions: List<TransactionEntity>, currentCoinPrice: Double): ProfitLossResult {
        var totalBuyAmount = 0.0
        var totalSpent = 0.0
        var totalSellAmount = 0.0

        for (transaction in transactions) {
            if (transaction.transactionType == "BUY") {
                totalBuyAmount += transaction.amount
                totalSpent += (transaction.amount * transaction.pricePerCoin)
            } else if (transaction.transactionType == "SELL") {
                totalSellAmount += transaction.amount
            }
        }

        val currentHoldings = totalBuyAmount - totalSellAmount

        if (currentHoldings <= 0.0) {
            return ProfitLossResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        }

        val averageBuyPrice = totalSpent / totalBuyAmount
        val totalCostBasis = currentHoldings * averageBuyPrice
        val currentValue = currentHoldings * currentCoinPrice
        val profitLoss = currentValue - totalCostBasis
        val profitLossPercentage = if (totalCostBasis > 0) {
            (profitLoss / totalCostBasis) * 100
        } else {
            0.0
        }

        return ProfitLossResult(
            totalAmount = currentHoldings,
            averageCost = averageBuyPrice,
            totalInvestment = totalCostBasis,
            currentValue = currentValue,
            profitLoss = profitLoss,
            profitLossPercentage = profitLossPercentage
        )
    }
}