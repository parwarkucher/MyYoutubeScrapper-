package com.parwar.myyoutubescrapper.data.repository

import com.parwar.myyoutubescrapper.data.source.YouTubeDataSource
import com.parwar.myyoutubescrapper.domain.model.Result
import com.parwar.myyoutubescrapper.domain.model.SearchParameters
import com.parwar.myyoutubescrapper.domain.model.Video
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class YouTubeRepository @Inject constructor(
    private val youTubeDataSource: YouTubeDataSource,
    private val preferencesRepository: com.parwar.myyoutubescrapper.data.repository.PreferencesRepository
) {
    suspend fun searchVideos(
        searchParameters: SearchParameters,
        apiKey: String
    ): Result<List<Video>> {
        return when (val response = youTubeDataSource.searchVideos(
            query = searchParameters.query,
            maxResults = searchParameters.maxResults,
            publishedAfter = searchParameters.timeFilter.toPublishedAfterDate(),
            apiKey = apiKey
        )) {
            is Result.Success -> {
                val videos = response.data.items.mapNotNull { item ->
                    item.id.videoId?.let { videoId ->
                        Video(
                            id = videoId,
                            title = item.snippet.title,
                            description = item.snippet.description,
                            thumbnailUrl = item.snippet.thumbnails.high.url,
                            publishedAt = item.snippet.publishedAt,
                            channelTitle = item.snippet.channelTitle
                        )
                    }
                }
                Result.Success(videos)
            }
            is Result.Error -> Result.Error(response.exception)
            is Result.Loading -> Result.Loading
        }
    }
    
    suspend fun getVideoCaption(
        videoId: String,
        apiKey: String,
        useOAuth: Boolean = false
    ): Result<String> {
        // Try direct caption download first (more reliable for most videos)
        val directResult = tryDirectCaptionDownload(videoId, apiKey)
        
        if (directResult is Result.Success && directResult.data.isNotBlank() && 
            !directResult.data.startsWith("Could not retrieve captions")) {
            return directResult
        }
        
        // If direct download fails or returns empty captions, try the official API
        // Get OAuth token if needed
        val oAuthToken = if (useOAuth) {
            preferencesRepository.youtubeOAuthClientId.first()
        } else {
            null
        }
        
        // Try to get captions list from official API
        return when (val captionsListResult = youTubeDataSource.getCaptions(
            videoId = videoId,
            apiKey = apiKey,
            oAuthClientId = oAuthToken
        )) {
            is Result.Success -> {
                val captionsList = captionsListResult.data
                if (captionsList.items.isNotEmpty()) {
                    // Sort caption tracks with preference for English
                    val sortedCaptions = captionsList.items.sortedWith(
                        compareBy(
                            { it.snippet.language != "en" }, // Prefer English captions
                            { !it.snippet.isAutoSynced },   // Prefer manual captions over auto-generated
                            { it.snippet.trackKind != "standard" } // Prefer standard tracks
                        )
                    )
                    
                    // Try to download the first available caption track
                    val captionTrack = sortedCaptions.first()
                    val authorization = if (useOAuth && !oAuthToken.isNullOrEmpty()) {
                        "Bearer $oAuthToken"
                    } else {
                        null
                    }
                    
                    when (val downloadResult = youTubeDataSource.downloadCaptionTrack(
                        captionTrack = captionTrack,
                        apiKey = apiKey,
                        authorization = authorization
                    )) {
                        is Result.Success -> {
                            if (downloadResult.data.isNotBlank()) {
                                Result.Success(downloadResult.data)
                            } else {
                                // If we got empty captions, return the direct result
                                directResult
                            }
                        }
                        is Result.Error -> {
                            // Return the direct result we already tried
                            directResult
                        }
                        is Result.Loading -> Result.Loading
                    }
                } else {
                    // No caption tracks found in the API, return the direct result
                    directResult
                }
            }
            is Result.Error -> {
                // If getting caption list fails, return the direct result
                directResult
            }
            is Result.Loading -> Result.Loading
        }
    }
    
    private suspend fun tryDirectCaptionDownload(
        videoId: String,
        apiKey: String
    ): Result<String> {
        return when (val directDownloadResult = youTubeDataSource.downloadCaptionContent(
            videoId = videoId,
            apiKey = apiKey
        )) {
            is Result.Success -> {
                if (directDownloadResult.data.isBlank()) {
                    Result.Success(
                        "Could not retrieve captions for video $videoId. " +
                        "The video may not have captions available or they may be disabled by the creator."
                    )
                } else {
                    Result.Success(directDownloadResult.data)
                }
            }
            is Result.Error -> {
                Result.Success(
                    "Could not retrieve captions for video $videoId. " +
                    "The video may not have captions available or they may be disabled by the creator."
                )
            }
            is Result.Loading -> Result.Loading
        }
    }
} 