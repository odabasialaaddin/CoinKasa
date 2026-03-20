package com.example.coinkasa.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.coinkasa.data.local.CoinKasaDatabase
import com.example.coinkasa.data.local.dao.TransactionDao
import com.example.coinkasa.data.local.entity.TransactionEntity
import com.example.coinkasa.data.mapper.toCoin
import com.example.coinkasa.data.remote.CoinGeckoApi
import com.example.coinkasa.data.remote.CoinRemoteMediator
import com.example.coinkasa.domain.model.Coin
import com.example.coinkasa.domain.repository.CoinRepository
import com.example.coinkasa.util.Constants
import com.example.coinkasa.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException

class CoinRepositoryImpl(
    private val api: CoinGeckoApi,
    private val db: CoinKasaDatabase,
    private val transactionDao: TransactionDao
) : CoinRepository {

    private val coinDao = db.coinDao

    @OptIn(ExperimentalPagingApi::class)
    override fun getCoins(): Flow<PagingData<Coin>> {
        return Pager(
            config = PagingConfig(
                pageSize = Constants.ITEMS_PER_PAGE,
                prefetchDistance = Constants.PREFETCH_DISTANCE
            ),
            remoteMediator = CoinRemoteMediator(
                api = api,
                db = db
            ),
            pagingSourceFactory = {
                coinDao.getCoinsPagingSource()
            }
        ).flow.map { pagingData ->
            pagingData.map { entity ->
                entity.toCoin()
            }
        }
    }

    override fun getAllTransactions(): Flow<List<TransactionEntity>> {
        return transactionDao.getAllTransactions()
    }

    override fun getTransactionsByCoinId(coinId: String): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsByCoinId(coinId)
    }

    override suspend fun insertTransaction(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
    }

    override suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
    }

    override fun searchCoins(query: String): Flow<Resource<List<Coin>>> = flow {
        try {
            emit(Resource.Loading())
            val response = api.searchCoins(query = query)
            val coins = response.coins?.map { it.toCoin() } ?: emptyList()
            emit(Resource.Success(coins))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Beklenmeyen bir hata oluştu"))
        } catch (e: IOException) {
            emit(Resource.Error("Lütfen internet bağlantınızı kontrol edin"))
        }
    }
}