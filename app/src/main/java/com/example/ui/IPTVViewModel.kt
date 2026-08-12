package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.database.BlockedItem
import com.example.database.Favorite
import com.example.database.PlaylistAccount
import com.example.database.WatchHistory
import com.example.model.IPTVCategory
import com.example.model.IPTVChannel
import com.example.model.IPTVSeason
import com.example.model.IPTVSeries
import com.example.network.IPTVRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.async

enum class Screen {
    SPLASH,
    DEVICE_SELECTION,
    LOGIN,
    HOME,
    LIVE_TV,
    MOVIES,
    SERIES,
    PLATFORMS,
    FAVORITES,
    HISTORY,
    PARENTAL_CONTROL,
    SETTINGS,
    ABOUT
}

sealed interface IPTVUiState {
    object Idle : IPTVUiState
    object Loading : IPTVUiState
    object Success : IPTVUiState
    data class Error(val message: String) : IPTVUiState
}

class IPTVViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = IPTVRepository(application)

    // UI State & Flow Exposing
    private val _uiState = MutableStateFlow<IPTVUiState>(IPTVUiState.Idle)
    val uiState: StateFlow<IPTVUiState> = _uiState.asStateFlow()

    private val _deviceLayoutMode = MutableStateFlow("UNSET")
    val deviceLayoutMode: StateFlow<String> = _deviceLayoutMode.asStateFlow()

    private val _currentScreen = MutableStateFlow(Screen.SPLASH) // or LOGIN/HOME, will be updated instantly
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()
    
    private val _isLoadingApp = MutableStateFlow(true)
    val isLoadingApp: StateFlow<Boolean> = _isLoadingApp.asStateFlow()

    // Loaded Content Lists
    private val _channels = MutableStateFlow<List<IPTVChannel>>(emptyList())
    val channels: StateFlow<List<IPTVChannel>> = _channels.asStateFlow()

    private val _categories = MutableStateFlow<List<IPTVCategory>>(emptyList())
    val categories: StateFlow<List<IPTVCategory>> = _categories.asStateFlow()
    
    private val _allCategories = MutableStateFlow<List<IPTVCategory>>(emptyList())
    val allCategories: StateFlow<List<IPTVCategory>> = _allCategories.asStateFlow()

    private val _seriesList = MutableStateFlow<List<IPTVSeries>>(emptyList())
    val seriesList: StateFlow<List<IPTVSeries>> = _seriesList.asStateFlow()

    // Selected Items for Details/Playback
    private val _selectedChannel = MutableStateFlow<IPTVChannel?>(null)
    val selectedChannel: StateFlow<IPTVChannel?> = _selectedChannel.asStateFlow()

    private val _selectedSeries = MutableStateFlow<IPTVSeries?>(null)
    val selectedSeries: StateFlow<IPTVSeries?> = _selectedSeries.asStateFlow()

    private val _seriesSeasons = MutableStateFlow<List<IPTVSeason>>(emptyList())
    val seriesSeasons: StateFlow<List<IPTVSeason>> = _seriesSeasons.asStateFlow()
    private val _currentEPG = MutableStateFlow<List<com.example.model.EPGProgram>>(emptyList())
    val currentEPG: StateFlow<List<com.example.model.EPGProgram>> = _currentEPG.asStateFlow()

    // Navigation and drawer state
    private val _isDrawerOpen = MutableStateFlow(false)
    val isDrawerOpen: StateFlow<Boolean> = _isDrawerOpen.asStateFlow()

    // Playlist Accounts from Room
    val accounts: StateFlow<List<PlaylistAccount>> = repository.accountsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeAccount: StateFlow<PlaylistAccount?> = repository.activeAccountFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Favorites & History from Room
    private val _favorites = MutableStateFlow<List<Favorite>>(emptyList())
    val favorites: StateFlow<List<Favorite>> = _favorites.asStateFlow()

    private val _watchHistory = MutableStateFlow<List<WatchHistory>>(emptyList())
    val watchHistory: StateFlow<List<WatchHistory>> = _watchHistory.asStateFlow()

    // Parental Block lists
    private val _blockedItems = MutableStateFlow<List<BlockedItem>>(emptyList())
    val blockedItems: StateFlow<List<BlockedItem>> = _blockedItems.asStateFlow()
    
    private val _accountExpiration = MutableStateFlow("Desconhecido")
    val accountExpiration: StateFlow<String> = _accountExpiration.asStateFlow()

    // Searching
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Filter Platform shortcuts (Netflix/Disney/Prime/Globoplay style filtering)
    private val _selectedPlatformFilter = MutableStateFlow<String?>(null)
    val selectedPlatformFilter: StateFlow<String?> = _selectedPlatformFilter.asStateFlow()
    fun setPlatformFilter(filter: String?) { _selectedPlatformFilter.value = filter }

    // Navigation BackStack
    private val backStack = mutableListOf<Screen>()
    
    // Preferences from Room
    private val _blockAdult = MutableStateFlow(false)
    val blockAdult: StateFlow<Boolean> = _blockAdult.asStateFlow()
    
    private val _hardwareDecoding = MutableStateFlow(true)
    val hardwareDecoding: StateFlow<Boolean> = _hardwareDecoding.asStateFlow()
    
    private val _bufferSize = MutableStateFlow("Médio (Padrão)")
    val bufferSize: StateFlow<String> = _bufferSize.asStateFlow()    
    fun setBlockAdult(block: Boolean) {
        _blockAdult.value = block
        viewModelScope.launch { 
            repository.setSetting("blockAdult", block.toString())
            refreshContents() 
        }
    }
    
    fun setHardwareDecoding(enabled: Boolean) {
        _hardwareDecoding.value = enabled
        viewModelScope.launch { repository.setSetting("hardwareDecoding", enabled.toString()) }
    }
    
    fun setBufferSize(size: String) {
        _bufferSize.value = size
        viewModelScope.launch { repository.setSetting("bufferSize", size) }
    }

    init {
        viewModelScope.launch {
            _deviceLayoutMode.value = repository.getSetting("deviceLayoutMode", "UNSET")
            _blockAdult.value = repository.getSetting("blockAdult", "false").toBoolean()
            _hardwareDecoding.value = repository.getSetting("hardwareDecoding", "true").toBoolean()
            _bufferSize.value = repository.getSetting("bufferSize", "Médio (Padrão)")        }
        // Start Splash Initialization Animation
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            try {
                
                // Read all properties sequentially first
                val currentDeviceMode = repository.getSetting("deviceLayoutMode", "UNSET")
                _deviceLayoutMode.value = currentDeviceMode
                _blockAdult.value = repository.getSetting("blockAdult", "false").toBoolean()
                _hardwareDecoding.value = repository.getSetting("hardwareDecoding", "true").toBoolean()
                _bufferSize.value = repository.getSetting("bufferSize", "Médio (Padrão)")
                
                val active = repository.getActiveAccount()
                var nextScreen = Screen.LOGIN
                var nextState: IPTVUiState = IPTVUiState.Idle
                var isLoaded = false

                if (active != null) {
                    val loaded = repository.loadActivePlaylist()
                    if (loaded) {
                        _accountExpiration.value = repository.getAccountExpiration()
                        refreshContents()
                        nextState = IPTVUiState.Success
                        nextScreen = Screen.HOME
                        isLoaded = true
                    }
                }
                
                val elapsed = System.currentTimeMillis() - startTime
                val targetDelay = if (isLoaded) 1500L else 800L
                if (elapsed < targetDelay) {
                    delay(targetDelay - elapsed)
                }
                
                // Set the current screen now that we know where to go
                if (currentDeviceMode == "UNSET") {
                    _uiState.value = IPTVUiState.Idle
                    _currentScreen.value = Screen.DEVICE_SELECTION
                } else {
                    _uiState.value = nextState
                    _currentScreen.value = nextScreen
                }
            } catch (e: Exception) {
                e.printStackTrace()
                val elapsed = System.currentTimeMillis() - startTime
                if (elapsed < 1200L) {
                    delay(1200L - elapsed)
                }
                if (_deviceLayoutMode.value == "UNSET") {
                    _currentScreen.value = Screen.DEVICE_SELECTION
                } else {
                    _currentScreen.value = Screen.LOGIN
                }
            } finally {
                _isLoadingApp.value = false
            }
        }

        // Keep Favorites and Watch History Syncing with Room
        viewModelScope.launch {
            activeAccount.collectLatest { acc ->
                if (acc != null) {
                    repository.getFavoritesFlow(acc.id).collect { list ->
                        _favorites.value = list
                    }
                }
            }
        }

        viewModelScope.launch {
            activeAccount.collectLatest { acc ->
                if (acc != null) {
                    repository.getWatchHistoryFlow(acc.id).collect { list ->
                        _watchHistory.value = list
                    }
                }
            }
        }

        viewModelScope.launch {
            activeAccount.collectLatest { acc ->
                if (acc != null) {
                    repository.getBlockedItemsFlow(acc.id).collect { list ->
                        _blockedItems.value = list
                        refreshContents()
                    }
                }
            }
        }
    }

    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            backStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
        _isDrawerOpen.value = false
    }

    fun navigateBack() {
        if (backStack.isNotEmpty()) {
            _currentScreen.value = backStack.removeAt(backStack.size - 1)
        }
    }

    fun toggleDrawer() {
        _isDrawerOpen.value = !_isDrawerOpen.value
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectPlatformFilter(platform: String?) {
        _selectedPlatformFilter.value = if (_selectedPlatformFilter.value == platform) null else platform
    }

    // Load list
    private suspend fun refreshContents() {
        val block = _blockAdult.value
        val allChannels = repository.getChannels()
        val allCategories = repository.getCategories()
        val allSeries = repository.getSeries()
        
        var filteredCats = allCategories
        var filteredChans = allChannels
        var filteredSers = allSeries

        if (block) {
            val adultWords = listOf("adult", "+18", "18+", "xxx", "porn", "sex", "erótico", "erotico", "privé", "prive")
            filteredCats = filteredCats.filter { cat -> 
                adultWords.none { word -> cat.name.contains(word, ignoreCase = true) } 
            }
            filteredChans = filteredChans.filter { ch -> 
                val catName = allCategories.find { it.id == ch.categoryId }?.name ?: ""
                adultWords.none { word -> ch.name.contains(word, ignoreCase = true) || catName.contains(word, ignoreCase = true) }
            }
            filteredSers = filteredSers.filter { s ->
                val catName = allCategories.find { it.id == s.categoryId }?.name ?: ""
                adultWords.none { word -> s.name.contains(word, ignoreCase = true) || catName.contains(word, ignoreCase = true) }
            }
        }
        
        _allCategories.value = filteredCats
        
        val hiddenCategoryIds = _blockedItems.value.filter { it.type == "HIDDEN_CATEGORY" || it.type == "CATEGORY" }.map { it.blockId }
        
        _categories.value = filteredCats.filter { !hiddenCategoryIds.contains(it.id) }
        _channels.value = filteredChans.filter { !hiddenCategoryIds.contains(it.categoryId) }
        _seriesList.value = filteredSers.filter { !hiddenCategoryIds.contains(it.categoryId) }
    }

    // Add list or Xtream account
    fun addAccount(account: PlaylistAccount) {
        viewModelScope.launch {
            _uiState.value = IPTVUiState.Loading
            val savedId = repository.saveAccount(account.copy(isActive = true))
            val loaded = repository.loadActivePlaylist()
            if (loaded) {
                _accountExpiration.value = repository.getAccountExpiration()
                refreshContents()
                _currentScreen.value = Screen.HOME
                _uiState.value = IPTVUiState.Success
            } else {
                _uiState.value = IPTVUiState.Error("Falha ao carregar a lista IPTV. Verifique a URL ou credenciais.")
            }
        }
    }

    fun selectAccount(accountId: Int) {
        viewModelScope.launch {
            _uiState.value = IPTVUiState.Loading
            repository.selectAccount(accountId)
            val loaded = repository.loadActivePlaylist()
            if (loaded) {
                _accountExpiration.value = repository.getAccountExpiration()
                refreshContents()
                _currentScreen.value = Screen.HOME
                _uiState.value = IPTVUiState.Success
            } else {
                _uiState.value = IPTVUiState.Error("Falha ao carregar a conta selecionada.")
            }
        }
    }

    fun deleteAccount(accountId: Int) {
        viewModelScope.launch {
            repository.deleteAccount(accountId)
            val active = repository.getActiveAccount()
            if (active == null) {
                _currentScreen.value = Screen.LOGIN
            }
        }
    }

    // Try demo list
    fun tryDemo() {
        viewModelScope.launch {
            _uiState.value = IPTVUiState.Loading
            val demoAcc = PlaylistAccount(
                name = "Lista de Teste Unlock",
                type = "DEMO",
                isActive = true
            )
            repository.saveAccount(demoAcc)
            val loaded = repository.loadActivePlaylist()
            if (loaded) {
                _accountExpiration.value = repository.getAccountExpiration()
                refreshContents()
                _currentScreen.value = Screen.HOME
                _uiState.value = IPTVUiState.Success
            } else {
                _uiState.value = IPTVUiState.Error("Falha ao carregar lista de demonstração.")
            }
        }
    }

    fun showError(message: String) {
        _uiState.value = IPTVUiState.Error(message)
    }

    fun clearError() {
        _uiState.value = IPTVUiState.Idle
    }

    // Selection handlers for playback/details
    fun selectChannel(channel: IPTVChannel?) {
        _currentEPG.value = emptyList()
        if (channel != null) {
            if (channel.type == "LIVE") {
                viewModelScope.launch {
                    _currentEPG.value = repository.fetchEPG(channel.id)
                }
            } else if (channel.type == "SERIES" && channel.seriesId.isNotEmpty()) {
                viewModelScope.launch {
                    val seasons = repository.fetchSeriesSeasonsAndEpisodes(channel.seriesId)
                    _seriesSeasons.value = seasons
                }
            }
        }
        _selectedChannel.value = channel
    }

    fun selectSeries(series: IPTVSeries?) {
        _selectedSeries.value = series
        if (series != null) {
            viewModelScope.launch {
                _seriesSeasons.value = repository.fetchSeriesSeasonsAndEpisodes(series.id)
            }
        } else {
            _seriesSeasons.value = emptyList()
        }
    }

    // Save playback progress
    fun saveWatchProgress(channel: IPTVChannel, currentPos: Long, totalDuration: Long) {
        viewModelScope.launch {
            repository.saveWatchProgress(channel, currentPos, totalDuration)
        }
    }

    // Favorites Toggle
    fun toggleFavorite(channel: IPTVChannel) {
        viewModelScope.launch {
            repository.toggleFavorite(channel)
        }
    }

    fun toggleFavoriteSeries(series: IPTVSeries) {
        viewModelScope.launch {
            repository.toggleFavoriteSeries(series)
        }
    }

    // Parental control blocks
    fun toggleCategoryBlock(categoryId: String) {
        viewModelScope.launch {
            repository.toggleCategoryBlock(categoryId)
        }
    }

    fun toggleCategoryHidden(categoryId: String) {
        viewModelScope.launch {
            repository.toggleCategoryHidden(categoryId)
        }
    }

    fun isCategoryBlocked(categoryId: String, categoryName: String): Boolean {
        if (_blockAdult.value) {
            val adultWords = listOf("adult", "+18", "18+", "xxx", "porn", "sex", "erótico", "erotico", "privé", "prive")
            val isAdult = adultWords.any { word -> categoryName.contains(word, ignoreCase = true) }
            if (isAdult) return true
        }
        return _blockedItems.value.any { it.blockId == categoryId && it.type == "CATEGORY" }
    }

    fun isCategoryHidden(categoryId: String): Boolean {
        return _blockedItems.value.any { it.blockId == categoryId && it.type == "HIDDEN_CATEGORY" }
    }

    fun setParentalPin(pin: String, callback: () -> Unit) {
        viewModelScope.launch {
            repository.setParentalPin(pin)
            callback()
        }
    }

    fun checkParentalPin(inputPin: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            val pin = repository.getParentalPin()
            if (pin == inputPin || (pin == null && inputPin == "0000")) {
                onSuccess()
            } else {
                onFailure()
            }
        }
    }

    fun isParentalPinSet(callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val pin = repository.getParentalPin()
            callback(pin != null)
        }
    }

    // Quick channel changing (Skip next/previous in same category or all if LIVE)
    fun getAdjacentChannels(channel: IPTVChannel): List<IPTVChannel> {
        if (channel.type == "LIVE") {
            // Return only live channels from the same category
            return _channels.value.filter { it.categoryId == channel.categoryId && it.type == "LIVE" }
        } else if (channel.type == "SERIES") {
            // Return all episodes of the currently selected series
            return _seriesSeasons.value.flatMap { it.episodes }
        }
        // Movies do not have adjacent channels
        return emptyList()
    }

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
