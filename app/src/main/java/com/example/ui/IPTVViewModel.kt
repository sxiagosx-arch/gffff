package com.example.ui

import android.app.Application
import android.provider.Settings
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

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
    private val context = application.applicationContext

    // UI State & Flow Exposing
    private val _uiState = MutableStateFlow<IPTVUiState>(IPTVUiState.Idle)
    val uiState: StateFlow<IPTVUiState> = _uiState.asStateFlow()

    private val _deviceLayoutMode = MutableStateFlow("UNSET")
    val deviceLayoutMode: StateFlow<String> = _deviceLayoutMode.asStateFlow()

    private val _currentScreen = MutableStateFlow(Screen.SPLASH)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _isLoadingApp = MutableStateFlow(true)
    val isLoadingApp: StateFlow<Boolean> = _isLoadingApp.asStateFlow()

    private val _channels = MutableStateFlow<List<IPTVChannel>>(emptyList())
    val channels: StateFlow<List<IPTVChannel>> = _channels.asStateFlow()

    private val _categories = MutableStateFlow<List<IPTVCategory>>(emptyList())
    val categories: StateFlow<List<IPTVCategory>> = _categories.asStateFlow()

    private val _allCategories = MutableStateFlow<List<IPTVCategory>>(emptyList())
    val allCategories: StateFlow<List<IPTVCategory>> = _allCategories.asStateFlow()

    private val _seriesList = MutableStateFlow<List<IPTVSeries>>(emptyList())
    val seriesList: StateFlow<List<IPTVSeries>> = _seriesList.asStateFlow()

    private val _selectedChannel = MutableStateFlow<IPTVChannel?>(null)
    val selectedChannel: StateFlow<IPTVChannel?> = _selectedChannel.asStateFlow()

    private val _selectedSeries = MutableStateFlow<IPTVSeries?>(null)
    val selectedSeries: StateFlow<IPTVSeries?> = _selectedSeries.asStateFlow()

    private val _seriesSeasons = MutableStateFlow<List<IPTVSeason>>(emptyList())
    val seriesSeasons: StateFlow<List<IPTVSeason>> = _seriesSeasons.asStateFlow()
    private val _currentEPG = MutableStateFlow<List<com.example.model.EPGProgram>>(emptyList())
    val currentEPG: StateFlow<List<com.example.model.EPGProgram>> = _currentEPG.asStateFlow()

    private val _isDrawerOpen = MutableStateFlow(false)
    val isDrawerOpen: StateFlow<Boolean> = _isDrawerOpen.asStateFlow()

    val accounts: StateFlow<List<PlaylistAccount>> = repository.accountsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeAccount: StateFlow<PlaylistAccount?> = repository.activeAccountFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _favorites = MutableStateFlow<List<Favorite>>(emptyList())
    val favorites: StateFlow<List<Favorite>> = _favorites.asStateFlow()

    private val _watchHistory = MutableStateFlow<List<WatchHistory>>(emptyList())
    val watchHistory: StateFlow<List<WatchHistory>> = _watchHistory.asStateFlow()

    private val _blockedItems = MutableStateFlow<List<BlockedItem>>(emptyList())
    val blockedItems: StateFlow<List<BlockedItem>> = _blockedItems.asStateFlow()

    // Agora vai pegar do servidor!
    private val _accountExpiration = MutableStateFlow("Desconhecido")
    val accountExpiration: StateFlow<String> = _accountExpiration.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedPlatformFilter = MutableStateFlow<String?>(null)
    val selectedPlatformFilter: StateFlow<String?> = _selectedPlatformFilter.asStateFlow()
    fun setPlatformFilter(filter: String?) { _selectedPlatformFilter.value = filter }

    private val backStack = mutableListOf<Screen>()

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
            _bufferSize.value = repository.getSetting("bufferSize", "Médio (Padrão)")
        }

        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            try {
                val currentDeviceMode = repository.getSetting("deviceLayoutMode", "UNSET")
                _deviceLayoutMode.value = currentDeviceMode

                val active = repository.getActiveAccount()
                var nextScreen = Screen.LOGIN
                var nextState: IPTVUiState = IPTVUiState.Idle
                var isLoaded = false

                if (active != null) {
                    // -> CHECAGEM NO SERVIDOR TODA VEZ QUE O APP ABRE
                    val isServerActive = verificarStatusServidor()
                    if (isServerActive) {
                        val loaded = repository.loadActivePlaylist()
                        if (loaded) {
                            refreshContents()
                            nextState = IPTVUiState.Success
                            nextScreen = Screen.HOME
                            isLoaded = true
                        }
                    } else {
                        // Se não estiver ativo (foi pausado ou banido no admin)
                        nextState = IPTVUiState.Error("Assinatura Pausada. Regularize para voltar.")
                        nextScreen = Screen.LOGIN
                    }
                }

                val elapsed = System.currentTimeMillis() - startTime
                val targetDelay = if (isLoaded) 1500L else 800L
                if (elapsed < targetDelay) {
                    delay(targetDelay - elapsed)
                }

                if (currentDeviceMode == "UNSET") {
                    _uiState.value = IPTVUiState.Idle
                    _currentScreen.value = Screen.DEVICE_SELECTION
                } else {
                    _uiState.value = nextState
                    _currentScreen.value = nextScreen
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (_deviceLayoutMode.value == "UNSET") {
                    _currentScreen.value = Screen.DEVICE_SELECTION
                } else {
                    _currentScreen.value = Screen.LOGIN
                }
            } finally {
                _isLoadingApp.value = false
            }
        }

        // Syncs
        viewModelScope.launch {
            activeAccount.collectLatest { acc ->
                if (acc != null) repository.getFavoritesFlow(acc.id).collect { _favorites.value = it }
            }
        }
        viewModelScope.launch {
            activeAccount.collectLatest { acc ->
                if (acc != null) repository.getWatchHistoryFlow(acc.id).collect { _watchHistory.value = it }
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

    // NOVA FUNÇÃO: PERGUNTA PARA O SEU SITE SE A LISTA DELE AINDA ESTÁ ATIVA
    private suspend fun verificarStatusServidor(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "UNKNOWN_ID"
                val deviceId = androidId.uppercase().take(8)
                val apiUrl = "https://lojavip.net/api/verificar_mac/$deviceId"

                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000

                if (connection.responseCode == 200) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val json = JSONObject(response)

                    if (json.optString("status") == "sucesso") {
                        // Puxa a validade direto do seu servidor
                        _accountExpiration.value = json.optString("vencimento", "N/A")
                        return@withContext true
                    }
                }
                return@withContext false
            } catch (e: Exception) {
                // Se der erro de internet, permite passar (já que a lista tá salva)
                return@withContext true
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
            filteredCats = filteredCats.filter { cat -> adultWords.none { word -> cat.name.contains(word, ignoreCase = true) } }
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

    fun addAccount(account: PlaylistAccount) {
        viewModelScope.launch {
            _uiState.value = IPTVUiState.Loading
            repository.saveAccount(account.copy(isActive = true))
            val loaded = repository.loadActivePlaylist()
            if (loaded) {
                // A validade já vem pela verificação, não precisa puxar do repository
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

    fun tryDemo() {
        viewModelScope.launch {
            _uiState.value = IPTVUiState.Loading
            val demoAcc = PlaylistAccount(name = "Lista de Teste Unlock", type = "DEMO", isActive = true)
            repository.saveAccount(demoAcc)
            val loaded = repository.loadActivePlaylist()
            if (loaded) {
                refreshContents()
                _currentScreen.value = Screen.HOME
                _uiState.value = IPTVUiState.Success
            } else {
                _uiState.value = IPTVUiState.Error("Falha ao carregar lista de demonstração.")
            }
        }
    }

    fun showError(message: String) { _uiState.value = IPTVUiState.Error(message) }
    fun clearError() { _uiState.value = IPTVUiState.Idle }

    fun selectChannel(channel: IPTVChannel?) {
        _currentEPG.value = emptyList()
        if (channel != null) {
            if (channel.type == "LIVE") {
                viewModelScope.launch { _currentEPG.value = repository.fetchEPG(channel.id) }
            } else if (channel.type == "SERIES" && channel.seriesId.isNotEmpty()) {
                viewModelScope.launch { _seriesSeasons.value = repository.fetchSeriesSeasonsAndEpisodes(channel.seriesId) }
            }
        }
        _selectedChannel.value = channel
    }

    fun selectSeries(series: IPTVSeries?) {
        _selectedSeries.value = series
        if (series != null) {
            viewModelScope.launch { _seriesSeasons.value = repository.fetchSeriesSeasonsAndEpisodes(series.id) }
        } else {
            _seriesSeasons.value = emptyList()
        }
    }

    fun saveWatchProgress(channel: IPTVChannel, currentPos: Long, totalDuration: Long) {
        viewModelScope.launch { repository.saveWatchProgress(channel, currentPos, totalDuration) }
    }

    fun toggleFavorite(channel: IPTVChannel) { viewModelScope.launch { repository.toggleFavorite(channel) } }
    fun toggleFavoriteSeries(series: IPTVSeries) { viewModelScope.launch { repository.toggleFavoriteSeries(series) } }
    fun toggleCategoryBlock(categoryId: String) { viewModelScope.launch { repository.toggleCategoryBlock(categoryId) } }
    fun toggleCategoryHidden(categoryId: String) { viewModelScope.launch { repository.toggleCategoryHidden(categoryId) } }

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
        viewModelScope.launch { repository.setParentalPin(pin); callback() }
    }

    fun checkParentalPin(inputPin: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            val pin = repository.getParentalPin()
            if (pin == inputPin || (pin == null && inputPin == "0000")) onSuccess() else onFailure()
        }
    }

    fun isParentalPinSet(callback: (Boolean) -> Unit) {
        viewModelScope.launch { callback(repository.getParentalPin() != null) }
    }

    fun getAdjacentChannels(channel: IPTVChannel): List<IPTVChannel> {
        if (channel.type == "LIVE") return _channels.value.filter { it.categoryId == channel.categoryId && it.type == "LIVE" }
        else if (channel.type == "SERIES") return _seriesSeasons.value.flatMap { it.episodes }
        return emptyList()
    }

    fun setDeviceLayoutMode(mode: String) {
        viewModelScope.launch {
            repository.setSetting("deviceLayoutMode", mode)
            _deviceLayoutMode.value = mode
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