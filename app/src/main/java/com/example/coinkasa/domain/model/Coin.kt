package com.example.coinkasa.domain.model

data class Coin(
    val id: String,
    val symbol: String,
    val name: String,
    val currentPrice: Double,
    val priceChangePercentage24h: Double,
    val imageUrl: String,
    val marketCapRank: Int?
)
