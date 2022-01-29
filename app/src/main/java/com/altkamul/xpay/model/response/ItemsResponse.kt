package com.altkamul.xpay.model.response

import com.altkamul.xpay.model.Item
import kotlinx.serialization.Serializable

@Serializable
data class ItemsResponse(
    val items: List<Item>
)