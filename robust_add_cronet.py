with open('gradle/libs.versions.toml', 'r') as f:
    content = f.read()

if 'media3-datasource-cronet' not in content:
    content = content.replace('[libraries]', '[libraries]\nandroidx-media3-datasource-cronet = { group = "androidx.media3", name = "media3-datasource-cronet", version.ref = "media3" }\nplay-services-cronet = { group = "com.google.android.gms", name = "play-services-cronet", version = "18.0.1" }')
    with open('gradle/libs.versions.toml', 'w') as f:
        f.write(content)

with open('app/build.gradle.kts', 'r') as f:
    content = f.read()

if 'libs.androidx.media3.datasource.cronet' not in content:
    content = content.replace('dependencies {', 'dependencies {\n    implementation(libs.androidx.media3.datasource.cronet)\n    implementation(libs.play.services.cronet)')
    with open('app/build.gradle.kts', 'w') as f:
        f.write(content)

