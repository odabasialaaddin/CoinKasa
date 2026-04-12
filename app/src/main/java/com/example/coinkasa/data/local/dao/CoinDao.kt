package com.example.coinkasa.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.coinkasa.data.local.entity.CoinEntity

@Dao
interface CoinDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(coins: List<CoinEntity>)

    @Query("SELECT * FROM coin_table ORDER BY marketCapRank ASC, id ASC")
    fun getCoinsPagingSource(): PagingSource<Int, CoinEntity>

    @Query("DELETE FROM coin_table")
    suspend fun clearAllCoins()
}