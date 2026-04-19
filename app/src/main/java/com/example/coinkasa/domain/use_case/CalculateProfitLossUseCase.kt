package com.example.coinkasa.domain.use_case

import com.example.coinkasa.data.local.entity.TransactionEntity
import com.example.coinkasa.domain.model.ProfitLossResult
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

class CalculateProfitLossUseCase @Inject constructor() {

    operator fun invoke(transactions: List<TransactionEntity>, currentCoinPrice: Double): ProfitLossResult {
        var totalBuyAmount = BigDecimal.ZERO
        var totalSpent = BigDecimal.ZERO
        var totalSellAmount = BigDecimal.ZERO

        for (transaction in transactions) {
            val amountStr = transaction.amount.replace(",", ".")
            val priceStr = transaction.pricePerCoin.replace(",", ".")

            val amount = amountStr.toBigDecimalOrNull() ?: BigDecimal.ZERO
            val price = priceStr.toBigDecimalOrNull() ?: BigDecimal.ZERO

            if (transaction.transactionType == "BUY") {
                totalBuyAmount += amount
                totalSpent += (amount * price)
            } else if (transaction.transactionType == "SELL") {
                totalSellAmount += amount
            }
        }

        val currentHoldings = totalBuyAmount - totalSellAmount

        if (currentHoldings <= BigDecimal.ZERO) {
            return ProfitLossResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        }

        val averageBuyPrice = if (totalBuyAmount > BigDecimal.ZERO) {
            totalSpent.divide(totalBuyAmount, 8, RoundingMode.HALF_UP)
        } else {
            BigDecimal.ZERO
        }

        val totalCostBasis = currentHoldings * averageBuyPrice
        val currentPriceBd = BigDecimal.valueOf(currentCoinPrice)
        val currentValue = currentHoldings * currentPriceBd
        val profitLoss = currentValue - totalCostBasis

        val profitLossPercentage = if (totalCostBasis > BigDecimal.ZERO) {
            (profitLoss.divide(totalCostBasis, 8, RoundingMode.HALF_UP)) * BigDecimal("100")
        } else {
            BigDecimal.ZERO
        }

        return ProfitLossResult(
            totalAmount = currentHoldings.toDouble(),
            averageCost = averageBuyPrice.toDouble(),
            totalInvestment = totalCostBasis.toDouble(),
            currentValue = currentValue.toDouble(),
            profitLoss = profitLoss.toDouble(),
            profitLossPercentage = profitLossPercentage.toDouble()
        )
    }
}