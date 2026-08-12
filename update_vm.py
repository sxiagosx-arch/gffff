import re

with open('app/src/main/java/com/example/ui/IPTVViewModel.kt', 'r') as f:
    content = f.read()

content = re.sub(r'\s*private val _streamQuality = MutableStateFlow\("Automática"\)\s*val streamQuality: StateFlow<String> = _streamQuality\.asStateFlow\(\)\n?', '', content)
content = re.sub(r'\s*fun setStreamQuality\(quality: String\) \{\s*_streamQuality\.value = quality\s*viewModelScope\.launch \{ repository\.setSetting\("streamQuality", quality\) \}\s*\}\n?', '', content)
content = re.sub(r'\s*_streamQuality\.value = repository\.getSetting\("streamQuality", "Automática"\)\n?', '', content)

with open('app/src/main/java/com/example/ui/IPTVViewModel.kt', 'w') as f:
    f.write(content)
