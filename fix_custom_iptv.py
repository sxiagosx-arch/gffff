with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

old_ds = """val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("VLC/3.0.0")
            .setConnectTimeoutMs(30000)
            .setReadTimeoutMs(30000)
            .setAllowCrossProtocolRedirects(true)"""

new_ds = """val dataSourceFactory = CronetUtil.getDataSourceFactory()"""

content = content.replace(old_ds, new_ds)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)
