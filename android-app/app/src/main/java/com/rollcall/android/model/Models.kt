package com.rollcall.android.model

import java.time.LocalDateTime

data class Session(
    val id: Long,
    val title: String,
    val creator: String,
    val startTime: String?,
    val endTime: String?
)

data class CheckinRequest(
    val lat: Double? = null,
    val lng: Double? = null,
    val qrCode: String? = null,
    val proofUrl: String? = null
)

data class LoginResponse(
    val token: String
)
