package com.example.coinkasa.domain.repository

import com.example.coinkasa.domain.model.Coin
import com.example.coinkasa.util.Resource
import kotlinx.coroutines.flow.Flow

interface CoinRepository {

    fun getCoins(): Flow<Resource<List<Coin>>>
}