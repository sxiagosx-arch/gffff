package com.example.network

import com.example.model.IPTVCategory
import com.example.model.IPTVChannel
import java.io.BufferedReader
import java.io.StringReader

object IPTVParser {
    fun parseM3U(file: java.io.File): List<IPTVChannel> {
        val channels = mutableListOf<IPTVChannel>()
        val reader = java.io.BufferedReader(java.io.FileReader(file))
        var line: String?
        var currentChannelMetadata: ExtInfMetadata? = null

        try {
            while (reader.readLine().also { line = it } != null) {
                val trimmed = line?.trim() ?: continue
                if (trimmed.isEmpty()) continue

                if (trimmed.startsWith("#EXTINF:")) {
                    currentChannelMetadata = parseExtInf(trimmed)
                } else if (!trimmed.startsWith("#") && trimmed.contains("://")) {
                    // This is a stream URL
                    if (currentChannelMetadata != null) {
                        channels.add(
                            IPTVChannel(
                                id = currentChannelMetadata.tvgId.ifEmpty { currentChannelMetadata.name.hashCode().toString() },
                                name = currentChannelMetadata.name,
                                url = trimmed,
                                logo = currentChannelMetadata.logo,
                                categoryId = currentChannelMetadata.groupTitle.lowercase().replace(" ", "_"),
                                categoryName = currentChannelMetadata.groupTitle,
                                type = guessTypeFromCategory(currentChannelMetadata.groupTitle, currentChannelMetadata.name, trimmed)
                            )
                        )
                        currentChannelMetadata = null
                    } else {
                        // Channel without metadata
                        val name = trimmed.substringAfterLast("/").substringBefore(".")
                        channels.add(
                            IPTVChannel(
                                id = name.hashCode().toString(),
                                name = name,
                                url = trimmed,
                                type = "LIVE"
                            )
                        )
                    }
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            reader.close()
        }
        return channels
    }

        private fun guessTypeFromCategory(category: String, name: String = "", url: String = ""): String {
        val cat = category.uppercase()
        val nam = name.uppercase()
        val path = url.lowercase()
        return when {
            path.contains("/series/") -> "SERIES"
            path.contains("/movie/") -> "MOVIE"
            path.contains("/live/") -> "LIVE"
            cat.contains("SERIE") || cat.contains("SÉRIE") || cat.contains("SHOW") || cat.contains("TEMPORADA") || cat.contains("SÉRIES") -> "SERIES"
            cat.contains("MOVIE") || cat.contains("FILME") || cat.contains("CINEMA") || cat.contains("VOD") -> "MOVIE"
            nam.matches(".*S\\d+E\\d+.*".toRegex()) || nam.contains("EPISÓDIO") || nam.contains("EPISODIO") -> "SERIES"
            else -> "LIVE"
        }
    }

    private fun parseExtInf(line: String): ExtInfMetadata {
        val tvgId = extractAttributeFast(line, "tvg-id")
        val tvgName = extractAttributeFast(line, "tvg-name")
        val logo = extractAttributeFast(line, "tvg-logo").ifEmpty { extractAttributeFast(line, "logo") }
        val groupTitle = extractAttributeFast(line, "group-title").ifEmpty { "Canais" }
        
        val commaIndex = line.lastIndexOf(",")
        val name = if (commaIndex != -1 && commaIndex < line.length - 1) line.substring(commaIndex + 1).trim() else ""

        return ExtInfMetadata(
            tvgId = tvgId,
            tvgName = tvgName.ifEmpty { name },
            logo = logo,
            groupTitle = groupTitle,
            name = name.ifEmpty { tvgName }.ifEmpty { "Sem Nome" }
        )
    }

    private fun extractAttributeFast(line: String, attribute: String): String {
        val attrSearch = "$attribute=\""
        val startIndex = line.indexOf(attrSearch)
        if (startIndex == -1) return ""
        val valueStart = startIndex + attrSearch.length
        val endIndex = line.indexOf("\"", valueStart)
        if (endIndex == -1) return ""
        return line.substring(valueStart, endIndex)
    }

    private data class ExtInfMetadata(
        val tvgId: String,
        val tvgName: String,
        val logo: String,
        val groupTitle: String,
        val name: String
    )
}
