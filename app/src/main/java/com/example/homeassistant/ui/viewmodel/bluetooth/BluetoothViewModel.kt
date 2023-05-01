package com.example.homeassistant.ui.viewmodel.bluetooth

import android.bluetooth.BluetoothSocket
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.homeassistant.domain.bluetooth.BluetoothInformation
import com.example.homeassistant.domain.bluetooth.StatefulBluetoothDevice
import com.example.homeassistant.repository.bluetooth.BluetoothRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream

class BluetoothViewModel(private val bluetoothRepository: BluetoothRepository) : ViewModel() {

    companion object {
        const val BUFFER_SIZE = 256
        const val DELAY_TIME = 300L
        const val NO_OFFSET = 0
        const val TEMPERATURE = 0
        const val HUMIDITY = 1
        const val MINIMUM_DATA_SIZE = 2
        const val DELIMITER = ";"
    }

    private val _uiState = MutableLiveData<UiState>().apply {
        value = UiState(bluetoothDevices = listOf())
    }
    val uiState: LiveData<UiState>
        get() = _uiState

    fun getBluetoothStatus() = bluetoothRepository.getBluetoothStatus().asLiveData()

    fun getDeviceAddress() = bluetoothRepository.getDeviceAddress().asLiveData()

    fun getBluetoothInformation(): LiveData<BluetoothInformation> {
        return bluetoothRepository.getBluetoothStatus()
            .combine(bluetoothRepository.getDeviceAddress()) { status, address ->
                BluetoothInformation(status, address)
            }.asLiveData()
    }

    fun saveDeviceAddressToPreferenceSource(deviceAddress: String, context: Context) {
        viewModelScope.launch {
            bluetoothRepository.saveDeviceAddressToPreferenceSource(deviceAddress, context)
        }
    }

    fun addBluetoothDevice(newBluetoothDevice: StatefulBluetoothDevice) {
        val bluetoothDevices = mutableListOf<StatefulBluetoothDevice>()
        _uiState.value?.bluetoothDevices?.forEach { bluetoothBluetoothDevice ->
            bluetoothDevices.add(bluetoothBluetoothDevice)
        }
        bluetoothDevices.add(newBluetoothDevice)
        _uiState.value = _uiState.value?.copy(
            bluetoothDevices = bluetoothDevices
        )
    }

    fun removeBluetoothDevice(newBluetoothDevice: StatefulBluetoothDevice) {
        val bluetoothDevices = _uiState.value?.bluetoothDevices?.filter {
            it.device.address != newBluetoothDevice.device.address
        }
        _uiState.value = bluetoothDevices?.let {
            _uiState.value?.copy(
                bluetoothDevices = it
            )
        }
    }


    fun receiveData(bluetoothSocket: BluetoothSocket) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val socketInputStream: InputStream = bluetoothSocket.inputStream
                val buffer = ByteArray(BUFFER_SIZE)
                var bytes: Int

                while (true) {
                    try {
                        if (socketInputStream.available() > 0) {
                            delay(DELAY_TIME)
                            bytes = socketInputStream.read(buffer)
                            val message = String(buffer, NO_OFFSET, bytes)
                            val data = message.split(DELIMITER)
                            if (data.size >= MINIMUM_DATA_SIZE) {
                                _uiState.postValue(
                                    _uiState.value?.copy(
                                        temperature = data[TEMPERATURE].toFloat(),
                                        humidity = data[HUMIDITY].toFloat()
                                    )
                                )
                            }
                        }
                    } catch (e: IOException) {
                        break
                    }
                }
            }
        }
    }

    data class UiState(
        val bluetoothDevices: List<StatefulBluetoothDevice>,
        val temperature: Float = 0f,
        val humidity: Float = 0f
    )

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
