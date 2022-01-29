package com.altkamul.xpay.model

import androidx.annotation.StringRes

data class PaymentMethod(
    val id: Int,
    @StringRes val name: Int,
    val enabled: Boolean,
)
