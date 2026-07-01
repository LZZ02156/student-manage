package com.rollcall.android.repository

import com.rollcall.android.model.CheckinRequest
import com.rollcall.android.model.LoginResponse
import com.rollcall.android.model.Session
import com.rollcall.android.network.NetworkModule
import retrofit2.Response

class AuthRepository {
    private val api = NetworkModule.apiService

    suspend fun login(username: String, password: String): Response<LoginResponse> {
        return api.login(username, password)
    }
}
