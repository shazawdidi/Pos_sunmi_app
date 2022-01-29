package com.altkamul.xpay.model.request

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordRequest(val oldPassword: String, val newPassword: String)