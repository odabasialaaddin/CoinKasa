package com.example.coinkasa.presentation.coin_list

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.coinkasa.core.base.BaseViewModel
import com.example.coinkasa.core.state.UiState
import com.example.coinkasa.domain.model.Coin
import com.example.coinkasa.domain.use_case.CoinUseCases
import com.example.coinkasa.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinListViewModel @Inject constructor(
    private val useCases: CoinUseCases
) : BaseViewModel() {

    private val _searchState = MutableStateFlow<UiState<List<Coin>>>(UiState.Idle)
    val searchState: StateFlow<UiState<List<Coin>>> = _searchState.asStateFlow()

    private var searchJob: Job? = null

    fun getCoins(): Flow<PagingData<Coin>> {
        return useCases.getCoins().cachedIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch(exceptionHandler) {
            delay(500L)
            if (query.isNotBlank()) {
                executeSearch(query)
            } else {
                _searchState.value = UiState.Idle
            }
        }
    }

    private fun executeSearch(query: String) {
        useCases.searchCoins(query).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _searchState.value = UiState.Success(result.data ?: emptyList())
                }
                is Resource.Error -> {
                    _searchState.value = UiState.Error(result.message ?: "Beklenmeyen bir hata oluştu")
                }
                is Resource.Loading -> {
                    _searchState.value = UiState.Loading
                }
            }
        }.launchIn(viewModelScope)
    }
}