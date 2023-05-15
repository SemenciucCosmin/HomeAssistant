package com.example.homeassistant.ui.adapter

import android.bluetooth.BluetoothDevice
import com.example.homeassistant.domain.bluetooth.StatefulBluetoothDevice

interface BluetoothDeviceCallback {
    fun onBluetoothDeviceClick(bluetoothDevice: StatefulBluetoothDevice)
}