package com.parwar.myyoutubescrapper.data.source

import android.util.Log
import com.parwar.myyoutubescrapper.data.model.youtube.CaptionTrack
import com.parwar.myyoutubescrapper.data.model.youtube.YouTubeCaptionListResponse
import com.parwar.myyoutubescrapper.data.model.youtube.YouTubeSearchResponse
import com.parwar.myyoutubescrapper.data.remote.YouTubeApiService
import com.parwar.myyoutubescrapper.domain.model.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONObject
import org.jsoup.Jsoup
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import javax.inject.Inject

class YouTubeDataSource @Inject constructor(
    private val youTubeApiService: YouTubeApiService
) {
    suspend fun searchVideos(
        query: String,
        maxResults: Int,
        publishedAfter: String?,
        apiKey: String
    ): Result<YouTubeSearchResponse> {
        return try {
            Log.d("YouTubeDataSource", "Searching with query='$query', maxResults=$maxResults, publishedAfter=$publishedAfter")
            
            val response = youTubeApiService.searchVideos(
                maxResults = maxResults,
                query = query,
                publishedAfter = publishedAfter,
                apiKey = apiKey
            )
            
            Log.d("YouTubeDataSource", "Search response received with ${response.items.size} items")
            Result.Success(response)
        } catch (e: Exception) {
            Log.e("YouTubeDataSource", "Error searching videos", e)
            Result.Error(e)
        }
    }
    
    suspend fun getCaptions(
        videoId: String,
        apiKey: String,
        oAuthClientId: String? = null
    ): Result<YouTubeCaptionListResponse> {
        return try {
            val response = if (oAuthClientId != null && oAuthClientId.isNotEmpty()) {
                // OAuth access for private captions (would need full OAuth 2.0 implementation)
                youTubeApiService.getCaptions(
                    videoId = videoId,
                    apiKey = apiKey
                )
            } else {
                // Regular API key access for public captions
                youTubeApiService.getCaptions(
                    videoId = videoId,
                    apiKey = apiKey
                )
            }
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Primary method for extracting captions using JSoup HTML parsing.
     * This approach is similar to MySummarizer implementation and extracts captions
     * directly from the YouTube video page.
     */
    suspend fun extractCaptionsWithJsoup(videoId: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d("YouTubeDataSource", "Fetching captions for video ID: $videoId with JSoup")
            val videoUrl = "https://www.youtube.com/watch?v=$videoId"
            val doc = Jsoup.connect(videoUrl)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .get()

            Log.d("YouTubeDataSource", "Successfully fetched HTML for video")

            // Find and extract captions
            val scripts = doc.select("script")
            var captionsText = ""

            Log.d("YouTubeDataSource", "Found ${scripts.size} script tags")

            for (script in scripts) {
                val content = script.html()
                if (content.contains("\"playerCaptionsTracklistRenderer\"")) {
                    Log.d("YouTubeDataSource", "Found script with captions")
                    try {
                        val jsonStr = content.substringAfter("\"captions\":{").substringBefore("\"videoDetails\"")
                        val captionsJson = JSONObject("{$jsonStr}")
                        val captionTracks = captionsJson.getJSONObject("playerCaptionsTracklistRenderer")
                            .getJSONArray("captionTracks")

                        if (captionTracks.length() > 0) {
                            val baseUrl = captionTracks.getJSONObject(0).getString("baseUrl")
                            val captionsResponse = Jsoup.connect(baseUrl)
                                .ignoreContentType(true)
                                .execute()
                            captionsText = captionsResponse.body()

                            // Clean up XML captions
                            captionsText = captionsText.replace(Regex("<[^>]*>"), " ")
                                .replace("&amp;", "&")
                                .replace("&quot;", "\"")
                                .replace("&#39;", "'")
                                .replace("  ", " ")
                                .trim()

                            Log.d("YouTubeDataSource", "Successfully extracted captions")
                            break
                        }
                    } catch (e: Exception) {
                        Log.e("YouTubeDataSource", "Error parsing captions JSON", e)
                    }
                }
            }

            if (captionsText.isEmpty()) {
                Log.d("YouTubeDataSource", "No captions found with JSoup method, trying fallback methods")
                return@withContext Result.Error(Exception("No captions found with JSoup method"))
            }

            Result.Success(captionsText)
        } catch (e: Exception) {
            Log.e("YouTubeDataSource", "Failed to fetch captions with JSoup", e)
            Result.Error(e)
        }
    }
    
    suspend fun downloadCaptionContent(
        videoId: String,
        apiKey: String,
        language: String = "en"
    ): Result<String> {
        // Try JSoup method first (this is the approach used in MySummarizer)
        val jsoupResult = extractCaptionsWithJsoup(videoId)
        if (jsoupResult is Result.Success && jsoupResult.data.isNotBlank()) {
            return jsoupResult
        }
        
        // Fall back to our other methods if JSoup approach fails
        return try {
            // 1. First try standard captions
            val captionText = tryStandardCaptions(videoId, language)
            if (captionText.isNotBlank()) {
                return Result.Success(captionText)
            }
            
            // 2. Try auto-generated captions if standard ones aren't available
            val autoCaptionText = tryAutoGeneratedCaptions(videoId, language)
            if (autoCaptionText.isNotBlank()) {
                return Result.Success(autoCaptionText)
            }
            
            // 3. If all methods fail, return an empty string
            Result.Success("")
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    private suspend fun tryStandardCaptions(videoId: String, language: String): String {
        try {
            // YouTube's unofficial caption API for standard captions
            val url = "https://www.youtube.com/api/timedtext?lang=$language&v=$videoId&fmt=json3"
            val response = youTubeApiService.downloadCaptionTrack(url)
            val jsonContent = response.string()
            return parseJson3Captions(jsonContent)
        } catch (e: Exception) {
            try {
                // Try XML format as fallback
                val xmlUrl = "https://www.youtube.com/api/timedtext?lang=$language&v=$videoId"
                val xmlResponse = youTubeApiService.downloadCaptionTrack(xmlUrl)
                return parseXmlCaptions(xmlResponse)
            } catch (e2: Exception) {
                // Return empty string if all methods fail
                return ""
            }
        }
    }
    
    private suspend fun tryAutoGeneratedCaptions(videoId: String, language: String): String {
        try {
            // Try auto-generated captions (the &kind=asr parameter specifies auto-generated)
            val url = "https://www.youtube.com/api/timedtext?lang=$language&v=$videoId&kind=asr&fmt=json3"
            val response = youTubeApiService.downloadCaptionTrack(url)
            val jsonContent = response.string()
            return parseJson3Captions(jsonContent)
        } catch (e: Exception) {
            try {
                // Try XML format for auto-generated captions
                val xmlUrl = "https://www.youtube.com/api/timedtext?lang=$language&v=$videoId&kind=asr"
                val xmlResponse = youTubeApiService.downloadCaptionTrack(xmlUrl)
                return parseXmlCaptions(xmlResponse)
            } catch (e2: Exception) {
                // Return empty string if all methods fail
                return ""
            }
        }
    }
    
    suspend fun downloadCaptionTrack(
        captionTrack: CaptionTrack,
        apiKey: String,
        authorization: String? = null
    ): Result<String> {
        return try {
            // YouTube doesn't provide direct access to caption content via the official API
            // We need to construct a URL to download the caption track
            // This is undocumented and might change in the future
            val trackId = captionTrack.id
            val url = "https://www.googleapis.com/youtube/v3/captions/$trackId?key=$apiKey"
            
            val response = youTubeApiService.downloadCaptionTrack(
                url = url,
                authorization = authorization
            )
            
            val captionText = parseXmlCaptions(response)
            Result.Success(captionText)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    private fun parseXmlCaptions(responseBody: ResponseBody): String {
        val xmlContent = responseBody.string()
        val parser = XmlPullParserFactory.newInstance().newPullParser()
        parser.setInput(StringReader(xmlContent))
        
        val captionTextBuilder = StringBuilder()
        var eventType = parser.eventType
        
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && parser.name == "text") {
                val text = parser.nextText()
                captionTextBuilder.append(text.trim()).append(" ")
            }
            eventType = parser.next()
        }
        
        return captionTextBuilder.toString().trim()
    }
    
    // Parse JSON3 format captions (YouTube's newer format)
    private fun parseJson3Captions(jsonContent: String): String {
        val captionTextBuilder = StringBuilder()
        
        // Simple parsing approach using regex
        // In a production app, you'd use a proper JSON parser
        val pattern = """"text":"(.*?)"""".toRegex()
        val matches = pattern.findAll(jsonContent)
        
        for (match in matches) {
            val captionText = match.groupValues[1]
                .replace("\\n", " ")
                .replace("\\\"", "\"")
                .replace("\\r", "")
                .replace("\\\\", "\\")
            
            captionTextBuilder.append(captionText).append(" ")
        }
        
        return captionTextBuilder.toString().trim()
    }
} 