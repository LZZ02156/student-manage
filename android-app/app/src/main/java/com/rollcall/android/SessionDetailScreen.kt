package com.rollcall.android

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.rollcall.android.model.AttendanceRecord
import com.rollcall.android.model.Session
import com.rollcall.android.repository.SessionRepository
import com.rollcall.android.view.AutoCheckinButton
import com.rollcall.android.view.CheckinDialog
import com.rollcall.android.viewmodel.SessionViewModel
import kotlinx.coroutines.launch

@Composable
fun SessionListScreen(token: String, sessionsVm: SessionViewModel) {
    val state by sessionsVm.state.collectAsState()
    val lastStatus by sessionsVm.lastCheckinStatus.collectAsState()
    val context = LocalContext.current
    var selectedSessionId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) { sessionsVm.loadSessions() }

    when (state) {
        is com.rollcall.android.viewmodel.SessionsState.Loading -> CircularProgressIndicator()
        is com.rollcall.android.viewmodel.SessionsState.Loaded -> {
            val list = (state as com.rollcall.android.viewmodel.SessionsState.Loaded).sessions
            Column {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(list) { s ->
                        SessionRow(s, onClick = { selectedSessionId = s.id })
                    }
                }
                if (lastStatus != null) {
                    Text("Last checkin: $lastStatus", modifier = Modifier.padding(8.dp))
                }
            }

            if (selectedSessionId != null) {
                SessionDetailScreen(sessionId = selectedSessionId!!, onClose = { selectedSessionId = null }, sessionsVm = sessionsVm)
            }
        }
        is com.rollcall.android.viewmodel.SessionsState.Error -> Text("Error: ${(state as com.rollcall.android.viewmodel.SessionsState.Error).message}")
        else -> Text("No sessions")
    }
}

@Composable
fun SessionRow(s: Session, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(s.title ?: "(no title)")
            Text("by ${s.creator}")
        }
    }
}

@Composable
fun SessionDetailScreen(sessionId: Long, onClose: () -> Unit, sessionsVm: SessionViewModel) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val sessionState = remember { mutableStateOf<Session?>(null) }
    val attendance by sessionsVm.attendance.collectAsState()
    var showCheckinDialog by remember { mutableStateOf(false) }

    // photo picker
    val pickLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            // upload and then checkin with proofUrl
            scope.launch {
                val repo = SessionRepository(context)
                val uploadResp = repo.uploadFile(uri)
                if (uploadResp.isSuccessful) {
                    val url = uploadResp.body()
                    sessionsVm.checkin(sessionId, null, null, url)
                    Toast.makeText(context, "Uploaded and checkin requested", Toast.LENGTH_SHORT).show()
                    sessionsVm.loadAttendance(sessionId)
                } else {
                    Toast.makeText(context, "Upload failed", Toast.LENGTH_SHORT).show()
                }
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
            Spacer(modifier = Modifier.height(12.dp))
            Text("Attendance:")
            LazyColumn(modifier = Modifier.height(200.dp)) {
                items(attendance) { a: AttendanceRecord ->
                    Text("${a.username} - ${a.status} - ${a.checkinTime}")
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
