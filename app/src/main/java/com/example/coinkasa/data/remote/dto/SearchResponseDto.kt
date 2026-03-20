package com.example.coinkasa.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResponseDto(
    @Json(name = "coins")
    val coins: List<SearchCoinDto>?
)

@JsonClass(generateAdapter = true)
data class SearchCoinDto(
    @Json(name = "id")
    val id: String?,
    @Json(name = "symbol")
    val symbol: String?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "large")
    val image: String?
)