package com.altkamul.xpay.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CheckoutResponse(
    @SerialName("transactionMasters") val checkoutTransaction: CheckoutTransaction,
    val message: String,
)