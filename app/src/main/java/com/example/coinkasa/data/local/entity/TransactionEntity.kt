package com.example.coinkasa.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transaction_table")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val transactionId: Int = 0,

    val coinId: String,
    val transactionType: String,
    val amount: Double,
    val pricePerCoin: Double,
    val dateMillis: Long
)
