package com.altkamul.xpay.model.request

import kotlinx.serialization.Serializable

@Serializable
data class EditUserRequest(
    val cashierName: String?,
    val cashierPassword: String?,
    val cashierType: Int?
)