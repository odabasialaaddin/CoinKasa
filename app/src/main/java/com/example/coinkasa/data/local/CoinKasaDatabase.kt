package com.example.coinkasa.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.coinkasa.data.local.dao.CoinDao
import com.example.coinkasa.data.local.dao.TransactionDao
import com.example.coinkasa.data.local.entity.CoinEntity
import com.example.coinkasa.data.local.entity.TransactionEntity

@Database(
    entities = [CoinEntity::class, TransactionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CoinKasaDatabase : RoomDatabase() {

    abstract val coinDao: CoinDao
    abstract val transactionDao: TransactionDao

}