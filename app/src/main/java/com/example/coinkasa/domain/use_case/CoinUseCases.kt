package com.example.coinkasa.domain.use_case

data class CoinUseCases(
    val getCoins: GetCoinsUseCase,
    val searchCoins: SearchCoinsUseCase,
    val insertTransaction: InsertTransactionUseCase,
    val deleteTransaction: DeleteTransactionUseCase,
    val getTransactions: GetTransactionsUseCase,
    val getTransactionsByCoinId: GetTransactionsByCoinIdUseCase,
    val calculateProfitLoss: CalculateProfitLossUseCase
)
