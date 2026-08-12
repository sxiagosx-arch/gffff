package com.example.model

import java.io.Serializable

data class IPTVChannel(
    val id: String,
    val name: String,
    val url: String,
    val logo: String = "",
    val categoryId: String = "",
    val categoryName: String = "",
    val type: String = "LIVE", // "LIVE", "MOVIE", "SERIES"
    val resolution: String = "HD",
    val epgTitle: String = "",
    val epgNextTitle: String = "",
    val description: String = "",
    val director: String = "",
    val cast: String = "",
    val duration: String = "",
    val year: String = "",
    val rating: String = "",
    val seriesId: String = "",
    val seasonNumber: Int = 0,
    val episodeNumber: Int = 0
) : Serializable

data class IPTVCategory(
    val id: String,
    val name: String,
    val type: String = "LIVE" // "LIVE", "MOVIE", "SERIES"
) : Serializable

data class IPTVSeries(
    val id: String,
    val name: String,
    val cover: String = "",
    val categoryId: String = "",
    val categoryName: String = "",
    val rating: String = "",
    val year: String = "",
    val cast: String = "",
    val director: String = "",
    val plot: String = ""
) : Serializable

data class IPTVSeason(
    val number: Int,
    val episodes: List<IPTVChannel>
) : Serializable


data class EPGProgram(
    val id: String,
    val title: String,
    val description: String,
    val start: String,
    val end: String,
    val startTimestamp: Long,
    val stopTimestamp: Long
) : java.io.Serializable
