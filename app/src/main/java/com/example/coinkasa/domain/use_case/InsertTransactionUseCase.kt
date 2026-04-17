package com.example.coinkasa.domain.use_case

import com.example.coinkasa.data.local.entity.TransactionEntity
import com.example.coinkasa.domain.repository.TransactionRepository
import javax.inject.Inject

class InsertTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: TransactionEntity) {
        repository.insertTransaction(transaction)
    }
}