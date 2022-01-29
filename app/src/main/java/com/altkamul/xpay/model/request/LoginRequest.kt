package com.altkamul.xpay.model.request

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val posid: String,
    val terminalID: String,
    val password: String,
)
