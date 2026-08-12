with open('app/src/main/java/com/example/ui/IPTVViewModel.kt', 'r') as f:
    content = f.read()

target = """        // Start Splash Initialization Animation
        viewModelScope.launch {
            delay(100) // Faster visual lock
            val active = repository.getActiveAccount()
            if (active != null) {
                _currentScreen.value = Screen.HOME
                _uiState.value = IPTVUiState.Loading
                
                val loaded = repository.loadActivePlaylist()
                if (loaded) {
                    refreshContents()
                    _uiState.value = IPTVUiState.Success
                } else {
                    _currentScreen.value = Screen.LOGIN
                    _uiState.value = IPTVUiState.Idle
                }
            } else {
                _currentScreen.value = Screen.LOGIN
                _uiState.value = IPTVUiState.Idle
            }
        }"""

replace = """        // Start Splash Initialization Animation
        viewModelScope.launch {
            val splashTimer = async { delay(3500) } // Minimum splash time for animation
            val active = repository.getActiveAccount()
            
            var nextScreen = Screen.LOGIN
            var nextState: IPTVUiState = IPTVUiState.Idle

            if (active != null) {
                val loaded = repository.loadActivePlaylist()
                if (loaded) {
                    refreshContents()
                    nextState = IPTVUiState.Success
                    nextScreen = Screen.HOME
                }
            }
            
            splashTimer.await()
            _uiState.value = nextState
            _currentScreen.value = nextScreen
        }"""

content = content.replace(target, replace)

if "import kotlinx.coroutines.async" not in content:
    content = content.replace("import kotlinx.coroutines.launch", "import kotlinx.coroutines.launch\nimport kotlinx.coroutines.async")

with open('app/src/main/java/com/example/ui/IPTVViewModel.kt', 'w') as f:
    f.write(content)

