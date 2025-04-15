package com.parwar.myyoutubescrapper.domain.usecase

import android.util.Log
import com.parwar.myyoutubescrapper.data.repository.OpenRouterRepository
import com.parwar.myyoutubescrapper.data.repository.PreferencesRepository
import com.parwar.myyoutubescrapper.data.repository.YouTubeRepository
import com.parwar.myyoutubescrapper.domain.model.Result
import com.parwar.myyoutubescrapper.domain.model.SearchParameters
import com.parwar.myyoutubescrapper.domain.model.SearchResults
import com.parwar.myyoutubescrapper.domain.model.Video
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SearchAndSummarizeUseCase @Inject constructor(
    private val youTubeRepository: YouTubeRepository,
    private val openRouterRepository: OpenRouterRepository,
    private val preferencesRepository: PreferencesRepository
) {
    suspend operator fun invoke(searchParameters: SearchParameters): Result<SearchResults> {
        try {
            // Get API keys
            val youtubeApiKey = preferencesRepository.youtubeApiKey.first()
            val openRouterApiKey = preferencesRepository.openRouterApiKey.first()
            
            // Log search parameters to debug
            Log.d("SearchUseCase", "Search Parameters: query=${searchParameters.query}, " +
                    "maxResults=${searchParameters.maxResults}, " +
                    "timeFilter=${searchParameters.timeFilter}, " +
                    "modelId=${searchParameters.modelId}")
            
            if (youtubeApiKey.isEmpty()) {
                return Result.Error(Exception("YouTube API key is not set"))
            }
            
            if (openRouterApiKey.isEmpty()) {
                return Result.Error(Exception("OpenRouter API key is not set"))
            }
            
            // Step 1: Search for videos
            val videosResult = youTubeRepository.searchVideos(searchParameters, youtubeApiKey)
            
            if (videosResult !is Result.Success) {
                return videosResult as Result<SearchResults>
            }
            
            // Ensure we only use the requested number of videos by slicing the result
            val allVideos = videosResult.data
            val videos = if (allVideos.size > searchParameters.maxResults) {
                Log.d("SearchUseCase", "Limiting videos from ${allVideos.size} to ${searchParameters.maxResults}")
                allVideos.take(searchParameters.maxResults)
            } else {
                allVideos
            }
            
            Log.d("SearchUseCase", "Found ${videos.size} videos after applying maxResults limit")
            
            if (videos.isEmpty()) {
                return Result.Success(
                    SearchResults(
                        videos = emptyList(),
                        captionsText = "",
                        shortSummary = null,
                        detailedSummary = null
                    )
                )
            }
            
            // Step 2: Fetch captions for each video
            val allCaptions = StringBuilder()
            
            for (video in videos) {
                when (val captionResult = youTubeRepository.getVideoCaption(
                    video.id, 
                    youtubeApiKey,
                    useOAuth = searchParameters.useOAuth
                )) {
                    is Result.Success -> {
                        allCaptions.append("=== Video: ${video.title} ===\n")
                        allCaptions.append(captionResult.data)
                        allCaptions.append("\n\n")
                    }
                    else -> {
                        // Skip if captions are not available or there's an error
                        continue
                    }
                }
            }
            
            val captionsText = allCaptions.toString().trim()
            
            if (captionsText.isEmpty()) {
                return Result.Success(
                    SearchResults(
                        videos = videos,
                        captionsText = "",
                        shortSummary = "No captions available for the selected videos.",
                        detailedSummary = "The selected videos don't have available captions to summarize."
                    )
                )
            }
            
            // Log before sending to OpenRouter
            Log.d("SearchUseCase", "Sending to ${searchParameters.modelId} for summarization")
            
            // Step 3: Send to OpenRouter for summarization
            val summaryResult = openRouterRepository.summarizeText(
                text = captionsText,
                modelId = searchParameters.modelId,
                apiKey = openRouterApiKey
            )
            
            return when (summaryResult) {
                is Result.Success -> {
                    val (shortSummary, detailedSummary, videoSummaries) = summaryResult.data
                    Result.Success(
                        SearchResults(
                            videos = videos,
                            captionsText = captionsText,
                            shortSummary = shortSummary,
                            detailedSummary = detailedSummary,
                            videoSummaries = videoSummaries
                        )
                    )
                }
                is Result.Error -> {
                    Result.Success(
                        SearchResults(
                            videos = videos,
                            captionsText = captionsText,
                            shortSummary = "Error generating summary: ${summaryResult.exception.message}",
                            detailedSummary = null,
                            videoSummaries = null
                        )
                    )
                }
                is Result.Loading -> Result.Loading
            }
            
        } catch (e: Exception) {
            Log.e("SearchUseCase", "Error during search and summarize", e)
            return Result.Error(e)
        }
    }
} 