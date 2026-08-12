import re

with open('app/src/main/java/com/example/ui/IPTVViewModel.kt', 'r') as f:
    content = f.read()

target1 = """    val blockedItems: StateFlow<List<BlockedItem>> = _blockedItems.asStateFlow()"""
replace1 = """    val blockedItems: StateFlow<List<BlockedItem>> = _blockedItems.asStateFlow()
    
    private val _accountExpiration = MutableStateFlow("Desconhecido")
    val accountExpiration: StateFlow<String> = _accountExpiration.asStateFlow()"""

content = content.replace(target1, replace1)

target2 = """            val loaded = repository.loadActivePlaylist()
            if (loaded) {
                refreshContents()"""
replace2 = """            val loaded = repository.loadActivePlaylist()
            if (loaded) {
                _accountExpiration.value = repository.getAccountExpiration()
                refreshContents()"""

content = content.replace(target2, replace2)

with open('app/src/main/java/com/example/ui/IPTVViewModel.kt', 'w') as f:
    f.write(content)
