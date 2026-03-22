package com.example.coinkasa.domain.use_case

import com.example.coinkasa.data.local.entity.TransactionEntity
import com.example.coinkasa.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val repository: CoinRepository
) {

    operator fun invoke(): Flow<List<TransactionEntity>> {
        return repository.getAllTransactions()
    }
}