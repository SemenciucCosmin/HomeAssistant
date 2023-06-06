package com.example.homeassistant.ui.viewmodel

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.homeassistant.domain.bluetooth.BluetoothInformation
import com.example.homeassistant.domain.bluetooth.StatefulBluetoothDevice
import com.example.homeassistant.domain.settings.TemperatureType
import com.example.homeassistant.repository.BluetoothRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream

class BluetoothViewModel(private val bluetoothRepository: BluetoothRepository) : ViewModel() {

    companion object {
        private const val TAG = "BluetoothViewModel"
        private const val BUFFER_SIZE = 256
        private const val DELAY_TIME = 300L
        private const val NO_OFFSET = 0
        private const val TEMPERATURE = 0
        private const val HUMIDITY = 1
        private const val MINIMUM_DATA_SIZE = 2
        private const val DELIMITER = ";"
        private const val NO_INPUT_STREAM_BYTES = 0
        private const val KELVIN_DIFFERENCE = 273.15
        private const val NO_HUMIDITY = 0
    }

    private val _uiState = MutableLiveData<UiState>().apply {
        value = UiState(bluetoothDevices = listOf())
    }
    val uiState: LiveData<UiState>
        get() = _uiState

    fun getBluetoothStatus() = bluetoothRepository.getBluetoothStatus().asLiveData()

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

    @SuppressLint("MissingPermission")
    fun getBluetoothSocket(
        bluetoothAdapter: BluetoothAdapter,
        deviceAddress: String
    ): BluetoothSocket? {
        val selectedDevice = bluetoothAdapter.bondedDevices.firstOrNull {
            it.address == deviceAddress
        }

        return if (selectedDevice != null && selectedDevice.uuids != null) {
            val uuid = selectedDevice.uuids.first().uuid
            selectedDevice.createRfcommSocketToServiceRecord(uuid)
        } else {
            null
        }
    }


    @SuppressLint("MissingPermission")
    fun receiveData(bluetoothSocket: BluetoothSocket) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    bluetoothSocket.connect()
                } catch (e: IOException) {
                    Log.d(TAG, "Bluetooth socket connection failed.")
                    return@withContext
                }

                val socketInputStream: InputStream = bluetoothSocket.inputStream
                val buffer = ByteArray(BUFFER_SIZE)
                var bytes: Int

                while (true) {
                    try {
                        if (socketInputStream.available() > NO_INPUT_STREAM_BYTES) {
                            delay(DELAY_TIME)
                            bytes = socketInputStream.read(buffer)
                            val message = String(buffer, NO_OFFSET, bytes)
                            val data = message.split(DELIMITER)
                            if (data.size >= MINIMUM_DATA_SIZE) {
                                _uiState.postValue(
                                    _uiState.value?.copy(
                                        temperature = data[TEMPERATURE].toDouble() + KELVIN_DIFFERENCE,
                                        humidity = data[HUMIDITY].toDouble().toInt()
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

    fun setTemperatureType(temperatureUnit: String) {
        _uiState.value = _uiState.value?.copy(
            temperatureType = TemperatureType.getByItemType(temperatureUnit)
        )
    }

    data class UiState(
        val bluetoothDevices: List<StatefulBluetoothDevice>,
        val temperature: Double = KELVIN_DIFFERENCE,
        val humidity: Int = NO_HUMIDITY,
        val temperatureType: TemperatureType = TemperatureType.CELSIUS
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
