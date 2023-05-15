package com.example.homeassistant.repository

import android.Manifest
import android.content.Context
import android.os.Build
import com.example.homeassistant.datasource.BluetoothStatusDataSource
import com.example.homeassistant.datasource.DeviceDataSource
import com.example.homeassistant.datasource.PhonePermissionDataSource
import com.example.homeassistant.domain.bluetooth.BluetoothStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BluetoothRepository(
    private val phonePermissionDataSource: PhonePermissionDataSource,
    private val bluetoothStatusDataSource: BluetoothStatusDataSource,
    private val deviceDataSource: DeviceDataSource
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
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S || (phonePermissionDataSource.isPermissionGranted(
            Manifest.permission.BLUETOOTH_CONNECT
        ) && phonePermissionDataSource.isPermissionGranted(Manifest.permission.BLUETOOTH_SCAN))
    }

    fun getDeviceAddress(): Flow<String> = deviceDataSource.deviceAddressFlow

    suspend fun saveDeviceAddressToPreferenceSource(deviceAddress: String, context: Context) =
        deviceDataSource.saveLocationToPreferenceStore(deviceAddress, context)
}
