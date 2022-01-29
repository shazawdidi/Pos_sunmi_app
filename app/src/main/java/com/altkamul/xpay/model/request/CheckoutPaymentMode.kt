package com.altkamul.xpay.model.request

import kotlinx.serialization.Serializable

@Serializable
data class CheckoutPaymentMode(
    val amount: Double,
    val paymentModeId: Int
)