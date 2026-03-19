package com.example.coinkasa.data.mapper

import com.example.coinkasa.data.remote.dto.CoinDto
import com.example.coinkasa.domain.model.Coin

fun CoinDto.toCoin(): Coin {
    return Coin(
        id = id ?: "",
        symbol = symbol ?: "",
        name = name ?: "",
        currentPrice = currentPrice ?: 0.0,
        priceChangePercentage24h = priceChangePercentage24h ?: 0.0,
        imageUrl = image ?: ""
    )
}