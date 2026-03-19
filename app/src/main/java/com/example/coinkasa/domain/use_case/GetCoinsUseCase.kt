package com.example.coinkasa.domain.use_case

import com.example.coinkasa.domain.model.Coin
import com.example.coinkasa.domain.repository.CoinRepository
import com.example.coinkasa.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCoinsUseCase @Inject constructor(
    private val repository: CoinRepository
) {
    operator fun invoke(): Flow<Resource<List<Coin>>> {
        return repository.getCoins()
    }
}