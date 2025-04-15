package com.parwar.myyoutubescrapper.ui.screens.results

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parwar.myyoutubescrapper.data.repository.PreferencesRepository
import com.parwar.myyoutubescrapper.domain.model.Result
import com.parwar.myyoutubescrapper.domain.model.SearchParameters
import com.parwar.myyoutubescrapper.domain.model.SearchResults
import com.parwar.myyoutubescrapper.domain.model.TimeFilter
import com.parwar.myyoutubescrapper.domain.model.Video
import com.parwar.myyoutubescrapper.domain.usecase.SearchAndSummarizeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultsViewModel @Inject constructor(
    private val searchAndSummarizeUseCase: SearchAndSummarizeUseCase,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ResultsUiState>(ResultsUiState.Loading)
    val uiState: StateFlow<ResultsUiState> = _uiState.asStateFlow()
    
    private val _searchParameters = MutableStateFlow(
        SearchParameters(
            query = "",
            timeFilter = TimeFilter.LAST_7_DAYS,
            maxResults = 10,
            modelId = "gpt-3.5-turbo"
        )
    )
    val searchParameters: StateFlow<SearchParameters> = _searchParameters.asStateFlow()
    
    init {
        viewModelScope.launch {
            // Get the selected model from preferences
            val selectedModelId = preferencesRepository.selectedModelId.first()
            _searchParameters.value = _searchParameters.value.copy(modelId = selectedModelId)
        }
    }
    
    fun setSearchParameters(searchParameters: SearchParameters) {
        _searchParameters.value = searchParameters
    }
    
    fun setQuery(query: String) {
        _searchParameters.value = _searchParameters.value.copy(query = query)
    }
    
    fun setSelectedModelId(modelId: String) {
        _searchParameters.value = _searchParameters.value.copy(modelId = modelId)
    }
    
    fun setMaxResults(maxResults: Int) {
        _searchParameters.value = _searchParameters.value.copy(maxResults = maxResults)
    }
    
    fun setTimeFilter(timeFilterName: String) {
        try {
            val timeFilter = com.parwar.myyoutubescrapper.domain.model.TimeFilter.valueOf(timeFilterName)
            _searchParameters.value = _searchParameters.value.copy(timeFilter = timeFilter)
            Log.d("ResultsViewModel", "Time filter set to: $timeFilter")
        } catch (e: Exception) {
            Log.e("ResultsViewModel", "Error setting time filter: $timeFilterName", e)
        }
    }
    
    fun startSearch() {
        _uiState.value = ResultsUiState.Loading
        
        viewModelScope.launch {
            val result = searchAndSummarizeUseCase(_searchParameters.value)
            
            _uiState.value = when (result) {
                is Result.Success -> ResultsUiState.Success(result.data)
                is Result.Error -> ResultsUiState.Error(result.exception.message ?: "Unknown error occurred")
                is Result.Loading -> ResultsUiState.Loading
            }
        }
    }
}

sealed class ResultsUiState {
    object Loading : ResultsUiState()
    data class Success(val searchResults: SearchResults) : ResultsUiState()
    data class Error(val message: String) : ResultsUiState()
} 