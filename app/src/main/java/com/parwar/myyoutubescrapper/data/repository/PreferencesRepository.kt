package com.parwar.myyoutubescrapper.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class PreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore
    
    companion object {
        private val YOUTUBE_API_KEY = stringPreferencesKey("youtube_api_key")
        private val YOUTUBE_OAUTH_CLIENT_ID = stringPreferencesKey("youtube_oauth_client_id")
        private val OPENROUTER_API_KEY = stringPreferencesKey("openrouter_api_key")
        private val SELECTED_MODEL_ID = stringPreferencesKey("selected_model_id")
    }
    
    val youtubeApiKey: Flow<String> = dataStore.data.map { preferences ->
        preferences[YOUTUBE_API_KEY] ?: ""
    }

    val youtubeOAuthClientId: Flow<String> = dataStore.data.map { preferences ->
        preferences[YOUTUBE_OAUTH_CLIENT_ID] ?: ""
    }
    
    val openRouterApiKey: Flow<String> = dataStore.data.map { preferences ->
        preferences[OPENROUTER_API_KEY] ?: ""
    }
    
    val selectedModelId: Flow<String> = dataStore.data.map { preferences ->
        preferences[SELECTED_MODEL_ID] ?: "gpt-3.5-turbo" // Default model
    }
    
    suspend fun setYouTubeApiKey(apiKey: String) {
        dataStore.edit { preferences ->
            preferences[YOUTUBE_API_KEY] = apiKey
        }
    }

    suspend fun setYouTubeOAuthClientId(clientId: String) {
        dataStore.edit { preferences ->
            preferences[YOUTUBE_OAUTH_CLIENT_ID] = clientId
        }
    }
    
    suspend fun setOpenRouterApiKey(apiKey: String) {
        dataStore.edit { preferences ->
            preferences[OPENROUTER_API_KEY] = apiKey
        }
    }
    
    suspend fun setSelectedModelId(modelId: String) {
        dataStore.edit { preferences ->
            preferences[SELECTED_MODEL_ID] = modelId
        }
    }
} 