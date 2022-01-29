package com.altkamul.xpay.model.request

import kotlinx.serialization.Serializable

@Serializable
data class AddUserRequest(
    val branchId: String?,
    val cashierName: String?,
    val cashierPassword: String?,
    val posRoleId: Int?
)