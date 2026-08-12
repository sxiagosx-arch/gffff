with open('app/src/main/java/com/example/ui/player/ExoPlayerManager.kt', 'r') as f:
    content = f.read()

content = content.replace('dataSourceFactory: DefaultHttpDataSource.Factory', 'dataSourceFactory: androidx.media3.datasource.DataSource.Factory')
content = content.replace('import androidx.media3.datasource.DefaultHttpDataSource', 'import androidx.media3.datasource.DataSource')

with open('app/src/main/java/com/example/ui/player/ExoPlayerManager.kt', 'w') as f:
    f.write(content)
