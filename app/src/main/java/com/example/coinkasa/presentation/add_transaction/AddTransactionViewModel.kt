package com.example.coinkasa.presentation.add_transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coinkasa.data.local.entity.TransactionEntity
import com.example.coinkasa.domain.use_case.CoinUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val coinUseCases: CoinUseCases
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun saveTransaction(
        coinId: String,
        coinName: String,
        coinSymbol: String,
        transactionType: String,
        amountStr: String,
        priceStr: String,
        exchangeName: String,
        dateMillis: Long?
    ) {
        val amount = try { BigDecimal(amountStr) } catch (e: Exception) { BigDecimal.ZERO }
        val price = try { BigDecimal(priceStr) } catch (e: Exception) { BigDecimal.ZERO }

        if (amount <= BigDecimal.ZERO) {
            emitEvent(UiEvent.ShowError("Geçerli bir miktar giriniz"))
            return
        }

        if (price <= BigDecimal.ZERO) {
            emitEvent(UiEvent.ShowError("Geçerli bir fiyat giriniz"))
            return
        }

        if (exchangeName.isBlank()) {
            emitEvent(UiEvent.ShowError("Borsa adı boş olamaz"))
            return
        }

        if (dateMillis == null || dateMillis == 0L) {
            emitEvent(UiEvent.ShowError("Lütfen bir tarih seçiniz"))
            return
        }

        viewModelScope.launch {
            try {
                val transaction = TransactionEntity(
                    coinId = coinId,
                    coinName = coinName,
                    coinSymbol = coinSymbol,
                    transactionType = transactionType,
                    exchangeName = exchangeName,
                    amount = amount.toPlainString(),
                    pricePerCoin = price.toPlainString(),
                    dateMillis = dateMillis
                )
                coinUseCases.insertTransaction(transaction)
                _uiEvent.emit(UiEvent.SaveSuccess)
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowError("Kaydedilirken beklenmeyen bir hata oluştu"))
            }
        }
    }

    private fun emitEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }

    sealed class UiEvent {
        object SaveSuccess : UiEvent()
        data class ShowError(val message: String) : UiEvent()
    }
}