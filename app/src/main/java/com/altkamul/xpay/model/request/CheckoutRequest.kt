package com.altkamul.xpay.model.request


import com.altkamul.xpay.model.CartItem
import kotlinx.serialization.Serializable

@Serializable
data class CheckoutRequest(
    val checkoutItems: List<CartItem>,
    val checkoutPaymentModes: List<CheckoutPaymentMode>,
    val couponCode: String,
    val mobileNumber: String,
    val param1: String,
    val param2: String,
    val param3: String
)