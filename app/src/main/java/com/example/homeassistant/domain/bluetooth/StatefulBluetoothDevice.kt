package com.example.homeassistant.domain.bluetooth

import android.bluetooth.BluetoothDevice
import com.example.homeassistant.R

data class StatefulBluetoothDevice(
    val iconId: Int = R.drawable.ic_link,
    val showIcon: Boolean = false,
    val device: BluetoothDevice
)