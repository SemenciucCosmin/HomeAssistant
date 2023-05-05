package com.example.homeassistant.ui.viewholder

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.homeassistant.R
import com.example.homeassistant.domain.bluetooth.StatefulBluetoothDevice
import com.example.homeassistant.ui.adapter.model.BluetoothDeviceCallback

class BluetoothDeviceViewHolder(
    private val view: View,
    private val bluetoothDeviceCallback: BluetoothDeviceCallback
) : RecyclerView.ViewHolder(view) {

    private val bluetoothDeviceName = view.findViewById<TextView>(R.id.bluetooth_device_name)
    private val bluetoothDeviceAddress = view.findViewById<TextView>(R.id.bluetooth_device_address)
    private val bluetoothDeviceLayout = view.findViewById<ConstraintLayout>(R.id.bluetooth_device_layout)
    private val bluetoothDeviceIcon = view.findViewById<ImageView>(R.id.bluetooth_device_icon)

    @SuppressLint("MissingPermission")
    fun bind(bluetoothDevice: StatefulBluetoothDevice) {
        bluetoothDeviceName.text = bluetoothDevice.device.name
            ?: view.context.getString(R.string.lbl_empty_bluetooth_device_name)
        bluetoothDeviceAddress.text = bluetoothDevice.device.address
        bluetoothDeviceLayout.setOnClickListener {
            bluetoothDeviceCallback.onBluetoothDeviceClick(bluetoothDevice)
        }
        val visibility = if (bluetoothDevice.showIcon) View.VISIBLE else View.INVISIBLE
        bluetoothDeviceIcon.visibility = visibility
        bluetoothDeviceIcon.background = ContextCompat.getDrawable(
            view.context,
            bluetoothDevice.iconId
        )
    }
}
