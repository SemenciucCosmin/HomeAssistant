package com.example.homeassistant.ui.activity

import android.Manifest
import android.content.Intent
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
        fun startActivity(activity: MainActivity) {
            val intent = Intent(activity, StartupChecksActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            }
            activity.startActivity(intent)
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        Log.d(TAG, "Permission granted: $isGranted")
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

            if (bluetoothPermission == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Bluetooth permission is already granted")
                this.finish()
                overridePendingTransition(0, 0)
            } else {
                Log.d(TAG, "Launch request for bluetooth permission")
                permissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
            }
        }
    }
}
