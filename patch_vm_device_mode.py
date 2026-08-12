with open('app/src/main/java/com/example/ui/IPTVViewModel.kt', 'r') as f:
    content = f.read()

# Add Screen.DEVICE_SELECTION
content = content.replace("enum class Screen {\n    SPLASH,\n    LOGIN,", "enum class Screen {\n    SPLASH,\n    DEVICE_SELECTION,\n    LOGIN,")

# Add deviceLayoutMode Flow
target_flow = """    private val _uiState = MutableStateFlow<IPTVUiState>(IPTVUiState.Idle)
    val uiState: StateFlow<IPTVUiState> = _uiState.asStateFlow()"""
replace_flow = """    private val _uiState = MutableStateFlow<IPTVUiState>(IPTVUiState.Idle)
    val uiState: StateFlow<IPTVUiState> = _uiState.asStateFlow()

    private val _deviceLayoutMode = MutableStateFlow("UNSET")
    val deviceLayoutMode: StateFlow<String> = _deviceLayoutMode.asStateFlow()"""
content = content.replace(target_flow, replace_flow)

# Init it
target_init = """        viewModelScope.launch {
            _blockAdult.value = repository.getSetting("blockAdult", "false").toBoolean()"""
replace_init = """        viewModelScope.launch {
            _deviceLayoutMode.value = repository.getSetting("deviceLayoutMode", "UNSET")
            _blockAdult.value = repository.getSetting("blockAdult", "false").toBoolean()"""
content = content.replace(target_init, replace_init)

# Check in splash
target_splash = """            splashTimer.await()
            _uiState.value = nextState
            _currentScreen.value = nextScreen"""
replace_splash = """            splashTimer.await()
            if (_deviceLayoutMode.value == "UNSET") {
                _uiState.value = IPTVUiState.Idle
                _currentScreen.value = Screen.DEVICE_SELECTION
            } else {
                _uiState.value = nextState
                _currentScreen.value = nextScreen
            }"""
content = content.replace(target_splash, replace_splash)

# Add setDeviceLayoutMode function
target_end = """    }
}
"""
replace_end = """    }

    fun setDeviceLayoutMode(mode: String) {
        viewModelScope.launch {
            repository.setSetting("deviceLayoutMode", mode)
            _deviceLayoutMode.value = mode
            
            // Check where to go next
            val active = repository.getActiveAccount()
            if (active != null) {
                val loaded = repository.loadActivePlaylist()
                if (loaded) {
                    refreshContents()
                    _uiState.value = IPTVUiState.Success
                    _currentScreen.value = Screen.HOME
                } else {
                    _uiState.value = IPTVUiState.Idle
                    _currentScreen.value = Screen.LOGIN
                }
            } else {
                _uiState.value = IPTVUiState.Idle
                _currentScreen.value = Screen.LOGIN
            }
        }
    }
}
"""
content = content.replace(target_end, replace_end)

with open('app/src/main/java/com/example/ui/IPTVViewModel.kt', 'w') as f:
    f.write(content)
