package com.altkamul.xpay.model

import kotlinx.serialization.Serializable

@Serializable
data class  DataVersion(
    val dataVersionID: Int,
    val table: String?,
    val version: Int?
)