package com.rollcall.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rollcall.android.repository.AuthRepository
import com.rollcall.android.storage.TokenStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle: AuthState()
    object Loading: AuthState()
    data class Success(val token: String): AuthState()
    data class Error(val message: String): AuthState()
}

class AuthViewModel: ViewModel() {
    private val repo = AuthRepository()

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state

    fun login(username: String, password: String) {
        _state.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val resp = repo.login(username, password)
                if (resp.isSuccessful) {
                    val body = resp.body()
                    val token = body?.token
                    if (token != null) {
                        TokenStorage.setToken(token)
                        _state.value = AuthState.Success(token)
                    } else {
                        _state.value = AuthState.Error("Empty token")
                    }
                } else {
                    _state.value = AuthState.Error("Login failed: ${resp.code()}")
                }
            } catch (e: Exception) {
                _state.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun logout() {
        TokenStorage.clear()
        _state.value = AuthState.Idle
    }
}
