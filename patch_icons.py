import os

svg_content = """<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="200dp"
    android:height="200dp"
    android:viewportWidth="240"
    android:viewportHeight="240">

    <!-- Outer Neon Glow for Chameleon Body & Tongue -->
    <path
        android:strokeColor="#2239FF14"
        android:strokeWidth="24"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"
        android:pathData="M 190,105 L 160,105 C 160,60 110,50 100,85 C 90,115 80,130 80,160 A 30,30 0 0,1 140,160 A 20,20 0 0,1 100,160 A 10,10 0 0,1 120,160" />

    <path
        android:strokeColor="#6639FF14"
        android:strokeWidth="12"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"
        android:pathData="M 190,105 L 160,105 C 160,60 110,50 100,85 C 90,115 80,130 80,160 A 30,30 0 0,1 140,160 A 20,20 0 0,1 100,160 A 10,10 0 0,1 120,160" />

    <path
        android:strokeColor="#FFFFFF"
        android:strokeWidth="4"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"
        android:pathData="M 190,105 L 160,105 C 160,60 110,50 100,85 C 90,115 80,130 80,160 A 30,30 0 0,1 140,160 A 20,20 0 0,1 100,160 A 10,10 0 0,1 120,160" />

    <!-- Tongue Tip Glow -->
    <path
        android:fillColor="#39FF14"
        android:pathData="M 200,105 A 10,10 0 1,1 180,105 A 10,10 0 1,1 200,105" />
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M 195,105 A 5,5 0 1,1 185,105 A 5,5 0 1,1 195,105" />

    <!-- Eye Glow -->
    <path
        android:fillColor="#39FF14"
        android:pathData="M 148,85 A 8,8 0 1,1 132,85 A 8,8 0 1,1 148,85" />
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M 144,85 A 4,4 0 1,1 136,85 A 4,4 0 1,1 144,85" />

    <!-- Lightning Bolt inside the curve of the back -->
    <path
        android:strokeColor="#2239FF14"
        android:strokeWidth="20"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"
        android:pathData="M 130,110 L 110,140 L 125,140 L 105,170" />
    <path
        android:strokeColor="#6639FF14"
        android:strokeWidth="10"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"
        android:pathData="M 130,110 L 110,140 L 125,140 L 105,170" />
    <path
        android:strokeColor="#FFFFFF"
        android:strokeWidth="4"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"
        android:pathData="M 130,110 L 110,140 L 125,140 L 105,170" />

</vector>
"""

with open('/app/applet/app/src/main/res/drawable/ic_logo.xml', 'w') as f:
    f.write(svg_content)

launcher_content = svg_content.replace('android:width="200dp"', 'android:width="108dp"').replace('android:height="200dp"', 'android:height="108dp"')

with open('/app/applet/app/src/main/res/drawable/ic_launcher_foreground.xml', 'w') as f:
    f.write(launcher_content)

