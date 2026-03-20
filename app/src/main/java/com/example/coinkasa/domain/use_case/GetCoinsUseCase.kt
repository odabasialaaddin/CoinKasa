package com.example.coinkasa.domain.use_case

import androidx.paging.PagingData
import com.example.coinkasa.domain.model.Coin
import com.example.coinkasa.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCoinsUseCase @Inject constructor(
    private val repository: CoinRepository
) {
    operator fun invoke(): Flow<PagingData<Coin>> {
        return repository.getCoins()
    }
}