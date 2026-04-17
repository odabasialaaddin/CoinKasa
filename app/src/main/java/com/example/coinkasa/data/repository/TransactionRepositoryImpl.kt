package com.example.coinkasa.data.repository

import com.example.coinkasa.data.local.dao.TransactionDao
import com.example.coinkasa.data.local.entity.TransactionEntity
import com.example.coinkasa.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionRepository {

    override suspend fun insertTransaction(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
    }

    override suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
    }

    override fun getAllTransactions(): Flow<List<TransactionEntity>> {
        return transactionDao.getAllTransactions()
    }

    override fun getTransactionsByCoinId(coinId: String): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsByCoinId(coinId)
    }
}