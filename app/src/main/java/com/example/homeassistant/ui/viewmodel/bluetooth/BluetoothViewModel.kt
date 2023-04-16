package com.example.homeassistant.ui.viewmodel.bluetooth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.homeassistant.repository.bluetooth.BluetoothRepository

class BluetoothViewModel(private val bluetoothRepository: BluetoothRepository) : ViewModel() {

    fun getBluetoothStatus() = bluetoothRepository.getBluetoothStatus().asLiveData()

    class BluetoothViewModelFactory(
        private val bluetoothRepository: BluetoothRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BluetoothViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return BluetoothViewModel(bluetoothRepository) as T
            }
            throw IllegalArgumentException("Unable to construct view model")
        }
    }
}
