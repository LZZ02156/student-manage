package com.rollcall.android.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
