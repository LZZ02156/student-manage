package com.rollcall.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rollcall.android.model.AttendanceRecord
import com.rollcall.android.model.CheckinRequest
import com.rollcall.android.model.Session
import com.rollcall.android.repository.SessionRepository
import com.rollcall.android.storage.TokenStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.content.Context

class SessionViewModel(private val context: Context? = null): ViewModel() {
    private val repo = if (context != null) SessionRepository(context) else null
    private val _state = MutableStateFlow<SessionsState>(SessionsState.Idle)
    val state: StateFlow<SessionsState> = _state

    private val _lastCheckinStatus = MutableStateFlow<String?>(null)
    val lastCheckinStatus: StateFlow<String?> = _lastCheckinStatus

    private val _attendance = MutableStateFlow<List<AttendanceRecord>>(emptyList())
    val attendance: StateFlow<List<AttendanceRecord>> = _attendance

    private val _selectedSession = MutableStateFlow<Session?>(null)
    val selectedSession: StateFlow<Session?> = _selectedSession

    fun loadSessions() {
        _state.value = SessionsState.Loading
        viewModelScope.launch {
            try {
                val token = TokenStorage.getToken()
                val bearer = if (token != null) "Bearer $token" else ""
                val resp = SessionRepository(context!!).listSessions(bearer)
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

    fun loadSession(bearer: String, id: Long) {
        viewModelScope.launch {
            try {
                val resp = SessionRepository(context!!).getSession(bearer, id)
                if (resp.isSuccessful) {
                    _selectedSession.value = resp.body()
                }
            } catch (_: Exception) {}
        }
    }

    fun loadAttendance(id: Long) {
        viewModelScope.launch {
            try {
                val token = TokenStorage.getToken()
                val bearer = if (token != null) "Bearer $token" else ""
                val resp = SessionRepository(context!!).getAttendance(bearer, id)
                if (resp.isSuccessful) {
                    _attendance.value = resp.body() ?: emptyList()
                }
            } catch (_: Exception) {}
        }
    }

    fun checkin(sessionId: Long, lat: Double? = null, lng: Double? = null, proofUrl: String? = null) {
        viewModelScope.launch {
            try {
                val token = TokenStorage.getToken()
                val bearer = if (token != null) "Bearer $token" else ""
                val req = CheckinRequest(lat, lng, null, proofUrl)
                val resp = SessionRepository(context!!).checkin(bearer, sessionId, req)
                if (resp.isSuccessful) {
                    val body = resp.body()
                    _lastCheckinStatus.value = body ?: "OK"
                } else {
                    _lastCheckinStatus.value = "failed: ${resp.code()}"
                }
            } catch (e: Exception) {
                _lastCheckinStatus.value = e.message ?: "error"
            }
        }
    }
}
