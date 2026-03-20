package com.example.coinkasa.domain.repository

import androidx.paging.PagingData
import com.example.coinkasa.data.local.entity.TransactionEntity
import com.example.coinkasa.domain.model.Coin
import com.example.coinkasa.util.Resource
import kotlinx.coroutines.flow.Flow

interface CoinRepository {

    fun getCoins(): Flow<PagingData<Coin>>

    fun getAllTransactions(): Flow<List<TransactionEntity>>

    fun getTransactionsByCoinId(coinId: String): Flow<List<TransactionEntity>>

    suspend fun insertTransaction(transaction: TransactionEntity)

    suspend fun deleteTransaction(transaction: TransactionEntity)

    fun searchCoins(query: String): Flow<Resource<List<Coin>>>
}