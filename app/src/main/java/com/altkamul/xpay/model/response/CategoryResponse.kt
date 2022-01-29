package com.altkamul.xpay.model.response

import com.altkamul.xpay.model.Category
import kotlinx.serialization.Serializable

@Serializable
data  class CategoryResponse(
    val categories:List<Category>

)