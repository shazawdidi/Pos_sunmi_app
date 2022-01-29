package com.altkamul.xpay.model

import kotlinx.serialization.Serializable

@Serializable
data class Roles(
    val posRoles: List<PosRole>?
)