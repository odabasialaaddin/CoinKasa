package com.example.coinkasa.di

import android.app.Application
import androidx.room.Room
import com.example.coinkasa.data.local.CoinKasaDatabase
import com.example.coinkasa.data.local.dao.TransactionDao
import com.example.coinkasa.data.remote.CoinGeckoApi
import com.example.coinkasa.data.repository.CoinRepositoryImpl
import com.example.coinkasa.domain.repository.CoinRepository
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
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideTransactionDao(db: CoinKasaDatabase): TransactionDao {
        return db.transactionDao
    }

    @Provides
    @Singleton
    fun provideCoinRepository(
        api: CoinGeckoApi,
        db: CoinKasaDatabase
    ): CoinRepository {
        return CoinRepositoryImpl(
            api = api,
            db = db
        )
    }
}