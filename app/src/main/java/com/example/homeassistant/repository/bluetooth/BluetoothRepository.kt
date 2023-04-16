package com.example.homeassistant.repository.bluetooth

import android.Manifest
import android.os.Build
import com.example.homeassistant.datasource.bluetooth.BluetoothStatusDataSource
import com.example.homeassistant.datasource.permission.PhonePermissionDataSource
import com.example.homeassistant.repository.model.BluetoothStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BluetoothRepository(
    private val phonePermissionDataSource: PhonePermissionDataSource,
    private val bluetoothStatusDataSource: BluetoothStatusDataSource
) {
    fun getBluetoothStatus(): Flow<BluetoothStatus> {
        return bluetoothStatusDataSource.isBluetoothEnabled().map { isBluetoothEnabled ->
            when {
                !isPermissionGranted() -> BluetoothStatus.NOT_GRANTED
                !isBluetoothEnabled -> BluetoothStatus.OFF
                else -> BluetoothStatus.ON
            }
        }
    }

    private suspend fun isPermissionGranted(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                phonePermissionDataSource.isPermissionGranted(Manifest.permission.BLUETOOTH_CONNECT)
    }
}
