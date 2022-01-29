package com.altkamul.xpay.model.response.print

import com.altkamul.xpay.model.TransactionItem
import kotlinx.serialization.Serializable

@Serializable
data class LayoutText(
    val list: List<Data>,
    val transaction: List<TransactionItem>?
)