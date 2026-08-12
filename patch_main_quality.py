import re

with open('/app/applet/app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

target1 = """                                bufferSize = viewModel.bufferSize.value,"""
replace1 = """                                bufferSize = viewModel.bufferSize.value,
                                streamQuality = viewModel.streamQuality.collectAsState().value,"""
content = content.replace(target1, replace1)

with open('/app/applet/app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)
