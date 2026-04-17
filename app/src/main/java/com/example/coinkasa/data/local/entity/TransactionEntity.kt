package com.example.coinkasa.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transaction_table")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val transactionId: Int = 0,
    val coinId: String,
    val coinName: String,
    val coinSymbol: String,
    val transactionType: String,
    val exchangeName: String,
    val amount: String,
    val pricePerCoin: String,
    val dateMillis: Long
)