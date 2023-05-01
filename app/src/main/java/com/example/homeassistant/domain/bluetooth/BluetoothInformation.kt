package com.example.homeassistant.domain.bluetooth

import com.example.homeassistant.repository.model.BluetoothStatus

data class BluetoothInformation(
    val status: BluetoothStatus,
    val deviceAddress: String
)