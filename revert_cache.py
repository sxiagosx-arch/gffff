import re

with open('app/src/main/java/com/example/ui/player/ExoPlayerManager.kt', 'r') as f:
    content = f.read()

# Replace setMediaSourceFactory
content = content.replace('.setMediaSourceFactory(androidx.media3.exoplayer.source.DefaultMediaSourceFactory(cacheDataSourceFactory))', '.setMediaSourceFactory(androidx.media3.exoplayer.source.DefaultMediaSourceFactory(dataSourceFactory))')

with open('app/src/main/java/com/example/ui/player/ExoPlayerManager.kt', 'w') as f:
    f.write(content)
