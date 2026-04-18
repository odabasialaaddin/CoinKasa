package com.example.coinkasa.domain.repository

import androidx.paging.PagingData
import com.example.coinkasa.domain.model.Coin
import com.example.coinkasa.util.Resource
import kotlinx.coroutines.flow.Flow

interface CoinRepository {

    fun getCoins(): Flow<PagingData<Coin>>

    fun searchCoins(query: String): Flow<Resource<List<Coin>>>

    fun getCoinsByIds(ids: String): Flow<Resource<List<Coin>>>
}