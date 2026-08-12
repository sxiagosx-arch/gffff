package com.example.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.network.IPTVRepository
import com.example.database.IPTVDatabase

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val database = IPTVDatabase.getDatabase(applicationContext)
        val repository = IPTVRepository(applicationContext)
        
        return try {
            val loaded = repository.loadActivePlaylist()
            if (loaded) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
