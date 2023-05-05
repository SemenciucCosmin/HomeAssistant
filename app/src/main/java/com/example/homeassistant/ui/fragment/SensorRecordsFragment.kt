package com.example.homeassistant.ui.fragment

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.homeassistant.R
import com.example.homeassistant.datasource.bluetooth.BluetoothStatusDataSource
import com.example.homeassistant.datasource.bluetooth.DeviceDataSource
import com.example.homeassistant.datasource.permission.PhonePermissionDataSource
import com.example.homeassistant.repository.bluetooth.BluetoothRepository
import com.example.homeassistant.repository.bluetooth.model.BluetoothStatus
import com.example.homeassistant.ui.viewmodel.bluetooth.BluetoothViewModel
import com.example.homeassistant.utils.showBluetoothPermissionRationale
import com.example.homeassistant.utils.showBluetoothStatusRationale
import java.io.IOException

class SensorRecordsFragment : Fragment() {
    companion object {
        private const val TAG = "SensorRecordsFragment"
    }

    private var bluetoothSocket: BluetoothSocket? = null
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothRepository: BluetoothRepository
    private val bluetoothViewModel: BluetoothViewModel by viewModels {
        BluetoothViewModel.BluetoothViewModelFactory(bluetoothRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sensor_records, container, false)
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val temperatureTextView = view.findViewById<TextView>(R.id.temperature)
        val humidityTextView = view.findViewById<TextView>(R.id.humidity)

        val phonePermissionDataSource = PhonePermissionDataSource(requireContext())
        val bluetoothStatusDataSource = BluetoothStatusDataSource(requireContext(), lifecycleScope)
        val deviceDataSource = DeviceDataSource(requireContext())
        bluetoothRepository = BluetoothRepository(
            phonePermissionDataSource,
            bluetoothStatusDataSource,
            deviceDataSource
        )

        bluetoothManager = requireActivity().getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter

        bluetoothViewModel.getBluetoothInformation()
            .observe(viewLifecycleOwner) { bluetoothInformation ->
                when (bluetoothInformation.status) {
                    BluetoothStatus.ON -> {
                        if (bluetoothSocket?.isConnected == false || bluetoothSocket == null) {
                            val selectedDevice = bluetoothAdapter.bondedDevices.firstOrNull {
                                it.address == bluetoothInformation.deviceAddress
                            }
                            if (selectedDevice != null && selectedDevice.uuids != null) {
                                val uuid = selectedDevice.uuids.first().uuid
                                bluetoothSocket =
                                    selectedDevice.createRfcommSocketToServiceRecord(uuid)
                                bluetoothSocket?.let { socket ->
                                    try {
                                        socket.connect()
                                        bluetoothViewModel.receiveData(socket)
                                    } catch (e: IOException) {
                                        Log.d(TAG, "Bluetooth socket connection failed.")
                                    }
                                }
                            }
                        }
                    }

                    BluetoothStatus.OFF -> {
                        showBluetoothStatusRationale(requireContext())
                    }

                    BluetoothStatus.NOT_GRANTED -> {
                        showBluetoothPermissionRationale(requireContext())
                    }
                }
            }

        bluetoothViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            temperatureTextView.text = uiState.temperature.toString()
            humidityTextView.text = uiState.humidity.toString()
        }
    }
}
