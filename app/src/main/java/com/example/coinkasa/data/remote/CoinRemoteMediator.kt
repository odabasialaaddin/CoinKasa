package com.example.coinkasa.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.coinkasa.data.local.CoinKasaDatabase
import com.example.coinkasa.data.local.entity.CoinEntity
import com.example.coinkasa.data.local.entity.RemoteKeyEntity
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class CoinRemoteMediator(
    private val api: CoinGeckoApi,
    private val db: CoinKasaDatabase
) : RemoteMediator<Int, CoinEntity>() {

    private val coinDao = db.coinDao
    private val remoteKeyDao = db.remoteKeyDao

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CoinEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                nextKey
            }
        }

        return try {
            val response = api.getCoins(page = page, perPage = state.config.pageSize)
            val endOfPaginationReached = response.isEmpty()

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    coinDao.clearAllCoins()
                    remoteKeyDao.clearRemoteKeys()
                }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                val keys = response.map {
                    RemoteKeyEntity(coinId = it.id ?: "", prevKey = prevKey, nextKey = nextKey)
                }

                val coinEntities = response.map { dto ->
                    CoinEntity(
                        id = dto.id ?: "",
                        symbol = dto.symbol ?: "",
                        name = dto.name ?: "",
                        currentPrice = dto.currentPrice ?: 0.0,
                        priceChangePercentage24h = dto.priceChangePercentage24h ?: 0.0,
                        imageUrl = dto.image ?: "",
                        marketCapRank = dto.marketCapRank ?: 0
                    )
                }

                remoteKeyDao.insertAll(keys)
                coinDao.insertAll(coinEntities)
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, CoinEntity>): RemoteKeyEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { coin ->
            remoteKeyDao.getRemoteKeyByCoinId(coin.id)
        }
    }
}