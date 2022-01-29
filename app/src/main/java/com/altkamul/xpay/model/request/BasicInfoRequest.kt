package com.altkamul.xpay.model.request

import kotlinx.serialization.Serializable

@Serializable
data class BasicInfoRequest(val terminalID: String, val posid: String)