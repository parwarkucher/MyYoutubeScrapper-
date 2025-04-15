package com.parwar.myyoutubescrapper.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Video(
    val id: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val publishedAt: String,
    val channelTitle: String
) : Parcelable

@Parcelize
data class SearchResults(
    val videos: List<Video>,
    val captionsText: String,
    val shortSummary: String?,
    val detailedSummary: String?,
    val videoSummaries: String? = null
) : Parcelable 