package com.example.database

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "playlist_accounts")
data class PlaylistAccount(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String, // "XTREAM", "M3U_URL", "M3U_FILE"
    val serverUrl: String = "",
    val username: String = "",
    val password: String = "",
    val m3uUrl: String = "",
    val filePath: String = "",
    val isActive: Boolean = false,
    val lastUpdated: Long = 0
)

@Entity(tableName = "favorites")
data class Favorite(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val accountId: Int,
    val streamId: String,
    val type: String, // "LIVE", "MOVIE", "SERIES"
    val name: String,
    val streamUrl: String = "",
    val logoUrl: String = "",
    val categoryId: String = ""
)

@Entity(tableName = "watch_history")
data class WatchHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val accountId: Int,
    val streamId: String,
    val type: String, // "LIVE", "MOVIE", "SERIES"
    val name: String,
    val streamUrl: String = "",
    val logoUrl: String = "",
    val positionMs: Long = 0,
    val durationMs: Long = 0,
    val lastWatched: Long = System.currentTimeMillis(),
    val seasonNumber: Int = 0,
    val episodeNumber: Int = 0,
    val episodeName: String = "",
    val seriesId: String = ""
)

@Entity(tableName = "blocked_items")
data class BlockedItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val accountId: Int,
    val blockId: String, // Category name, group name or channel ID
    val type: String // "CATEGORY", "CHANNEL", "MOVIE", "SERIES"
)

@Entity(tableName = "app_settings")
data class AppSetting(
    @PrimaryKey val key: String,
    val value: String
)

@Dao
interface IPTVDao {
    // Playlist accounts
    @Query("SELECT * FROM playlist_accounts")
    fun getAllAccountsFlow(): Flow<List<PlaylistAccount>>

    @Query("SELECT * FROM playlist_accounts")
    suspend fun getAllAccounts(): List<PlaylistAccount>

    @Query("SELECT * FROM playlist_accounts WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveAccount(): PlaylistAccount?

    @Query("SELECT * FROM playlist_accounts WHERE isActive = 1 LIMIT 1")
    fun getActiveAccountFlow(): Flow<PlaylistAccount?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: PlaylistAccount): Long

    @Update
    suspend fun updateAccount(account: PlaylistAccount)

    @Query("UPDATE playlist_accounts SET isActive = 0")
    suspend fun deactivateAllAccounts()

    @Query("DELETE FROM playlist_accounts WHERE id = :id")
    suspend fun deleteAccountById(id: Int)

    // Favorites
    @Query("SELECT * FROM favorites WHERE accountId = :accountId")
    fun getFavoritesFlow(accountId: Int): Flow<List<Favorite>>

    @Query("SELECT * FROM favorites WHERE accountId = :accountId AND streamId = :streamId AND type = :type LIMIT 1")
    suspend fun getFavorite(accountId: Int, streamId: String, type: String): Favorite?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: Favorite)

    @Query("DELETE FROM favorites WHERE accountId = :accountId AND streamId = :streamId AND type = :type")
    suspend fun deleteFavorite(accountId: Int, streamId: String, type: String)

    // Watch History
    @Query("SELECT * FROM watch_history WHERE accountId = :accountId ORDER BY lastWatched DESC")
    fun getWatchHistoryFlow(accountId: Int): Flow<List<WatchHistory>>

    @Query("SELECT * FROM watch_history WHERE accountId = :accountId AND streamId = :streamId AND type = :type LIMIT 1")
    suspend fun getWatchHistoryItem(accountId: Int, streamId: String, type: String): WatchHistory?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchHistory(history: WatchHistory)

    @Query("DELETE FROM watch_history WHERE accountId = :accountId AND streamId = :streamId AND type = :type")
    suspend fun deleteWatchHistory(accountId: Int, streamId: String, type: String)

    @Query("DELETE FROM watch_history WHERE accountId = :accountId AND seriesId = :seriesId")
    suspend fun deleteWatchHistoryBySeries(accountId: Int, seriesId: String)

    @Query("DELETE FROM watch_history WHERE accountId = :accountId")
    suspend fun clearWatchHistory(accountId: Int)

    // Blocked Items (Parental Control)
    @Query("SELECT * FROM blocked_items WHERE accountId = :accountId")
    fun getBlockedItemsFlow(accountId: Int): Flow<List<BlockedItem>>

    @Query("SELECT * FROM blocked_items WHERE accountId = :accountId")
    suspend fun getBlockedItems(accountId: Int): List<BlockedItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockedItem(blockedItem: BlockedItem)

    @Query("DELETE FROM blocked_items WHERE accountId = :accountId AND blockId = :blockId AND type = :type")
    suspend fun deleteBlockedItem(accountId: Int, blockId: String, type: String)

    // General App Settings
    @Query("SELECT * FROM app_settings")
    fun getSettingsFlow(): Flow<List<AppSetting>>

    @Query("SELECT value FROM app_settings WHERE `key` = :key LIMIT 1")
    suspend fun getSettingValue(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: AppSetting)
}

@Database(
    entities = [
        PlaylistAccount::class,
        Favorite::class,
        WatchHistory::class,
        BlockedItem::class,
        AppSetting::class
    ],
    version = 1,
    exportSchema = false
)
abstract class IPTVDatabase : RoomDatabase() {
    abstract fun iptvDao(): IPTVDao

    companion object {
        @Volatile
        private var INSTANCE: IPTVDatabase? = null

        fun getDatabase(context: Context): IPTVDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    IPTVDatabase::class.java,
                    "unlock_iptv_database"
                )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
