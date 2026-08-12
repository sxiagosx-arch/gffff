package com.example.util

import com.example.model.IPTVChannel
import java.io.InputStream
import java.util.UUID

object M3UParser {

    fun parse(inputStream: InputStream): List<IPTVChannel> {
        val channels = mutableListOf<IPTVChannel>()
        
        val reader = inputStream.bufferedReader()
        var currentName = ""
        var currentLogo = ""
        var currentGroup = ""
        var currentId = ""
        
        reader.forEachLine { line ->
            val trimmedLine = line.trim()
            if (trimmedLine.startsWith("#EXTINF:")) {
                currentLogo = extractAttribute(trimmedLine, "tvg-logo")
                currentGroup = extractAttribute(trimmedLine, "group-title")
                currentId = extractAttribute(trimmedLine, "tvg-id")
                
                val commaIndex = trimmedLine.lastIndexOf(',')
                if (commaIndex != -1 && commaIndex < trimmedLine.length - 1) {
                    currentName = trimmedLine.substring(commaIndex + 1).trim()
                } else {
                    currentName = "Unknown Channel"
                }
            } else if (trimmedLine.isNotEmpty() && !trimmedLine.startsWith("#")) {
                val url = trimmedLine
                val id = if (currentId.isNotEmpty()) currentId else UUID.randomUUID().toString()
                channels.add(
                    IPTVChannel(
                        id = id,
                        name = currentName,
                        url = url,
                        logo = currentLogo,
                        categoryId = currentGroup.ifEmpty { "Uncategorized" },
                        categoryName = currentGroup.ifEmpty { "Uncategorized" },
                        type = "LIVE"
                    )
                )
                
                currentName = ""
                currentLogo = ""
                currentGroup = ""
                currentId = ""
            }
        }
        
        return channels
    }
    
    private fun extractAttribute(line: String, attribute: String): String {
        val regex = Regex("""$attribute="([^"]+)"""")
        val matchResult = regex.find(line)
        return matchResult?.groups?.get(1)?.value ?: ""
    }
    
    fun parse(content: String): List<IPTVChannel> {
        return parse(content.byteInputStream())
    }
}
