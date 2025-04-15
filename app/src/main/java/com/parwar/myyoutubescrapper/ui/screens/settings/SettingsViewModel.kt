package com.parwar.myyoutubescrapper.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parwar.myyoutubescrapper.data.model.openrouter.AvailableModels
import com.parwar.myyoutubescrapper.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    val availableModels = AvailableModels.models
    
    init {
        viewModelScope.launch {
            val youtubeApiKey = preferencesRepository.youtubeApiKey.first()
            val youtubeOAuthClientId = preferencesRepository.youtubeOAuthClientId.first()
            val openRouterApiKey = preferencesRepository.openRouterApiKey.first()
            val selectedModelId = preferencesRepository.selectedModelId.first()
            
            _uiState.value = SettingsUiState(
                youtubeApiKey = youtubeApiKey,
                youtubeOAuthClientId = youtubeOAuthClientId,
                openRouterApiKey = openRouterApiKey,
                selectedModelId = selectedModelId
            )
        }
    }
    
    fun updateYouTubeApiKey(apiKey: String) {
        _uiState.value = _uiState.value.copy(youtubeApiKey = apiKey)
    }
    
    fun updateYouTubeOAuthClientId(clientId: String) {
        _uiState.value = _uiState.value.copy(youtubeOAuthClientId = clientId)
    }
    
    fun updateOpenRouterApiKey(apiKey: String) {
        _uiState.value = _uiState.value.copy(openRouterApiKey = apiKey)
    }
    
    fun updateSelectedModel(modelId: String) {
        _uiState.value = _uiState.value.copy(selectedModelId = modelId)
    }
    
    fun saveSettings() {
        viewModelScope.launch {
            preferencesRepository.setYouTubeApiKey(_uiState.value.youtubeApiKey)
            preferencesRepository.setYouTubeOAuthClientId(_uiState.value.youtubeOAuthClientId)
            preferencesRepository.setOpenRouterApiKey(_uiState.value.openRouterApiKey)
            preferencesRepository.setSelectedModelId(_uiState.value.selectedModelId)
        }
    }
}

data class SettingsUiState(
    val youtubeApiKey: String = "",
    val youtubeOAuthClientId: String = "",
    val openRouterApiKey: String = "",
    val selectedModelId: String = "gpt-3.5-turbo"
) 