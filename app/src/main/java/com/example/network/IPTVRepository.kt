package com.example.network

import android.content.Context
import android.util.Log
import com.example.database.AppSetting
import com.example.database.BlockedItem
import com.example.database.Favorite
import com.example.database.IPTVDatabase
import com.example.database.PlaylistAccount
import com.example.database.WatchHistory
import com.example.model.IPTVCategory
import com.example.model.IPTVChannel
import com.example.model.IPTVSeries
import com.example.model.IPTVSeason
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class IPTVRepository(private val context: Context) {

    private val db = IPTVDatabase.getDatabase(context)
    private val dao = db.iptvDao()
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    // Current playlist cache in memory
    private val cachedChannels = mutableListOf<IPTVChannel>()
    private val cachedCategories = mutableListOf<IPTVCategory>()
    private val cachedSeries = mutableListOf<IPTVSeries>()
    private val cachedM3USeriesEpisodes = mutableMapOf<String, List<IPTVChannel>>()

    // Exposed Flows from Room
    val accountsFlow: Flow<List<PlaylistAccount>> = dao.getAllAccountsFlow()
    val activeAccountFlow: Flow<PlaylistAccount?> = dao.getActiveAccountFlow()

    suspend fun getActiveAccount(): PlaylistAccount? = withContext(Dispatchers.IO) {
        dao.getActiveAccount()
    }
    
    suspend fun getAccountExpiration(): String = withContext(Dispatchers.IO) {
        val active = dao.getActiveAccount() ?: return@withContext "Nenhuma conta"
        if (active.type == "XTREAM") {
            try {
                val url = "${active.serverUrl}/player_api.php?username=${active.username}&password=${active.password}"
                val jsonStr = getNetworkString(url)
                if (jsonStr.isEmpty()) return@withContext "Falha na conexão"
                val root = org.json.JSONObject(jsonStr)
                val userInfo = root.optJSONObject("user_info")
                if (userInfo != null) {
                    val exp = userInfo.optString("exp_date", "")
                    if (exp.isNotEmpty() && exp != "null") {
                        val expLong = exp.toLongOrNull()
                        if (expLong != null) {
                            val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                            return@withContext sdf.format(java.util.Date(expLong * 1000L))
                        }
                        return@withContext exp
                    }
                    return@withContext "Ilimitado"
                }
            } catch(e: Exception) {
                return@withContext "Desconhecido"
            }
        }
        return@withContext "Não aplicável (M3U)"
    }

    suspend fun saveAccount(account: PlaylistAccount): Int = withContext(Dispatchers.IO) {
        if (account.isActive) {
            dao.deactivateAllAccounts()
        }
        val id = dao.insertAccount(account).toInt()
        id
    }

    suspend fun selectAccount(accountId: Int) = withContext(Dispatchers.IO) {
        dao.deactivateAllAccounts()
        val accounts = dao.getAllAccounts()
        val target = accounts.find { it.id == accountId }
        if (target != null) {
            dao.updateAccount(target.copy(isActive = true))
        }
        clearCache()
    }

    suspend fun deleteAccount(accountId: Int) = withContext(Dispatchers.IO) {
        dao.deleteAccountById(accountId)
        clearCache()
    }

    private fun clearCache() {
        cachedChannels.clear()
        cachedCategories.clear()
        cachedSeries.clear()
    }

    // Load IPTV contents based on active account
    suspend fun loadActivePlaylist(forceRefresh: Boolean = false): Boolean = withContext(Dispatchers.IO) {
        val active = dao.getActiveAccount() ?: return@withContext false
        
        val cacheFile = java.io.File(context.cacheDir, "playlist_cache_${active.id}.dat")
        if (!forceRefresh && cacheFile.exists()) {
            try {
                java.io.ObjectInputStream(cacheFile.inputStream().buffered()).use { ois ->
                    val channelsCount = ois.readInt()
                    val channels = ArrayList<IPTVChannel>(channelsCount)
                    for (i in 0 until channelsCount) {
                        channels.add(ois.readUnshared() as IPTVChannel)
                    }
                    val categoriesCount = ois.readInt()
                    val categories = ArrayList<IPTVCategory>(categoriesCount)
                    for (i in 0 until categoriesCount) {
                        categories.add(ois.readUnshared() as IPTVCategory)
                    }
                    val seriesCount = ois.readInt()
                    val series = ArrayList<IPTVSeries>(seriesCount)
                    for (i in 0 until seriesCount) {
                        series.add(ois.readUnshared() as IPTVSeries)
                    }
                    
                    val map = ois.readObject() as? Map<String, List<IPTVChannel>> ?: emptyMap()
                    
                    cachedChannels.clear()
                    cachedChannels.addAll(channels)
                    cachedCategories.clear()
                    cachedCategories.addAll(categories)
                    cachedSeries.clear()
                    cachedSeries.addAll(series)
                    cachedM3USeriesEpisodes.clear()
                    cachedM3USeriesEpisodes.putAll(map)
                    
                    return@withContext true
                }
            } catch (e: Throwable) {
                Log.e("IPTVRepository", "Error reading cache, fetching again", e)
                cacheFile.delete()
            }
        }
        
        var success = false
        try {
            when (active.type) {
                "DEMO" -> {
                    loadDemoPlaylist(active.id)
                    success = true
                }
                "M3U_URL" -> {
                    // Try to auto-upgrade to Xtream if it's an Xtream URL
                    val xtreamRegex = "(https?://[^/]+)/get\\.php.*username=([^&]+).*password=([^&]+)".toRegex(RegexOption.IGNORE_CASE)
                    val match = xtreamRegex.find(active.m3uUrl)
                    if (match != null && match.groupValues.size >= 4) {
                        val upgradedAccount = active.copy(
                            type = "XTREAM",
                            serverUrl = match.groupValues[1],
                            username = match.groupValues[2],
                            password = match.groupValues[3]
                        )
                        dao.updateAccount(upgradedAccount)
                        success = loadXtreamPlaylist(upgradedAccount)
                        if (success) {
                            return@withContext true
                        }
                    }

                    val m3uFile = java.io.File(context.cacheDir, "temp_m3u_${active.id}.m3u")
                    val downloaded = downloadToFile(active.m3uUrl, m3uFile)
                    if (downloaded && m3uFile.exists()) {
                        val list = IPTVParser.parseM3U(m3uFile)
                        m3uFile.delete()
                        processM3UList(list)
                        success = true
                    }
                }
                "XTREAM" -> {
                    success = loadXtreamPlaylist(active)
                }
            }
        } catch (e: Throwable) {
            Log.e("IPTVRepository", "Error loading playlist", e)
        }
        
        if (success) {
            try {
                java.io.ObjectOutputStream(cacheFile.outputStream().buffered()).use { oos ->
                    val channels = cachedChannels.toList()
                    oos.writeInt(channels.size)
                    for ((index, item) in channels.withIndex()) {
                        oos.writeUnshared(item)
                        if (index % 500 == 0) oos.reset()
                    }
                    val categories = cachedCategories.toList()
                    oos.writeInt(categories.size)
                    for (item in categories) {
                        oos.writeUnshared(item)
                    }
                    val series = cachedSeries.toList()
                    oos.writeInt(series.size)
                    for ((index, item) in series.withIndex()) {
                        oos.writeUnshared(item)
                        if (index % 500 == 0) oos.reset()
                    }
                    oos.writeObject(cachedM3USeriesEpisodes.toMap())
                }
            } catch (e: Throwable) {
                Log.e("IPTVRepository", "Error saving cache", e)
                cacheFile.delete()
            }
        }
        
        return@withContext success
    }

    private fun extractCategoriesFromChannels() {
        val uniqueCats = mutableMapOf<String, Pair<String, String>>() // id -> (name, type)
        
        for (ch in cachedChannels) {
            if (ch.categoryId.isNotEmpty() && !uniqueCats.containsKey(ch.categoryId)) {
                uniqueCats[ch.categoryId] = Pair(ch.categoryName, ch.type)
            }
        }
        
        for (series in cachedSeries) {
            if (series.categoryId.isNotEmpty() && !uniqueCats.containsKey(series.categoryId)) {
                uniqueCats[series.categoryId] = Pair(series.categoryName, "SERIES")
            }
        }

        cachedCategories.clear()
        uniqueCats.forEach { (id, info) ->
            cachedCategories.add(com.example.model.IPTVCategory(id = id, name = info.first, type = info.second))
        }
    }
    

    private suspend fun downloadM3U(url: String): String = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(url).build()
            okHttpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    return@withContext response.body?.string() ?: ""
                }
            }
        } catch (e: Throwable) {
            Log.e("IPTVRepository", "M3U download failed", e)
        }
        ""
    }

    private suspend fun downloadToFile(url: String, file: java.io.File): Boolean = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(url).build()
            okHttpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    response.body?.byteStream()?.use { input ->
                        file.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    return@withContext true
                }
            }
        } catch (e: Throwable) {
            Log.e("IPTVRepository", "Failed to download $url", e)
        }
        false
    }

    private suspend fun loadXtreamPlaylist(acc: PlaylistAccount): Boolean = withContext(Dispatchers.IO) {
        try {
            val liveCatsFile = java.io.File(context.cacheDir, "temp_live_cats.json")
            val vodCatsFile = java.io.File(context.cacheDir, "temp_vod_cats.json")
            val seriesCatsFile = java.io.File(context.cacheDir, "temp_series_cats.json")
            val liveStreamsFile = java.io.File(context.cacheDir, "temp_live_streams.json")
            val vodStreamsFile = java.io.File(context.cacheDir, "temp_vod_streams.json")
            val seriesFile = java.io.File(context.cacheDir, "temp_series.json")

            // Fetch everything in parallel
            val liveCatsDef = async { downloadToFile("${acc.serverUrl}/player_api.php?username=${acc.username}&password=${acc.password}&action=get_live_categories", liveCatsFile) }
            val vodCatsDef = async { downloadToFile("${acc.serverUrl}/player_api.php?username=${acc.username}&password=${acc.password}&action=get_vod_categories", vodCatsFile) }
            val seriesCatsDef = async { downloadToFile("${acc.serverUrl}/player_api.php?username=${acc.username}&password=${acc.password}&action=get_series_categories", seriesCatsFile) }
            val liveStreamsDef = async { downloadToFile("${acc.serverUrl}/player_api.php?username=${acc.username}&password=${acc.password}&action=get_live_streams", liveStreamsFile) }
            val vodStreamsDef = async { downloadToFile("${acc.serverUrl}/player_api.php?username=${acc.username}&password=${acc.password}&action=get_vod_streams", vodStreamsFile) }
            val seriesDef = async { downloadToFile("${acc.serverUrl}/player_api.php?username=${acc.username}&password=${acc.password}&action=get_series", seriesFile) }

            liveCatsDef.await()
            vodCatsDef.await()
            seriesCatsDef.await()

            cachedCategories.clear()
            parseXtreamCategories(liveCatsFile, "LIVE")
            parseXtreamCategories(vodCatsFile, "MOVIE")
            parseXtreamCategories(seriesCatsFile, "SERIES")

            liveStreamsDef.await()
            vodStreamsDef.await()
            seriesDef.await()

            val liveChannels = parseXtreamStreams(liveStreamsFile, "LIVE", acc)
            val vodChannels = parseXtreamStreams(vodStreamsFile, "MOVIE", acc)
            val series = parseXtreamSeries(seriesFile)

            cachedChannels.clear()
            cachedChannels.addAll(liveChannels)
            cachedChannels.addAll(vodChannels)
            
            cachedSeries.clear()
            cachedSeries.addAll(series)

            // Cleanup temp files
            liveCatsFile.delete()
            vodCatsFile.delete()
            seriesCatsFile.delete()
            liveStreamsFile.delete()
            vodStreamsFile.delete()
            seriesFile.delete()

            return@withContext true
        } catch (e: Throwable) {
            Log.e("IPTVRepository", "Xtream load failed", e)
        }
        false
    }

    private fun parseXtreamCategories(file: java.io.File, type: String) {
        if (!file.exists()) return
        try {
            android.util.JsonReader(java.io.FileReader(file)).use { reader ->
                val peek = reader.peek()
                if (peek == android.util.JsonToken.BEGIN_ARRAY) {
                    reader.beginArray()
                    while (reader.hasNext()) {
                        reader.beginObject()
                        var id = ""
                        var name = ""
                        while (reader.hasNext()) {
                            when (reader.nextName()) {
                                "category_id" -> id = try { reader.nextString() } catch(e: Exception) { reader.nextInt().toString() }
                                "category_name" -> name = reader.nextString()
                                else -> reader.skipValue()
                            }
                        }
                        reader.endObject()
                        if (id.isNotEmpty()) {
                            cachedCategories.add(IPTVCategory(id = id, name = name, type = type))
                        }
                    }
                    reader.endArray()
                } else if (peek == android.util.JsonToken.BEGIN_OBJECT) {
                    // Usually this is an error object
                }
            }
        } catch (e: Throwable) {
            Log.e("IPTVRepository", "Error parsing categories $type", e)
        }
    }

    private fun parseXtreamStreams(file: java.io.File, type: String, acc: PlaylistAccount): List<IPTVChannel> {
        val result = mutableListOf<IPTVChannel>()
        if (!file.exists()) return result
        try {
            android.util.JsonReader(java.io.FileReader(file)).use { reader ->
                val categoryMap = cachedCategories.associateBy { it.id }
                
                val peek = reader.peek()
                if (peek == android.util.JsonToken.BEGIN_ARRAY) {
                    reader.beginArray()
                    while (reader.hasNext()) {
                        reader.beginObject()
                        
                        var streamId = ""
                        var name = ""
                        var logo = ""
                        var categoryId = ""
                        var ext = "ts"
                        var rating = ""
                        var plot = ""
                        var cast = ""
                        var director = ""
                        var duration = ""
                        var year = ""

                        while (reader.hasNext()) {
                            when (reader.nextName()) {
                                "stream_id" -> streamId = try { reader.nextString() } catch(e: Exception) { reader.nextInt().toString() }
                                "name" -> name = try { reader.nextString() } catch(e: Exception) { reader.skipValue(); "" }
                                "stream_icon" -> logo = try { reader.nextString() } catch(e: Exception) { reader.skipValue(); "" }
                                "category_id" -> categoryId = try { reader.nextString() } catch(e: Exception) { reader.nextInt().toString() }
                                "container_extension" -> ext = try { reader.nextString() } catch(e: Exception) { reader.skipValue(); "ts" }
                                "rating" -> rating = try { reader.nextString() } catch(e: Exception) { reader.skipValue(); "" }
                                "plot" -> plot = try { reader.nextString() } catch(e: Exception) { reader.skipValue(); "" }
                                "cast" -> cast = try { reader.nextString() } catch(e: Exception) { reader.skipValue(); "" }
                                "director" -> director = try { reader.nextString() } catch(e: Exception) { reader.skipValue(); "" }
                                "duration" -> duration = try { reader.nextString() } catch(e: Exception) { reader.skipValue(); "" }
                                "year" -> year = try { reader.nextString() } catch(e: Exception) { reader.skipValue(); "" }
                                else -> reader.skipValue()
                            }
                        }
                        reader.endObject()
                        
                        if (streamId.isNotEmpty()) {
                            val categoryName = categoryMap[categoryId]?.name ?: ""
                            val streamTypePath = if (type == "MOVIE") "movie" else "live"
                            val url = "${acc.serverUrl}/$streamTypePath/${acc.username}/${acc.password}/$streamId.$ext"
                            result.add(IPTVChannel(
                                id = streamId, name = name, url = url, logo = logo,
                                categoryId = categoryId, categoryName = categoryName, type = type,
                                rating = rating, description = plot, cast = cast,
                                director = director, duration = duration, year = year
                            ))
                        }
                    }
                    reader.endArray()
                }
            }
        } catch (e: Throwable) {
            Log.e("IPTVRepository", "Error parsing streams $type", e)
        }
        return result
    }

    private fun parseXtreamSeries(file: java.io.File): List<IPTVSeries> {
        val result = mutableListOf<IPTVSeries>()
        if (!file.exists()) return result
        try {
            android.util.JsonReader(java.io.FileReader(file)).use { reader ->
                val categoryMap = cachedCategories.associateBy { it.id }
                
                val peek = reader.peek()
                if (peek == android.util.JsonToken.BEGIN_ARRAY) {
                    reader.beginArray()
                    while (reader.hasNext()) {
                        reader.beginObject()
                        
                        var seriesId = ""
                        var num = ""
                        var name = ""
                        var title = ""
                        var cover = ""
                        var categoryId = ""
                        var rating = ""
                        var year = ""
                        var plot = ""
                        var cast = ""
                        var director = ""

                        while (reader.hasNext()) {
                            when (reader.nextName()) {
                                "series_id" -> seriesId = try { reader.nextString() } catch(e: Exception) { reader.nextInt().toString() }
                                "num" -> num = try { reader.nextString() } catch(e: Exception) { reader.nextInt().toString() }
                                "name" -> name = try { reader.nextString() } catch(e: Exception) { reader.skipValue(); "" }
                                "title" -> title = try { reader.nextString() } catch(e: Exception) { reader.skipValue(); "" }
                                "cover" -> cover = try { reader.nextString() } catch(e: Exception) { reader.skipValue(); "" }
                                "category_id" -> categoryId = try { reader.nextString() } catch(e: Exception) { reader.nextInt().toString() }
                                "rating" -> rating = try { reader.nextString() } catch(e: Exception) { reader.skipValue(); "" }
                                "releaseDate" -> year = try { reader.nextString() } catch(e: Exception) { reader.skipValue(); "" }
                                "plot" -> plot = try { reader.nextString() } catch(e: Exception) { reader.skipValue(); "" }
                                "cast" -> cast = try { reader.nextString() } catch(e: Exception) { reader.skipValue(); "" }
                                "director" -> director = try { reader.nextString() } catch(e: Exception) { reader.skipValue(); "" }
                                else -> reader.skipValue()
                            }
                        }
                        reader.endObject()
                        
                        val finalId = if (seriesId.isNotEmpty()) seriesId else num
                        val finalName = if (name.isNotEmpty()) name else title
                        
                        if (finalId.isNotEmpty()) {
                            val categoryName = categoryMap[categoryId]?.name ?: ""
                            val finalYear = if (year.contains("-")) year.substringBefore("-") else year
                            
                            result.add(IPTVSeries(
                                id = finalId, name = finalName, cover = cover,
                                categoryId = categoryId, categoryName = categoryName,
                                rating = rating, year = finalYear, plot = plot,
                                cast = cast, director = director
                            ))
                        }
                    }
                    reader.endArray()
                }
            }
        } catch (e: Throwable) {
            Log.e("IPTVRepository", "Error parsing series", e)
        }
        return result
    }

    private suspend fun getNetworkString(url: String): String {
        return try {
            val request = Request.Builder().url(url).build()
            okHttpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) response.body?.string() ?: "" else ""
            }
        } catch (e: Throwable) {
            ""
        }
    }

        suspend fun fetchSeriesSeasonsAndEpisodes(seriesId: String): List<IPTVSeason> = withContext(Dispatchers.IO) {
        val active = dao.getActiveAccount() ?: return@withContext emptyList()
        
        if (active.type == "M3U_URL" || active.type == "DEMO") {
            // Find the series by checking if seriesId matches
            val targetSeries = cachedSeries.find { it.id == seriesId } ?: return@withContext emptyList()
            
            val eps = cachedM3USeriesEpisodes[seriesId] ?: emptyList()
            
            val seasonsMap = mutableMapOf<Int, MutableList<IPTVChannel>>()
            val regex = "S\\d+[E|e]\\d+".toRegex(RegexOption.IGNORE_CASE)
            val regex2 = "S\\d+".toRegex(RegexOption.IGNORE_CASE)
            val seasonEpMatch = ".*S(\\d+)E(\\d+).*".toRegex(RegexOption.IGNORE_CASE)
            val seasonMatch = ".*S(\\d+).*".toRegex(RegexOption.IGNORE_CASE)
            
            for (ep in eps) {
                var sNum = 1
                val match = seasonEpMatch.find(ep.name)
                if (match != null) {
                    sNum = match.groupValues[1].toIntOrNull() ?: 1
                } else {
                    val match2 = seasonMatch.find(ep.name)
                    if (match2 != null) {
                        sNum = match2.groupValues[1].toIntOrNull() ?: 1
                    }
                }
                var cleanName = ep.name
                val cleanMatch = regex.find(ep.name)
                if (cleanMatch != null) {
                    cleanName = ep.name.substring(cleanMatch.range.last + 1).trim().removePrefix("-").trim()
                } else {
                    val cleanMatch2 = regex2.find(ep.name)
                    if (cleanMatch2 != null) {
                        cleanName = ep.name.substring(cleanMatch2.range.last + 1).trim().removePrefix("-").trim()
                    }
                }
                if (cleanName.isEmpty() || cleanName.equals(ep.name, ignoreCase = true)) {
                    // Try another common pattern: Title S01 E01 - EpName
                    val fallbackRegex = "S\\d+\\s*E\\d+(.*)".toRegex(RegexOption.IGNORE_CASE)
                    val fb = fallbackRegex.find(ep.name)
                    if (fb != null && fb.groupValues.size > 1) {
                        cleanName = fb.groupValues[1].trim().removePrefix("-").trim()
                    }
                }
                if (cleanName.isEmpty()) {
                    cleanName = "Episódio " + (seasonsMap[sNum]?.size?.plus(1) ?: 1)
                }
                
                val cleanEp = ep.copy(name = cleanName)
                seasonsMap.getOrPut(sNum) { mutableListOf() }.add(cleanEp)
            }
            
            val result = mutableListOf<IPTVSeason>()
            seasonsMap.forEach { (sNum, epList) ->
                result.add(
                    IPTVSeason(
                        number = sNum,
                        
                        episodes = epList
                    )
                )
            }
            return@withContext result.sortedBy { it.number }
        }

        val url = "${active.serverUrl}/player_api.php?username=${active.username}&password=${active.password}&action=get_series_info&series_id=$seriesId"
        val jsonStr = getNetworkString(url)
        if (jsonStr.isEmpty()) return@withContext emptyList()

        val seasonsMap = mutableMapOf<Int, MutableList<IPTVChannel>>()
        try {
            val mainObj = org.json.JSONObject(jsonStr)
            val episodesObj = mainObj.optJSONObject("episodes") ?: return@withContext emptyList()
            
            val seasonKeys = episodesObj.keys()
            while (seasonKeys.hasNext()) {
                val sKey = seasonKeys.next()
                val sNum = sKey.toIntOrNull() ?: 1
                val epArr = episodesObj.optJSONArray(sKey) ?: continue
                
                val epList = mutableListOf<IPTVChannel>()
                for (i in 0 until epArr.length()) {
                    val epObj = epArr.getJSONObject(i)
                    val id = epObj.optString("id")
                    val name = epObj.optString("title")
                    val epId = epObj.optString("id")
                    val ext = epObj.optString("container_extension", "mp4")
                    
                    val info = epObj.optJSONObject("info")
                    var logo = ""
                    if (info != null) {
                        logo = info.optString("movie_image", "")
                    }
                    
                    epList.add(
                        IPTVChannel(
                            id = id,
                            name = name,
                            url = "${active.serverUrl}/series/${active.username}/${active.password}/$epId.$ext",
                            logo = logo,
                            type = "SERIES"
                        )
                    )
                }
                seasonsMap[sNum] = epList
            }
        } catch (e: Throwable) {
            android.util.Log.e("IPTVRepository", "Error parsing series info", e)
        }
        
        val result = mutableListOf<IPTVSeason>()
        seasonsMap.forEach { (sNum, epList) ->
            result.add(
                IPTVSeason(
                    number = sNum,
                    
                    episodes = epList
                )
            )
        }
        return@withContext result.sortedBy { it.number }
    }

    fun getSeries(): List<IPTVSeries> = cachedSeries

    fun getChannelsByType(type: String): List<IPTVChannel> {
        return cachedChannels.filter { it.type == type }
    }

    fun getChannels(): List<IPTVChannel> = cachedChannels

    fun getCategories(): List<IPTVCategory> = cachedCategories

    fun getCategoriesByType(type: String): List<IPTVCategory> {
        return cachedCategories.filter { it.type == type }
    }

    fun getFavoritesFlow(accountId: Int): Flow<List<Favorite>> = dao.getFavoritesFlow(accountId)

    suspend fun getFavorites(): List<Favorite> = withContext(Dispatchers.IO) {
        val active = dao.getActiveAccount() ?: return@withContext emptyList()
        dao.getFavoritesFlow(active.id).first()
    }

    suspend fun toggleFavorite(channel: IPTVChannel) = withContext(Dispatchers.IO) {
        val active = dao.getActiveAccount() ?: return@withContext
        val existing = dao.getFavorite(active.id, channel.id, channel.type)
        if (existing != null) {
            dao.deleteFavorite(active.id, channel.id, channel.type)
        } else {
            dao.insertFavorite(Favorite(accountId = active.id, streamId = channel.id, name = channel.name, logoUrl = channel.logo, type = channel.type, categoryId = channel.categoryId))
        }
    }

    suspend fun toggleFavoriteSeries(series: IPTVSeries) = withContext(Dispatchers.IO) {
        val active = dao.getActiveAccount() ?: return@withContext
        val existing = dao.getFavorite(active.id, series.id, "SERIES")
        if (existing != null) {
            dao.deleteFavorite(active.id, series.id, "SERIES")
        } else {
            dao.insertFavorite(Favorite(accountId = active.id, streamId = series.id, name = series.name, logoUrl = series.cover, type = "SERIES", categoryId = series.categoryId))
        }
    }

    fun getWatchHistoryFlow(accountId: Int): Flow<List<WatchHistory>> = dao.getWatchHistoryFlow(accountId)

    suspend fun saveWatchProgress(channel: IPTVChannel, position: Long, duration: Long) = withContext(Dispatchers.IO) {
        val active = dao.getActiveAccount() ?: return@withContext
        
        if (channel.type == "SERIES" && channel.seriesId.isNotEmpty()) {
            dao.deleteWatchHistoryBySeries(active.id, channel.seriesId)
        } else {
            dao.deleteWatchHistory(active.id, channel.id, channel.type)
        }
        
        dao.insertWatchHistory(
            WatchHistory(
                accountId = active.id,
                streamId = channel.id,
                name = channel.name,
                logoUrl = channel.logo,
                type = channel.type,
                streamUrl = channel.url,
                positionMs = position,
                durationMs = duration,
                lastWatched = System.currentTimeMillis(),
                seasonNumber = channel.seasonNumber,
                episodeNumber = channel.episodeNumber,
                seriesId = channel.seriesId
            )
        )
    }

    fun getBlockedItemsFlow(accountId: Int): Flow<List<BlockedItem>> = dao.getBlockedItemsFlow(accountId)

    suspend fun toggleCategoryBlock(categoryId: String) = withContext(Dispatchers.IO) {
        val active = dao.getActiveAccount() ?: return@withContext
        val existing = dao.getBlockedItems(active.id).find { it.blockId == categoryId && it.type == "CATEGORY" }
        if (existing != null) {
            dao.deleteBlockedItem(active.id, categoryId, "CATEGORY")
        } else {
            dao.insertBlockedItem(BlockedItem(accountId = active.id, blockId = categoryId, type = "CATEGORY"))
        }
    }

    suspend fun toggleCategoryHidden(categoryId: String) = withContext(Dispatchers.IO) {
        val active = dao.getActiveAccount() ?: return@withContext
        val existing = dao.getBlockedItems(active.id).find { it.blockId == categoryId && it.type == "HIDDEN_CATEGORY" }
        if (existing != null) {
            dao.deleteBlockedItem(active.id, categoryId, "HIDDEN_CATEGORY")
        } else {
            dao.insertBlockedItem(BlockedItem(accountId = active.id, blockId = categoryId, type = "HIDDEN_CATEGORY"))
        }
    }


    suspend fun getSetting(key: String, defaultValue: String): String = withContext(Dispatchers.IO) {
        dao.getSettingValue(key) ?: defaultValue
    }
    
    suspend fun setSetting(key: String, value: String) = withContext(Dispatchers.IO) {
        dao.insertSetting(AppSetting(key, value))
    }

    suspend fun getParentalPin(): String? = withContext(Dispatchers.IO) {
        dao.getSettingValue("parental_pin")
    }

    suspend fun setParentalPin(pin: String) = withContext(Dispatchers.IO) {
        dao.insertSetting(AppSetting("parental_pin", pin))
    }

    suspend fun fetchEPG(streamId: String): List<com.example.model.EPGProgram> = withContext(Dispatchers.IO) {
        val active = dao.getActiveAccount() ?: return@withContext emptyList()
        if (active.type == "DEMO" || active.type == "M3U_URL") return@withContext emptyList()
        val url = "${active.serverUrl}/player_api.php?username=${active.username}&password=${active.password}&action=get_short_epg&stream_id=$streamId&limit=10"
        val jsonStr = getNetworkString(url)
        if (jsonStr.isEmpty()) return@withContext emptyList()
        try {
            val root = org.json.JSONObject(jsonStr)
            val epgArr = root.optJSONArray("epg_listings") ?: return@withContext emptyList()
            val list = mutableListOf<com.example.model.EPGProgram>()
            for (i in 0 until epgArr.length()) {
                val obj = epgArr.getJSONObject(i)
                list.add(
                    com.example.model.EPGProgram(
                        id = obj.optString("id"),
                        title = decodeXtreamString(obj.optString("title")),
                        description = decodeXtreamString(obj.optString("description")),
                        start = obj.optString("start"),
                        end = obj.optString("end"),
                        startTimestamp = obj.optLong("start_timestamp", 0L),
                        stopTimestamp = obj.optLong("stop_timestamp", 0L)
                    )
                )
            }
            return@withContext list
        } catch (e: Throwable) {
            Log.e("IPTVRepository", "EPG fetch failed", e)
        }
        emptyList()
    }
    
    private suspend fun loadDemoPlaylist(accountId: Int) {
        val demoUrl = "http://main.alprox.xyz/get.php?username=375845526&password=754922664&type=m3u_plus&output=mpegts"
        val m3uFile = java.io.File(context.cacheDir, "temp_demo_m3u_$accountId.m3u")
        val downloaded = downloadToFile(demoUrl, m3uFile)
        if (downloaded && m3uFile.exists()) {
            val list = IPTVParser.parseM3U(m3uFile)
            m3uFile.delete()
            processM3UList(list)
        }
    }

    private fun decodeXtreamString(str: String): String {
        return try {
            val bytes = android.util.Base64.decode(str, android.util.Base64.DEFAULT)
            String(bytes, Charsets.UTF_8)
        } catch (e: Throwable) {
            str
        }
    }

    private fun processM3UList(list: List<IPTVChannel>) {
        val seriesChannels = mutableListOf<IPTVChannel>()
        val regularChannels = mutableListOf<IPTVChannel>()

        for (ch in list) {
            if (ch.type == "SERIES") {
                seriesChannels.add(ch)
            } else {
                regularChannels.add(ch)
            }
        }

        cachedChannels.clear()
        cachedChannels.addAll(regularChannels)

        // Group series
        val seriesMap = mutableMapOf<String, MutableList<IPTVChannel>>()
        val regex = ".*S(\\d+)E(\\d+).*".toRegex(RegexOption.IGNORE_CASE)
        val sxxRegex = ".*S(\\d+).*".toRegex(RegexOption.IGNORE_CASE)

        for (ep in seriesChannels) {
            var seriesName = ep.name
            val match = regex.find(ep.name)
            if (match != null) {
                seriesName = ep.name.substring(0, match.range.first).trim().removeSuffix("-").trim()
            } else {
                val matchS = sxxRegex.find(ep.name)
                if (matchS != null) {
                    seriesName = ep.name.substring(0, matchS.range.first).trim().removeSuffix("-").trim()
                }
            }
            if (seriesName.isEmpty()) seriesName = ep.name
            seriesMap.getOrPut(seriesName) { mutableListOf() }.add(ep)
        }

        cachedSeries.clear()
        cachedM3USeriesEpisodes.clear()
        for ((sName, eps) in seriesMap) {
            val first = eps.first()
            val sId = first.categoryId + "_" + sName.hashCode().toString()
            cachedSeries.add(
                com.example.model.IPTVSeries(
                    id = sId,
                    name = sName,
                    cover = first.logo,
                    categoryId = first.categoryId,
                    categoryName = first.categoryName,
                    rating = "",
                    year = "",
                    director = "",
                    cast = "",
                    plot = ""
                )
            )
            cachedM3USeriesEpisodes[sId] = eps
        }
        
        extractCategoriesFromChannels()
    }
}

