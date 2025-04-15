package com.parwar.myyoutubescrapper.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parwar.myyoutubescrapper.data.model.openrouter.AvailableModels
import com.parwar.myyoutubescrapper.data.repository.PreferencesRepository
import com.parwar.myyoutubescrapper.domain.model.SearchParameters
import com.parwar.myyoutubescrapper.domain.model.TimeFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    val availableTimeFilters = TimeFilter.values().toList()
    val availableModels = AvailableModels.models
    val maxResultsOptions = listOf(5, 10, 15, 20)
    
    init {
        viewModelScope.launch {
            // Load API keys and selected model
            val youtubeApiKey = preferencesRepository.youtubeApiKey.first()
            val youtubeOAuthClientId = preferencesRepository.youtubeOAuthClientId.first()
            val openRouterApiKey = preferencesRepository.openRouterApiKey.first()
            val selectedModelId = preferencesRepository.selectedModelId.first()
            
            _uiState.value = HomeUiState(
                youtubeApiKeySet = youtubeApiKey.isNotEmpty(),
                youtubeOAuthClientIdSet = youtubeOAuthClientId.isNotEmpty(),
                openRouterApiKeySet = openRouterApiKey.isNotEmpty(),
                selectedModelId = selectedModelId
            )
        }
    }
    
    fun updateQuery(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
    }
    
    fun updateTimeFilter(timeFilter: TimeFilter) {
        _uiState.value = _uiState.value.copy(selectedTimeFilter = timeFilter)
    }
    
    fun updateMaxResults(maxResults: Int) {
        _uiState.value = _uiState.value.copy(selectedMaxResults = maxResults)
    }
    
    fun updateSelectedModel(modelId: String) {
        viewModelScope.launch {
            preferencesRepository.setSelectedModelId(modelId)
            _uiState.value = _uiState.value.copy(selectedModelId = modelId)
        }
    }
    
    fun createSearchParameters(): SearchParameters {
        return SearchParameters(
            query = _uiState.value.query,
            timeFilter = _uiState.value.selectedTimeFilter,
            maxResults = _uiState.value.selectedMaxResults,
            modelId = _uiState.value.selectedModelId,
            useOAuth = _uiState.value.youtubeOAuthClientIdSet
        )
    }
}

data class HomeUiState(
    val query: String = "",
    val selectedTimeFilter: TimeFilter = TimeFilter.LAST_7_DAYS,
    val selectedMaxResults: Int = 10,
    val selectedModelId: String = "gpt-3.5-turbo",
    val youtubeApiKeySet: Boolean = false,
    val youtubeOAuthClientIdSet: Boolean = false,
    val openRouterApiKeySet: Boolean = false
) 