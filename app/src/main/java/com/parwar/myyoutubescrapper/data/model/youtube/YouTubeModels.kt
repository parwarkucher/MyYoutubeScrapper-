package com.parwar.myyoutubescrapper.data.model.youtube

import com.google.gson.annotations.SerializedName

// Search response model
data class YouTubeSearchResponse(
    @SerializedName("kind")
    val kind: String,
    @SerializedName("etag")
    val etag: String,
    @SerializedName("nextPageToken")
    val nextPageToken: String?,
    @SerializedName("regionCode")
    val regionCode: String?,
    @SerializedName("pageInfo")
    val pageInfo: PageInfo,
    @SerializedName("items")
    val items: List<SearchItem>
)

data class PageInfo(
    @SerializedName("totalResults")
    val totalResults: Int,
    @SerializedName("resultsPerPage")
    val resultsPerPage: Int
)

data class SearchItem(
    @SerializedName("kind")
    val kind: String,
    @SerializedName("etag")
    val etag: String,
    @SerializedName("id")
    val id: Id,
    @SerializedName("snippet")
    val snippet: Snippet
)

data class Id(
    @SerializedName("kind")
    val kind: String,
    @SerializedName("videoId")
    val videoId: String?,
    @SerializedName("channelId")
    val channelId: String?,
    @SerializedName("playlistId")
    val playlistId: String?
)

data class Snippet(
    @SerializedName("publishedAt")
    val publishedAt: String,
    @SerializedName("channelId")
    val channelId: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("thumbnails")
    val thumbnails: Thumbnails,
    @SerializedName("channelTitle")
    val channelTitle: String,
    @SerializedName("liveBroadcastContent")
    val liveBroadcastContent: String
)

data class Thumbnails(
    @SerializedName("default")
    val default: Thumbnail,
    @SerializedName("medium")
    val medium: Thumbnail,
    @SerializedName("high")
    val high: Thumbnail
)

data class Thumbnail(
    @SerializedName("url")
    val url: String,
    @SerializedName("width")
    val width: Int,
    @SerializedName("height")
    val height: Int
)

// Captions response model
data class YouTubeCaptionListResponse(
    @SerializedName("kind")
    val kind: String,
    @SerializedName("etag")
    val etag: String,
    @SerializedName("items")
    val items: List<CaptionTrack>
)

data class CaptionTrack(
    @SerializedName("kind")
    val kind: String,
    @SerializedName("etag")
    val etag: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("snippet")
    val snippet: CaptionSnippet
)

data class CaptionSnippet(
    @SerializedName("videoId")
    val videoId: String,
    @SerializedName("lastUpdated")
    val lastUpdated: String,
    @SerializedName("trackKind")
    val trackKind: String,
    @SerializedName("language")
    val language: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("audioTrackType")
    val audioTrackType: String,
    @SerializedName("isCC")
    val isCC: Boolean,
    @SerializedName("isAutoSynced")
    val isAutoSynced: Boolean,
    @SerializedName("status")
    val status: String
) 