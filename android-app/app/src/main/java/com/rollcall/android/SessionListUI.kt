package com.rollcall.android

import android.widget.Toast
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
import com.rollcall.android.model.Session
import com.rollcall.android.view.AutoCheckinButton
import com.rollcall.android.view.CheckinDialog
import com.rollcall.android.viewmodel.SessionViewModel

@Composable
fun SessionListScreen(token: String, sessionsVm: SessionViewModel) {
    val state by sessionsVm.state.collectAsState()
    val lastStatus by sessionsVm.lastCheckinStatus.collectAsState()
    val context = LocalContext.current
    var showDialogFor by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) { sessionsVm.loadSessions() }

    when (state) {
        is com.rollcall.android.viewmodel.SessionsState.Loading -> CircularProgressIndicator()
        is com.rollcall.android.viewmodel.SessionsState.Loaded -> {
            val list = (state as com.rollcall.android.viewmodel.SessionsState.Loaded).sessions
            Column {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(list) { s -> SessionRow(s, onClick = { showDialogFor = s.id }) }
                }
                if (lastStatus != null) {
                    Text("Last checkin: $lastStatus", modifier = Modifier.padding(8.dp))
                }
            }

            if (showDialogFor != null) {
                CheckinDialog(onConfirm = { lat, lng ->
                    sessionsVm.checkin(showDialogFor!!, lat, lng)
                    showDialogFor = null
                }, onDismiss = { showDialogFor = null })
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
