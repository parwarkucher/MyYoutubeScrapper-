package com.parwar.myyoutubescrapper.data.remote

import com.parwar.myyoutubescrapper.data.model.openrouter.ChatCompletionRequest
import com.parwar.myyoutubescrapper.data.model.openrouter.ChatCompletionResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenRouterApiService {
    
    @POST("chat/completions")
    suspend fun getChatCompletions(
        @Header("Authorization") authorization: String,
        @Header("HTTP-Referer") httpReferer: String = "https://github.com/",
        @Header("X-Title") title: String = "YouTube Caption Summarizer Android App",
        @Body request: ChatCompletionRequest
    ): ChatCompletionResponse
} 