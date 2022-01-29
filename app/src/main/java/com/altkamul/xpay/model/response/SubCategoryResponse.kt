package com.altkamul.xpay.model.response

import com.altkamul.xpay.model.SubCategory
import kotlinx.serialization.Serializable

@Serializable
data class SubCategoryResponse(
    val subCategories: List<SubCategory>
)