with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

content = content.replace('super.onCreate(savedInstanceState)', 'super.onCreate(savedInstanceState)\n        com.example.ui.player.CronetUtil.init(this)')

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)
