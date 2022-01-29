package com.altkamul.xpay.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Entity(tableName = "SubCategory_table")
@Serializable
data class SubCategory(
    @SerializedName("subCategoryId") @PrimaryKey var subCategoryId: Int?,
    @SerializedName("subCategoryNameEN") var subCategoryNameEN: String?,
    @SerializedName("subCategoryNameAR") var subCategoryNameAR: String?,
    @SerializedName("subCategoryNameFR") var subCategoryNameFR: String?,
    @SerializedName("subCategoryNameTR") var subCategoryNameTR: String?,
    @SerializedName("subCategoryPlace") var subCategoryPlace: Int?,
    @SerializedName("categoryId") var categoryId: Int?,
    @SerializedName("imageUrl") var imageUrl: String?,
    @SerializedName("version") var version: Int?,
) {
    @Ignore
    var items: List<Item>? = null
}