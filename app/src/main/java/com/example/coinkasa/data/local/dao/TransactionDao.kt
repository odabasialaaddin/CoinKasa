package com.example.coinkasa.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.coinkasa.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transaction_table ORDER BY dateMillis DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transaction_table WHERE coinId = :coinId ORDER BY dateMillis DESC")
    fun getTransactionsByCoinId(coinId: String): Flow<List<TransactionEntity>>

    @Query("DELETE FROM transaction_table WHERE coinId = :coinId")
    suspend fun deleteTransactionsByCoinId(coinId: String)
}