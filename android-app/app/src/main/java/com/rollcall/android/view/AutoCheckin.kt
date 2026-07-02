package com.rollcall.android.view

import android.Manifest
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.rollcall.android.viewmodel.SessionViewModel

@Composable
fun CheckinDialog(onConfirm: (Double?, Double?) -> Unit, onDismiss: () -> Unit) {
    var latText by remember { mutableStateOf("") }
    var lngText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Check-in (manual)") },
        text = {
            Column {
                OutlinedTextField(value = latText, onValueChange = { latText = it }, label = { Text("lat") })
                OutlinedTextField(value = lngText, onValueChange = { lngText = it }, label = { Text("lng") })
            }
        },
        confirmButton = {
            Button(onClick = {
                val lat = latText.toDoubleOrNull()
                val lng = lngText.toDoubleOrNull()
                onConfirm(lat, lng)
            }) { Text("Checkin") }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AutoCheckinButton(sessionId: Long, sessionVm: SessionViewModel, onResult: (String) -> Unit) {
    val context = LocalContext.current
    var lastMessage by remember { mutableStateOf<String?>(null) }

    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                // fetch location
                try {
                    fusedClient.lastLocation.addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            sessionVm.checkin(sessionId, location.latitude, location.longitude)
                            lastMessage = "Check-in requested (lat=${location.latitude}, lng=${location.longitude})"
                            onResult(lastMessage)
                        } else {
                            lastMessage = "无法获取当前位置"
                            onResult(lastMessage)
                        }
                    }.addOnFailureListener { ex ->
                        lastMessage = "定位失败: ${ex.message}"
                        onResult(lastMessage)
                    }
                } catch (e: SecurityException) {
                    lastMessage = "无法获取定位权限"
                    onResult(lastMessage)
                }
            } else {
                lastMessage = "定位权限被拒绝"
                onResult(lastMessage)
            }
        }
    )

    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        Button(onClick = { permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }, modifier = Modifier.weight(1f)) {
            Text("Auto Check-in (use location)")
        }
    }
}
