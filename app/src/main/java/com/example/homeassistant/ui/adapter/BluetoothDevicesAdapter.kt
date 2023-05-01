package com.example.homeassistant.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.homeassistant.R
import com.example.homeassistant.domain.bluetooth.StatefulBluetoothDevice
import com.example.homeassistant.ui.adapter.model.BluetoothDeviceCallback
import com.example.homeassistant.ui.viewholder.BluetoothDeviceViewHolder

class BluetoothDevicesAdapter(private val bluetoothDeviceCallback: BluetoothDeviceCallback) :
    ListAdapter<StatefulBluetoothDevice, BluetoothDeviceViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<StatefulBluetoothDevice>() {
            override fun areItemsTheSame(
                oldItem: StatefulBluetoothDevice,
                newItem: StatefulBluetoothDevice
            ): Boolean {
                return oldItem.device.address == newItem.device.address
            }

            override fun areContentsTheSame(
                oldItem: StatefulBluetoothDevice,
                newItem: StatefulBluetoothDevice
            ): Boolean {
                return oldItem.isSelected == newItem.isSelected
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BluetoothDeviceViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.bluetooth_device_item, parent, false)
        return BluetoothDeviceViewHolder(view, bluetoothDeviceCallback)
    }

    override fun onBindViewHolder(holder: BluetoothDeviceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}