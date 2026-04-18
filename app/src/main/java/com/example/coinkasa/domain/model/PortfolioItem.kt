package com.example.coinkasa.domain.model

data class PortfolioItem(
    val coinId: String,
    val coinName: String,
    val coinSymbol: String,
    val imageUrl: String,
    val amount: Double,
    val currentPrice: Double,
    val totalValue: Double,
    val profitLoss: Double,
    val profitLossPercentage: Double
)