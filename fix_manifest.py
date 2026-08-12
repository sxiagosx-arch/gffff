with open('app/src/main/AndroidManifest.xml', 'r') as f:
    content = f.read()

features = """    <uses-feature android:name="android.hardware.touchscreen" android:required="false" />
    <uses-feature android:name="android.software.leanback" android:required="false" />
    
    <uses-permission"""

content = content.replace('<uses-permission', features, 1)

category = """<category android:name="android.intent.category.LAUNCHER" />
                <category android:name="android.intent.category.LEANBACK_LAUNCHER" />"""

content = content.replace('<category android:name="android.intent.category.LAUNCHER" />', category)

banner = """android:icon="@mipmap/ic_launcher"
        android:banner="@mipmap/ic_launcher"
        android:label="@string/app_name\""""

content = content.replace('android:icon="@mipmap/ic_launcher"\n        android:label="@string/app_name"', banner)

with open('app/src/main/AndroidManifest.xml', 'w') as f:
    f.write(content)
