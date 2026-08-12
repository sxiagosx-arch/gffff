import re

with open('/app/applet/app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

target = """        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setConnectTimeoutMs(15000)
            .setReadTimeoutMs(15000)
            .setAllowCrossProtocolRedirects(true)"""
            
replace = """        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("VLC/3.0.0")
            .setConnectTimeoutMs(15000)
            .setReadTimeoutMs(15000)
            .setAllowCrossProtocolRedirects(true)"""
            
content = content.replace(target, replace)

with open('/app/applet/app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)
