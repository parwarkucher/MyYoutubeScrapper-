package com.parwar.myyoutubescrapper.data.repository

import com.parwar.myyoutubescrapper.data.source.OpenRouterDataSource
import com.parwar.myyoutubescrapper.domain.model.Result
import javax.inject.Inject

class OpenRouterRepository @Inject constructor(
    private val openRouterDataSource: OpenRouterDataSource
) {
    suspend fun summarizeText(
        text: String,
        modelId: String,
        apiKey: String
    ): Result<Triple<String, String, String>> {
        return when (val response = openRouterDataSource.summarizeText(
            text = text,
            modelId = modelId,
            apiKey = apiKey
        )) {
            is Result.Success -> {
                val content = response.data.choices.firstOrNull()?.message?.content ?: ""
                
                // Parse the response to extract summaries
                val shortSummary = extractShortSummary(content)
                val detailedSummary = extractDetailedSummary(content)
                val videoSummaries = extractVideoSummaries(content)
                
                Result.Success(Triple(shortSummary, detailedSummary, videoSummaries))
            }
            is Result.Error -> Result.Error(response.exception)
            is Result.Loading -> Result.Loading
        }
    }
    
    private fun extractShortSummary(content: String): String {
        val startMarker = "SHORT SUMMARY:"
        val endMarker = "DETAILED SUMMARY:"
        
        return if (content.contains(startMarker)) {
            val start = content.indexOf(startMarker) + startMarker.length
            val end = if (content.contains(endMarker)) content.indexOf(endMarker) else content.length
            content.substring(start, end).trim()
        } else {
            "No short summary available"
        }
    }
    
    private fun extractDetailedSummary(content: String): String {
        val startMarker = "DETAILED SUMMARY:"
        val endMarker = "VIDEO SUMMARIES:"
        
        return if (content.contains(startMarker)) {
            val start = content.indexOf(startMarker) + startMarker.length
            val end = if (content.contains(endMarker)) content.indexOf(endMarker) else content.length
            content.substring(start, end).trim()
        } else {
            "No detailed summary available"
        }
    }
    
    private fun extractVideoSummaries(content: String): String {
        val marker = "VIDEO SUMMARIES:"
        
        return if (content.contains(marker)) {
            content.substring(content.indexOf(marker) + marker.length).trim()
        } else {
            "Individual video summaries not available"
        }
    }
} 