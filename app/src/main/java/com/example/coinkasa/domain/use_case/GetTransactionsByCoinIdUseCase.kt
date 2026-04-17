package com.example.coinkasa.domain.use_case

import com.example.coinkasa.data.local.entity.TransactionEntity
import com.example.coinkasa.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetTransactionsByCoinIdUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(coinId: String): Flow<List<TransactionEntity>> {
        if (coinId.isBlank()) {
            return flow { emit(emptyList()) }
        }
        return repository.getTransactionsByCoinId(coinId)
    }
}