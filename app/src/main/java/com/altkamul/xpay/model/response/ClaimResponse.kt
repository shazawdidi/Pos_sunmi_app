package com.altkamul.xpay.model.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ClaimResponse(
    @SerializedName("message") var message: String? = null,
    @SerializedName("statusCode") var statusCode: Int? = null,
    @SerializedName("isSucceeded") var isSucceeded: Boolean? = null
)