package com.altkamul.xpay.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "Categories_table")
@Serializable
data class Category(
    @PrimaryKey val categoryId: Int?,
    var categoryNameAR: String?,
    val categoryNameEN: String?,
    val categoryNameFR: String?,
    val categoryNameTR: String?,
    val categoryPlace: Int?,
    val imageUrl: String?,
    val version: Int?
) {
    @Ignore
    var subcategories: List<SubCategory>? = null
}