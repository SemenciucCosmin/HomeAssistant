package com.example.homeassistant.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class StartupChecksActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "StartupChecksActivity"
    }

    private val bluetoothPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach {
            Log.d(TAG, "${it.key} permission granted: ${it.value}")
        }
        checkLocationPermission()
    }

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        Log.d(TAG, "Location permission granted: $isGranted")
        checkNotificationsPermission()
    }

    private val notificationsPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        Log.d(TAG, "Notifications permission granted: $isGranted")
        MainActivity.startActivity(this)
        this.finish()
        overridePendingTransition(0, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkBluetoothPermission()
    }

    private fun checkBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val bluetoothPermission = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            )
            val permissions = arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN
            )
            if (bluetoothPermission == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Bluetooth permission is already granted")
                checkLocationPermission()
            } else {
                Log.d(TAG, "Launch request for bluetooth permission")
                bluetoothPermissionLauncher.launch(permissions)
            }
        }
    }

    private fun checkLocationPermission() {
        val coarseLocationPermission = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val fineLocationPermission = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (coarseLocationPermission == PackageManager.PERMISSION_GRANTED ||
            fineLocationPermission == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "Location permission is already granted")
            checkNotificationsPermission()
        } else {
            Log.d(TAG, "Launch request for location permission")
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    @SuppressLint("InlinedApi")
    private fun checkNotificationsPermission() {
        val postNotificationsPermission = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        )

        if (postNotificationsPermission == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Notifications permission is already granted")
            MainActivity.startActivity(this)
            this.finish()
            overridePendingTransition(0, 0)
        } else {
            Log.d(TAG, "Launch request for notifications permission")
            notificationsPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
