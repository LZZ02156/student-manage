package com.rollcall.android.repository

import com.rollcall.android.model.CheckinRequest
import com.rollcall.android.model.Session
import com.rollcall.android.network.NetworkModule
import retrofit2.Response

class SessionRepository {
    private val api = NetworkModule.apiService

    suspend fun listSessions(bearer: String): Response<List<Session>> {
        return api.listSessions(bearer)
    }

    suspend fun getSession(bearer: String, id: Long): Response<Session> {
        return api.getSession(bearer, id)
    }

    suspend fun checkin(bearer: String, id: Long, req: CheckinRequest): Response<String> {
        return api.checkin(bearer, id, req)
    }
}
