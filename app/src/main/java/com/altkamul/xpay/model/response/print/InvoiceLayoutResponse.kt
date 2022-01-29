package com.altkamul.xpay.model.response.print

import kotlinx.serialization.Serializable

@Serializable
data class InvoiceLayoutResponse(
    val copyType: Int?,
    val isDeleted: Boolean?,
    val isSucceeded: Boolean?,
    val layoutName: String?,
    val layoutText: LayoutText?,
    val layoutType: Int?,
    val message: String?,
    val printingLayoutId: String?,
    val statusCode: Int?
)