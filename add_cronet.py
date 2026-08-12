with open('gradle/libs.versions.toml', 'r') as f:
    content = f.read()

content = content.replace('androidx-media3-exoplayer-hls = { group = "androidx.media3", name = "media3-exoplayer-hls", version.ref = "media3" }',
'''androidx-media3-exoplayer-hls = { group = "androidx.media3", name = "media3-exoplayer-hls", version.ref = "media3" }
androidx-media3-datasource-cronet = { group = "androidx.media3", name = "media3-datasource-cronet", version.ref = "media3" }
play-services-cronet = { group = "com.google.android.gms", name = "play-services-cronet", version = "18.0.1" }''')

with open('gradle/libs.versions.toml', 'w') as f:
    f.write(content)

with open('app/build.gradle.kts', 'r') as f:
    content = f.read()

content = content.replace('implementation(libs.androidx.media3.exoplayer.hls)',
'''implementation(libs.androidx.media3.exoplayer.hls)
    implementation(libs.androidx.media3.datasource.cronet)
    implementation(libs.play.services.cronet)''')

with open('app/build.gradle.kts', 'w') as f:
    f.write(content)
