package com.example.homeassistant.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.homeassistant.R
import com.example.homeassistant.datasource.bluetooth.BluetoothStatusDataSource
import com.example.homeassistant.datasource.permission.PhonePermissionDataSource
import com.example.homeassistant.utils.showBluetoothPermissionRationale
import com.example.homeassistant.repository.bluetooth.BluetoothRepository
import com.example.homeassistant.ui.viewmodel.bluetooth.BluetoothViewModel

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var bluetoothRepository: BluetoothRepository
    private val bluetoothViewModel: BluetoothViewModel by viewModels {
        BluetoothViewModel.BluetoothViewModelFactory(bluetoothRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        StartupChecksActivity.startActivity(this)
        val phonePermissionDataSource = PhonePermissionDataSource(this)
        val bluetoothStatusDataSource = BluetoothStatusDataSource(this, lifecycleScope)
        bluetoothRepository = BluetoothRepository(
            phonePermissionDataSource,
            bluetoothStatusDataSource
        )

        val retryButton = findViewById<Button>(R.id.retry_button)
        val bluetoothStatusTextView = findViewById<TextView>(R.id.bluetooth_status)

        bluetoothViewModel.getBluetoothStatus().observe(this) { bluetoothStatus ->
            bluetoothStatusTextView.text = bluetoothStatus.toString()
        }

        retryButton.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(TAG, "Bluetooth permission already granted")
            } else {
                Log.d(TAG, "Show bluetooth permission rationale")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    showBluetoothPermissionRationale(this)
                }
            }
        }
    }
}
