    private suspend fun loadDemoPlaylist(accountId: Int) {
        cachedChannels.clear()
        cachedCategories.clear()
        cachedSeries.clear()
        
        val demoUrl = "http://main.alprox.xyz/get.php?username=375845526&password=754922664&type=m3u_plus&output=mpegts"
        val m3uFile = java.io.File(context.cacheDir, "temp_demo_m3u_$accountId.m3u")
        val downloaded = downloadToFile(demoUrl, m3uFile)
        if (downloaded && m3uFile.exists()) {
            val list = IPTVParser.parseM3U(m3uFile)
            m3uFile.delete()
            cachedChannels.addAll(list)
            extractCategoriesFromChannels()
        }
    }
