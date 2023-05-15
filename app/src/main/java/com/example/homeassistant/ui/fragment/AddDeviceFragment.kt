package com.example.homeassistant.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homeassistant.R
import com.example.homeassistant.datasource.BluetoothStatusDataSource
import com.example.homeassistant.datasource.DeviceDataSource
import com.example.homeassistant.datasource.PhonePermissionDataSource
import com.example.homeassistant.domain.bluetooth.BluetoothStatus
import com.example.homeassistant.domain.bluetooth.StatefulBluetoothDevice
import com.example.homeassistant.repository.BluetoothRepository
import com.example.homeassistant.ui.adapter.BluetoothDeviceCallback
import com.example.homeassistant.ui.adapter.BluetoothDevicesAdapter
import com.example.homeassistant.ui.viewmodel.BluetoothViewModel
import com.example.homeassistant.utils.showBluetoothPermissionRationale
import com.example.homeassistant.utils.showBluetoothStatusRationale


class AddDeviceFragment : Fragment(), BluetoothDeviceCallback {
    companion object {
        private const val TAG = "AddDeviceFragment"
    }

    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothRepository: BluetoothRepository
    private val bluetoothViewModel: BluetoothViewModel by viewModels {
        BluetoothViewModel.BluetoothViewModelFactory(bluetoothRepository)
    }
    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(
                        BluetoothDevice.EXTRA_DEVICE
                    )
                    device?.let {
                        if (!bluetoothAdapter.bondedDevices.contains(it)) {
                            bluetoothViewModel.addBluetoothDevice(
                                StatefulBluetoothDevice(
                                    showIcon = true,
                                    device = it
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_device, container, false)
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

        bluetoothManager = requireActivity().getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        requireActivity().registerReceiver(receiver, filter)

        bluetoothViewModel.getBluetoothStatus().observe(viewLifecycleOwner) { status ->
            if (status != null) {
                when (status) {
                    BluetoothStatus.ON -> {
                        bluetoothAdapter.startDiscovery()
                    }

                    BluetoothStatus.OFF -> {
                        showBluetoothStatusRationale(requireContext())
                    }

                    BluetoothStatus.NOT_GRANTED -> {
                        showBluetoothPermissionRationale(requireContext())
                    }
                }
            }
        }

        val bluetoothDevicesAdapter = BluetoothDevicesAdapter(this)
        val devicesRecyclerView = view.findViewById<RecyclerView>(R.id.available_bluetooth_devices)
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

        bluetoothViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            bluetoothDevicesAdapter.submitList(uiState.bluetoothDevices)
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        requireActivity().registerReceiver(receiver, filter)
    }

    override fun onPause() {
        super.onPause()
        requireActivity().unregisterReceiver(receiver)
    }

    override fun onBluetoothDeviceClick(bluetoothDevice: StatefulBluetoothDevice) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val bluetoothPermission = ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.BLUETOOTH_CONNECT
            )

            if (bluetoothPermission == PackageManager.PERMISSION_GRANTED) {
                bluetoothDevice.device.createBond()
                bluetoothViewModel.removeBluetoothDevice(bluetoothDevice)
                val toast = Toast.makeText(
                    requireContext(),
                    getString(R.string.msg_device_is_paired),
                    Toast.LENGTH_SHORT
                )
                toast.show()
                Log.d(TAG, "Create bond with device $$bluetoothDevice")
            } else {
                Log.d(TAG, "Launch request for bluetooth permission")
                showBluetoothPermissionRationale(requireContext())
            }
        }
    }
}