package com.parwar.myyoutubescrapper.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Parcelize
data class SearchParameters(
    val query: String,
    val timeFilter: TimeFilter,
    val maxResults: Int,
    val modelId: String,
    val useOAuth: Boolean = false
) : Parcelable

enum class TimeFilter(val displayName: String, val daysBack: Int?) {
    LAST_24_HOURS("Last 24 hours", 1),
    LAST_7_DAYS("Last 7 days", 7),
    LAST_30_DAYS("Last 30 days", 30),
    LAST_YEAR("Last year", 365),
    ANY_TIME("Any time", null);
    
    fun toPublishedAfterDate(): String? {
        return daysBack?.let {
            val date = LocalDateTime.now().minusDays(it.toLong())
            date.atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME)
        }
    }
} 