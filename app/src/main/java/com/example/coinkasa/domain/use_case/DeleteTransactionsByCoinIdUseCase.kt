package com.example.coinkasa.domain.use_case

import com.example.coinkasa.domain.repository.TransactionRepository
import javax.inject.Inject

class DeleteTransactionsByCoinIdUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(coinId: String) {
        repository.deleteTransactionsByCoinId(coinId)
    }
}