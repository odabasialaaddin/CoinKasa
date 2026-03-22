package com.example.coinkasa.domain.use_case

import com.example.coinkasa.domain.model.Coin
import com.example.coinkasa.domain.repository.CoinRepository
import com.example.coinkasa.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SearchCoinsUseCase @Inject constructor(
    private val repository: CoinRepository
) {

    operator fun invoke(query: String): Flow<Resource<List<Coin>>>{
        if(query.isBlank()){
            return flow { emit(Resource.Success(emptyList())) }
        }

        return repository.searchCoins(query)
    }
}