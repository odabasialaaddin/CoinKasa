package com.example.coinkasa.domain.repository

import com.example.coinkasa.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {

    suspend fun insertTransaction(transaction: TransactionEntity)

    suspend fun deleteTransaction(transaction: TransactionEntity)

    fun getAllTransactions(): Flow<List<TransactionEntity>>

    fun getTransactionsByCoinId(coinId: String): Flow<List<TransactionEntity>>

    suspend fun deleteTransactionsByCoinId(coinId: String)
}