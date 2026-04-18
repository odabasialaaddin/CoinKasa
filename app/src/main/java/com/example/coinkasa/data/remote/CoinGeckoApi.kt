package com.example.coinkasa.data.remote

import com.example.coinkasa.BuildConfig
import com.example.coinkasa.data.remote.dto.CoinDto
import com.example.coinkasa.data.remote.dto.SearchResponseDto
import com.example.coinkasa.util.Constants
import retrofit2.http.GET
import retrofit2.http.Query

interface CoinGeckoApi {

    @GET("api/v3/coins/markets")
    suspend fun getCoins(
        @Query("vs_currency") currency: String = Constants.DEFAULT_CURRENCY,
        @Query("per_page") perPage: Int = Constants.ITEMS_PER_PAGE,
        @Query("page") page: Int = Constants.STARTING_PAGE_INDEX,
        @Query("x_cg_demo_api_key") apiKey: String = BuildConfig.API_KEY
    ): List<CoinDto>

    @GET("api/v3/search")
    suspend fun searchCoins(
        @Query("query") query: String,
        @Query("x_cg_demo_api_key") apiKey: String = BuildConfig.API_KEY
    ): SearchResponseDto

    @GET("api/v3/coins/markets")
    suspend fun getCoinsByIds(
        @Query("vs_currency") currency: String = Constants.DEFAULT_CURRENCY,
        @Query("ids") ids: String, // Örn: "bitcoin,ethereum,solana"
        @Query("x_cg_demo_api_key") apiKey: String = BuildConfig.API_KEY
    ): List<CoinDto>
}