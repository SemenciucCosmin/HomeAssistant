package com.example.homeassistant.domain.bluetooth

import android.bluetooth.BluetoothDevice

data class StatefulBluetoothDevice(
    val isSelected: Boolean = false,
    val device: BluetoothDevice
)