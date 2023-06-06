package com.example.homeassistant.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import com.example.homeassistant.R

fun showBluetoothPermissionRationale(context: Context) {
    val alertDialogBuilder = AlertDialog.Builder(context, R.style.AlertDialogStyle)
    alertDialogBuilder.apply {
        setTitle(R.string.lbl_bluetooth_permission_rationale_title)
        setMessage(R.string.lbl_bluetooth_permission_rationale_message)
        setPositiveButton(R.string.lbl_settings) { _, _ ->
            startActivity(
                context,
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                ),
                null
            )
        }
        setNegativeButton(R.string.lbl_cancel) { dialog, _ ->
            dialog.cancel()
        }
    }
    val alertDialog = alertDialogBuilder.create()
    alertDialog.show()
}

fun showBluetoothStatusRationale(context: Context) {
    val alertDialogBuilder = AlertDialog.Builder(context, R.style.AlertDialogStyle)
    alertDialogBuilder.apply {
        setTitle(R.string.lbl_bluetooth_status_rationale_title)
        setMessage(R.string.lbl_bluetooth_status_rationale_message)
        setPositiveButton(R.string.lbl_settings) { _, _ ->
            startActivity(
                context,
                Intent(Settings.ACTION_BLUETOOTH_SETTINGS),
                null
            )
        }
        setNegativeButton(R.string.lbl_cancel) { dialog, _ ->
            dialog.cancel()
        }
    }
    val alertDialog = alertDialogBuilder.create()
    alertDialog.show()
}

fun showLocationPermissionRationale(context: Context) {
    val alertDialogBuilder = AlertDialog.Builder(context, R.style.AlertDialogStyle)
    alertDialogBuilder.apply {
        setTitle(R.string.lbl_location_permission_rationale_title)
        setMessage(R.string.lbl_location_permission_rationale_message)
        setPositiveButton(R.string.lbl_settings) { _, _ ->
            startActivity(
                context,
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS),
                null
            )
        }
        setNegativeButton(R.string.lbl_cancel) { dialog, _ ->
            dialog.cancel()
        }
    }
    val alertDialog = alertDialogBuilder.create()
    alertDialog.show()
}

fun showNotificationsPermissionRationale(context: Context) {
    val alertDialogBuilder = AlertDialog.Builder(context, R.style.AlertDialogStyle)
    alertDialogBuilder.apply {
        setTitle(R.string.lbl_notifications_permission_rationale_title)
        setMessage(R.string.lbl_notifications_permission_rationale_message)
        setPositiveButton(R.string.lbl_settings) { _, _ ->
            startActivity(
                context,
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS),
                null
            )
        }
        setNegativeButton(R.string.lbl_cancel) { dialog, _ ->
            dialog.cancel()
        }
    }
    val alertDialog = alertDialogBuilder.create()
    alertDialog.show()
}
