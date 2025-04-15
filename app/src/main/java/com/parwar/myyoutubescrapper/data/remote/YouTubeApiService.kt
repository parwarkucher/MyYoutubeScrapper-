package com.parwar.myyoutubescrapper.data.remote

import com.parwar.myyoutubescrapper.data.model.youtube.YouTubeCaptionListResponse
import com.parwar.myyoutubescrapper.data.model.youtube.YouTubeSearchResponse
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.http.Url

interface YouTubeApiService {
    
    @GET("youtube/v3/search")
    suspend fun searchVideos(
        @Query("part") part: String = "snippet",
        @Query("maxResults") maxResults: Int,
        @Query("q") query: String,
        @Query("type") type: String = "video",
        @Query("publishedAfter") publishedAfter: String? = null,
        @Query("key") apiKey: String
    ): YouTubeSearchResponse
    
    @GET("youtube/v3/captions")
    suspend fun getCaptions(
        @Query("part") part: String = "snippet",
        @Query("videoId") videoId: String,
        @Query("key") apiKey: String
    ): YouTubeCaptionListResponse
    
    @GET("youtube/v3/timedtext")
    suspend fun getCaptionContent(
        @Query("lang") language: String = "en",
        @Query("v") videoId: String,
        @Query("fmt") format: String = "srv1",  // srv1 is XML format
        @Query("key") apiKey: String
    ): ResponseBody
    
    // Alternative method to download captions using YouTube's undocumented API
    @GET
    suspend fun downloadCaptionTrack(
        @Url url: String,
        @Header("Authorization") authorization: String? = null
    ): ResponseBody
} 