package com.example.coinkasa.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.coinkasa.data.local.dao.CoinDao
import com.example.coinkasa.data.local.dao.RemoteKeyDao
import com.example.coinkasa.data.local.dao.TransactionDao
import com.example.coinkasa.data.local.entity.CoinEntity
import com.example.coinkasa.data.local.entity.RemoteKeyEntity
import com.example.coinkasa.data.local.entity.TransactionEntity

@Database(
    entities = [CoinEntity::class, TransactionEntity::class, RemoteKeyEntity::class],
    version = 4,
    exportSchema = false
)
abstract class CoinKasaDatabase : RoomDatabase() {

    abstract val coinDao: CoinDao
    abstract val transactionDao: TransactionDao
    abstract val remoteKeyDao: RemoteKeyDao

}