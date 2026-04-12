package com.example.coinkasa.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CoinDto(
    @Json(name = "id")
    val id: String?,
    @Json(name = "symbol")
    val symbol: String?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "current_price")
    val currentPrice: Double?,
    @Json(name = "price_change_percentage_24h")
    val priceChangePercentage24h: Double?,
    @Json(name = "image")
    val image: String?,
    @Json(name = "market_cap_rank")
    val marketCapRank: Int?

)
