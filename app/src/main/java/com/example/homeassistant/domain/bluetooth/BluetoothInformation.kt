package com.example.homeassistant.domain.bluetooth

data class BluetoothInformation(
    val status: BluetoothStatus,
    val deviceAddress: String
)