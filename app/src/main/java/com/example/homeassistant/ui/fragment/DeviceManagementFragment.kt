package com.example.homeassistant.ui.fragment

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homeassistant.R
import com.example.homeassistant.datasource.bluetooth.BluetoothStatusDataSource
import com.example.homeassistant.datasource.bluetooth.DeviceDataSource
import com.example.homeassistant.datasource.permission.PhonePermissionDataSource
import com.example.homeassistant.domain.bluetooth.StatefulBluetoothDevice
import com.example.homeassistant.repository.bluetooth.BluetoothRepository
import com.example.homeassistant.repository.bluetooth.model.BluetoothStatus
import com.example.homeassistant.ui.adapter.BluetoothDevicesAdapter
import com.example.homeassistant.ui.adapter.model.BluetoothDeviceCallback
import com.example.homeassistant.ui.viewmodel.bluetooth.BluetoothViewModel
import com.example.homeassistant.utils.showBluetoothPermissionRationale
import com.example.homeassistant.utils.showBluetoothStatusRationale

class DeviceManagementFragment : Fragment(), BluetoothDeviceCallback {

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
        return inflater.inflate(R.layout.fragment_device_management, container, false)
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val phonePermissionDataSource = PhonePermissionDataSource(requireContext())
        val bluetoothStatusDataSource = BluetoothStatusDataSource(requireContext(), lifecycleScope)
        val deviceDataSource = DeviceDataSource(requireContext())
        bluetoothRepository = BluetoothRepository(
            phonePermissionDataSource,
            bluetoothStatusDataSource,
            deviceDataSource
        )

        val noPairedDevicesMessage = view.findViewById<TextView>(R.id.no_paired_devices_message)
        val bluetoothDevicesAdapter = BluetoothDevicesAdapter(this)
        val devicesRecyclerView = view.findViewById<RecyclerView>(R.id.paired_bluetooth_devices)
        devicesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bluetoothDevicesAdapter
            val itemDecorator = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.recycler_view_divider
            )?.let { itemDecorator.setDrawable(it) }
            addItemDecoration(itemDecorator)
        }

        bluetoothManager = requireActivity().getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter

        bluetoothViewModel.getBluetoothInformation()
            .observe(viewLifecycleOwner) { bluetoothInformation ->
                when (bluetoothInformation.status) {
                    BluetoothStatus.ON -> {
                        val pairedBluetoothDevices = bluetoothAdapter.bondedDevices.toList().map {
                            StatefulBluetoothDevice(
                                device = it,
                                isSelected = it.address == bluetoothInformation.deviceAddress
                            )
                        }
                        noPairedDevicesMessage.isVisible = pairedBluetoothDevices.isEmpty()
                        bluetoothDevicesAdapter.submitList(pairedBluetoothDevices)
                    }

                    BluetoothStatus.OFF -> {
                        showBluetoothStatusRationale(requireContext())
                    }

                    BluetoothStatus.NOT_GRANTED -> {
                        showBluetoothPermissionRationale(requireContext())
                    }
                }
            }

        val addDeviceButton = view.findViewById<Button>(R.id.add_device)
        addDeviceButton.setOnClickListener {
            findNavController().navigate(R.id.nav_add_device)
        }
    }

    override fun onBluetoothDeviceClick(bluetoothDevice: StatefulBluetoothDevice) {
        bluetoothViewModel.saveDeviceAddressToPreferenceSource(
            bluetoothDevice.device.address,
            requireContext()
        )
    }
}