package com.example.homeassistant.datasource

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PhonePermissionDataSource(private val context: Context) {
    suspend fun isPermissionGranted(permission: String): Boolean {
        return withContext(Dispatchers.Default) {
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}
