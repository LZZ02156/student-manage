package com.rollcall.android

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.rollcall.android.model.AttendanceRecord
import com.rollcall.android.model.Session
import com.rollcall.android.repository.SessionRepository
import com.rollcall.android.view.AutoCheckinButton
import com.rollcall.android.view.CheckinDialog
import com.rollcall.android.viewmodel.SessionViewModel
import kotlinx.coroutines.launch

@Composable
fun SessionDetailScreen(sessionId: Long, onClose: () -> Unit, sessionsVm: SessionViewModel) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val sessionState = remember { mutableStateOf<Session?>(null) }
    val attendance by sessionsVm.attendance.collectAsState()
    var showCheckinDialog by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableStateOf(0f) }

    // photo picker
    val pickLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            // upload and then checkin with proofUrl
            scope.launch {
                val repo = SessionRepository(context)
                val uploadResp = repo.uploadFile(uri) { uploaded, total ->
                    uploadProgress = uploaded.toFloat() / total.toFloat()
                }
                if (uploadResp.isSuccessful) {
                    val body = uploadResp.body()
                    val url = body?.url
                    sessionsVm.checkin(sessionId, null, null, url)
                    Toast.makeText(context, "Uploaded and checkin requested", Toast.LENGTH_SHORT).show()
                    sessionsVm.loadAttendance(sessionId)
                } else {
                    Toast.makeText(context, "Upload failed", Toast.LENGTH_SHORT).show()
                }
                uploadProgress = 0f
            }
        }
    }

    LaunchedEffect(sessionId) {
        val token = com.rollcall.android.storage.TokenStorage.getToken()
        val bearer = if (token != null) "Bearer $token" else ""
        // load session details and attendance
        sessionsVm.loadSession(bearer, sessionId)
        sessionsVm.loadAttendance(sessionId)
    }

    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Session: $sessionId")
                Button(onClick = onClose) { Text("Close") }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { showCheckinDialog = true }) { Text("Manual Checkin") }
            Spacer(modifier = Modifier.height(8.dp))
            AutoCheckinButton(sessionId = sessionId, sessionVm = sessionsVm, onResult = { msg ->
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                sessionsVm.loadAttendance(sessionId)
            })
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { pickLauncher.launch("image/*") }) { Text("Choose Photo & Checkin") }
            if (uploadProgress > 0f) {
                LinearProgressIndicator(progress = uploadProgress, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("Attendance:")
            LazyColumn(modifier = Modifier.height(200.dp)) {
                items(attendance) { a: AttendanceRecord ->
                    Column(modifier = Modifier.padding(4.dp)) {
                        Text("${a.username} - ${a.status} - ${a.checkinTime}")
                        if (!a.proofUrl.isNullOrEmpty()) {
                            AsyncImage(model = "http://10.0.2.2:8080" + a.proofUrl, contentDescription = "proof", modifier = Modifier.height(80.dp).width(80.dp))
                        }
                    }
                }
            }
        }
    }

    if (showCheckinDialog) {
        CheckinDialog(onConfirm = { lat, lng ->
            sessionsVm.checkin(sessionId, lat, lng, null)
            sessionsVm.loadAttendance(sessionId)
            showCheckinDialog = false
        }, onDismiss = { showCheckinDialog = false })
    }
}
