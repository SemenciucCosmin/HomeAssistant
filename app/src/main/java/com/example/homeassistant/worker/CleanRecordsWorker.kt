package com.example.homeassistant.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.homeassistant.database.HomeAssistantDatabase

class CleanRecordsWorker(appContext: Context, params: WorkerParameters) : Worker(appContext, params) {
    override fun doWork(): Result {
        val airQualityDao = HomeAssistantDatabase.getDatabase(applicationContext).airQualityDao()
        airQualityDao.removeUnusedRecords()
        return Result.success()
    }
}
