package com.example.coinkasa.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeyEntity(
    @PrimaryKey val coinId: String,
    val prevKey: Int?,
    val nextKey: Int?
)