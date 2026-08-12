package com.example.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object ImageFallbackFetcher {
    private val cache = mutableMapOf<String, String?>()

    suspend fun fetchPosterUrl(title: String, type: String): String? {
        if (cache.containsKey(title)) return cache[title]

        return withContext(Dispatchers.IO) {
            try {
                // Clean title by removing quality tags, years in parentheses, and extra symbols
                var cleanTitle = title.replace(Regex("(?i)(\\(?\\d{4}\\)?|HD|4K|FHD|1080p|720p|S\\d+E\\d+)"), "")
                    .replace("-", " ")
                    .replace("_", " ")
                    .trim()

                val encodedTitle = URLEncoder.encode(cleanTitle, "UTF-8")

                if (type == "SERIES") {
                    // Try TVMaze first for series
                    val tvmazeUrl = "https://api.tvmaze.com/search/shows?q=$encodedTitle"
                    val connection = URL(tvmazeUrl).openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.connectTimeout = 3000
                    connection.readTimeout = 3000
                    
                    if (connection.responseCode == 200) {
                        val response = connection.inputStream.bufferedReader().use { it.readText() }
                        val jsonArray = JSONArray(response)
                        if (jsonArray.length() > 0) {
                            val firstResult = jsonArray.getJSONObject(0).optJSONObject("show")
                            val image = firstResult?.optJSONObject("image")
                            val originalImg = image?.optString("original", "") ?: ""
                            if (originalImg.isNotEmpty()) {
                                cache[title] = originalImg
                                return@withContext originalImg
                            }
                        }
                    }
                }

                // Fallback to iTunes for Movies and Series
                val entity = if (type == "SERIES") "tvShow" else "movie"
                val itunesUrl = "https://itunes.apple.com/search?term=$encodedTitle&entity=$entity&limit=1"
                
                val connection = URL(itunesUrl).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 4000
                connection.readTimeout = 4000

                if (connection.responseCode == 200) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val json = JSONObject(response)
                    val results = json.optJSONArray("results")
                    if (results != null && results.length() > 0) {
                        val firstResult = results.getJSONObject(0)
                        val artworkUrl = firstResult.optString("artworkUrl100", "")
                        if (artworkUrl.isNotEmpty()) {
                            // Upgrade resolution for iTunes
                            val highResUrl = artworkUrl.replace("100x100bb", "600x600bb")
                            cache[title] = highResUrl
                            return@withContext highResUrl
                        }
                    }
                }

                cache[title] = null
                null
            } catch (e: Exception) {
                null
            }
        }
    }
}
