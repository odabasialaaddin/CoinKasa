package com.example.coinkasa.di

import android.app.Application
import androidx.room.Room
import com.example.coinkasa.data.local.CoinKasaDatabase
import com.example.coinkasa.data.remote.CoinGeckoApi
import com.example.coinkasa.data.repository.CoinRepositoryImpl
import com.example.coinkasa.domain.repository.CoinRepository
import com.example.coinkasa.domain.use_case.CalculateProfitLossUseCase
import com.example.coinkasa.domain.use_case.CoinUseCases
import com.example.coinkasa.domain.use_case.DeleteTransactionUseCase
import com.example.coinkasa.domain.use_case.GetCoinsUseCase
import com.example.coinkasa.domain.use_case.GetTransactionsByCoinIdUseCase
import com.example.coinkasa.domain.use_case.GetTransactionsUseCase
import com.example.coinkasa.domain.use_case.InsertTransactionUseCase
import com.example.coinkasa.domain.use_case.SearchCoinsUseCase
import com.example.coinkasa.util.Constants
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideCoinGeckoApi(moshi: Moshi): CoinGeckoApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(CoinGeckoApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCoinKasaDatabase(app: Application): CoinKasaDatabase {
        return Room.databaseBuilder(
            app,
            CoinKasaDatabase::class.java,
            "coin_kasa_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideCoinRepository(
        api: CoinGeckoApi,
        db: CoinKasaDatabase
    ): CoinRepository {
        return CoinRepositoryImpl(
            api = api,
            db = db,
            transactionDao = db.transactionDao
        )
    }

    @Provides
    @Singleton
    fun provideCoinUseCases(repository: CoinRepository): CoinUseCases {
        return CoinUseCases(
            getCoins = GetCoinsUseCase(repository),
            searchCoins = SearchCoinsUseCase(repository),
            insertTransaction = InsertTransactionUseCase(repository),
            deleteTransaction = DeleteTransactionUseCase(repository),
            getTransactions = GetTransactionsUseCase(repository),
            getTransactionsByCoinId = GetTransactionsByCoinIdUseCase(repository),
            calculateProfitLoss = CalculateProfitLossUseCase()
        )
    }
}