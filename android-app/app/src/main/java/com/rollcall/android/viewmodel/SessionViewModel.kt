package com.rollcall.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rollcall.android.model.CheckinRequest
import com.rollcall.android.model.Session
import com.rollcall.android.repository.SessionRepository
import com.rollcall.android.storage.TokenStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class SessionsState {
    object Idle: SessionsState()
    object Loading: SessionsState()
    data class Loaded(val sessions: List<Session>): SessionsState()
    data class Error(val message: String): SessionsState()
}

class SessionViewModel: ViewModel() {
    private val repo = SessionRepository()
    private val _state = MutableStateFlow<SessionsState>(SessionsState.Idle)
    val state: StateFlow<SessionsState> = _state

    fun loadSessions() {
        _state.value = SessionsState.Loading
        viewModelScope.launch {
            try {
                val token = TokenStorage.getToken()
                val bearer = if (token != null) "Bearer $token" else ""
                val resp = repo.listSessions(bearer)
                if (resp.isSuccessful) {
                    val list = resp.body() ?: emptyList()
                    _state.value = SessionsState.Loaded(list)
                } else {
                    _state.value = SessionsState.Error("Failed: ${resp.code()}")
                }
            } catch (e: Exception) {
                _state.value = SessionsState.Error(e.message ?: "Unknown")
            }
        }
    }

    fun checkin(sessionId: Long, lat: Double?, lng: Double?) {
        viewModelScope.launch {
            try {
                val token = TokenStorage.getToken()
                val bearer = if (token != null) "Bearer $token" else ""
                val req = CheckinRequest(lat, lng, null, null)
                val resp = repo.checkin(bearer, sessionId, req)
                // ignore response for now; caller can observe result via callbacks
            } catch (_: Exception) {
            }
        }
    }
}
