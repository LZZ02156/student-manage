package com.rollcall.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rollcall.android.model.Session
import com.rollcall.android.ui.theme.RollcallTheme
import com.rollcall.android.view.CheckinDialog
import com.rollcall.android.viewmodel.AuthState
import com.rollcall.android.viewmodel.AuthViewModel
import com.rollcall.android.viewmodel.SessionViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RollcallTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val authVm: AuthViewModel = viewModel()
    val sessionsVm: SessionViewModel = viewModel()
    val authState by authVm.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "RollCall Android (MVP)", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(20.dp))

        when (authState) {
            is AuthState.Idle -> LoginForm(onLogin = { username, password -> authVm.login(username, password) })
            is AuthState.Loading -> CircularProgressIndicator()
            is AuthState.Success -> {
                SessionListScreen(token = (authState as AuthState.Success).token, sessionsVm = sessionsVm)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { authVm.logout() }) { Text("Logout") }
            }
            is AuthState.Error -> {
                Text("Login error: ${(authState as AuthState.Error).message}")
                LoginForm(onLogin = { username, password -> authVm.login(username, password) })
            }
        }
    }
}

@Composable
fun LoginForm(onLogin: (String, String) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("username") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("password") })
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onLogin(username, password) }) { Text("Login") }
    }
}

@Composable
fun SessionListScreen(token: String, sessionsVm: SessionViewModel) {
    val state by sessionsVm.state.collectAsState()
    LaunchedEffect(Unit) { sessionsVm.loadSessions() }

    when (state) {
        is com.rollcall.android.viewmodel.SessionsState.Loading -> CircularProgressIndicator()
        is com.rollcall.android.viewmodel.SessionsState.Loaded -> {
            val list = (state as com.rollcall.android.viewmodel.SessionsState.Loaded).sessions
            LazyColumn {
                items(list) { s -> SessionRow(s, onClick = { /* open detail */ }) }
            }
        }
        is com.rollcall.android.viewmodel.SessionsState.Error -> Text("Error: ${(state as com.rollcall.android.viewmodel.SessionsState.Error).message}")
        else -> Text("No sessions")
    }
}

@Composable
fun SessionRow(s: Session, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(4.dp).clickable { onClick() }) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(s.title ?: "(no title)")
            Text("by ${s.creator}")
        }
    }
}
