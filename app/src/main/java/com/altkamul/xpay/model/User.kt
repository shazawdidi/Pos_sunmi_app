package com.altkamul.xpay.model

import androidx.room.Ignore
import kotlinx.serialization.Serializable


@Serializable
data class User(
    val userId: String? = "",
    val name: String?,
    internal val userType: String? = null,
    val token: String? = null,
) {
    @Ignore
    var permissions: List<Permission>? = null
}