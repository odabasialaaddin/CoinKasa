package com.example.coinkasa.domain.model

data class ProfitLossResult(
    val totalAmount: Double,
    val averageCost: Double,
    val totalInvestment: Double,
    val currentValue: Double,
    val profitLoss: Double,
    val profitLossPercentage: Double
)
