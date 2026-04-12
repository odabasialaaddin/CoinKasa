package com.example.coinkasa.data.mapper

import com.example.coinkasa.data.local.entity.CoinEntity
import com.example.coinkasa.data.remote.dto.CoinDto
import com.example.coinkasa.data.remote.dto.SearchCoinDto
import com.example.coinkasa.domain.model.Coin

fun CoinDto.toCoin(): Coin {
    return Coin(
        id = id ?: "",
        symbol = symbol ?: "",
        name = name ?: "",
        currentPrice = currentPrice ?: 0.0,
        priceChangePercentage24h = priceChangePercentage24h ?: 0.0,
        imageUrl = image ?: "",
        marketCapRank = marketCapRank ?: 0
    )
}

fun CoinDto.toCoinEntity(): CoinEntity {
    return CoinEntity(
        id = id ?: "",
        symbol = symbol ?: "",
        name = name ?: "",
        currentPrice = currentPrice ?: 0.0,
        priceChangePercentage24h = priceChangePercentage24h ?: 0.0,
        imageUrl = image ?: "",
        marketCapRank = marketCapRank ?: 0
    )
}

fun CoinEntity.toCoin(): Coin {
    return Coin(
        id = id,
        symbol = symbol,
        name = name,
        currentPrice = currentPrice,
        priceChangePercentage24h = priceChangePercentage24h,
        imageUrl = imageUrl,
        marketCapRank = marketCapRank
    )
}

fun SearchCoinDto.toCoin(): Coin {
    return Coin(
        id = id ?: "",
        symbol = symbol ?: "",
        name = name ?: "",
        currentPrice = 0.0,
        priceChangePercentage24h = 0.0,
        imageUrl = image ?: "",
        marketCapRank = marketCapRank ?: 0
    )
}