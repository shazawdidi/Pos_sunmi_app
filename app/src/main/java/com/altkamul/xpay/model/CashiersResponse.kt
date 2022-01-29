package com.altkamul.xpay.model

import kotlinx.serialization.Serializable

@Serializable
data class CashiersResponse(
    val cashiers: List<Cashiers>?,
)