package com.example.coinkasa.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.coinkasa.data.local.CoinKasaDatabase
import com.example.coinkasa.data.local.entity.CoinEntity
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class CoinRemoteMediator(
    private val api: CoinGeckoApi,
    private val db: CoinKasaDatabase
) : RemoteMediator<Int, CoinEntity>() {

    private val coinDao = db.coinDao

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CoinEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1

                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)

                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    (state.pages.sumOf { it.data.size } / state.config.pageSize) + 1
                }
            }

            val response = api.getCoins(page = page, perPage = state.config.pageSize)

            val coinEntities = response.map { dto ->
                CoinEntity(
                    id = dto.id ?: "",
                    symbol = dto.symbol ?: "",
                    name = dto.name ?: "",
                    currentPrice = dto.currentPrice ?: 0.0,
                    priceChangePercentage24h = dto.priceChangePercentage24h ?: 0.0,
                    imageUrl = dto.image ?: ""
                )
            }

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    coinDao.clearAllCoins()
                }
                coinDao.insertAll(coinEntities)
            }

            MediatorResult.Success(endOfPaginationReached = response.isEmpty())

        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}