package com.example.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

object ArtworkFetcher {
    private val validCache = mutableMapOf<String, Boolean>()

    suspend fun getValidImageUrl(primaryUrl: String, title: String, type: String): String? {
        if (primaryUrl.isEmpty()) {
            return ImageFallbackFetcher.fetchPosterUrl(title, type)
        }

        // Check if we already validated this URL
        if (validCache.containsKey(primaryUrl)) {
            if (validCache[primaryUrl] == true) {
                return primaryUrl
            } else {
                return ImageFallbackFetcher.fetchPosterUrl(title, type)
            }
        }

        return withContext(Dispatchers.IO) {
            try {
                val url = URL(primaryUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "HEAD" // Just check headers, faster
                connection.connectTimeout = 3000
                connection.readTimeout = 3000
                
                val code = connection.responseCode
                if (code in 200..299 || code in 300..399) {
                    validCache[primaryUrl] = true
                    primaryUrl
                } else {
                    validCache[primaryUrl] = false
                    ImageFallbackFetcher.fetchPosterUrl(title, type)
                }
            } catch (e: Exception) {
                validCache[primaryUrl] = false
                ImageFallbackFetcher.fetchPosterUrl(title, type)
            }
        }
    }
}
