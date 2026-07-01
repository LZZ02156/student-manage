package com.rollcall.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rollcall.android.ui.theme.RollcallTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    var token by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "RollCall Android (MVP)", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(20.dp))

        if (token == null) {
            LoginForm(onLogin = { receivedToken -> token = receivedToken })
        } else {
            SessionListScreen(token!!)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { token = null }) {
                Text("Logout")
            }
        }
    }
}

@Composable
fun LoginForm(onLogin: (String) -> Unit) {
    // Simple placeholder UI - in real app use proper text fields
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Demo login (uses /auth/login)")
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            // Demo: generate a placeholder token. Replace with real network call.
            CoroutineScope(Dispatchers.IO).launch {
                onLogin("demo-token")
            }
        }) {
            Text("Login as demo user")
        }
    }
}

@Composable
fun SessionListScreen(token: String) {
    // TODO: call backend to fetch sessions
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Sessions (placeholder) - token: ${token.take(10)}...")
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { /* navigate to checkin screen */ }) {
            Text("Open session (demo)")
        }
    }
}
