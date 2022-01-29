package com.altkamul.xpay.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Entity(tableName = "Items_table")
@Serializable
@kotlinx.parcelize.Parcelize
data class Item(
    @SerializedName("itemId") @PrimaryKey var itemId: Int?,
    @SerializedName("itemNameEN") var itemNameEN: String?,
    @SerializedName("itemNameAR") var itemNameAR: String?,
    @SerializedName("itemNameFR") var itemNameFR: String?,
    @SerializedName("itemNameTR") var itemNameTR: String?,
    @SerializedName("itemVersion") var itemVersion: Int?,
    @SerializedName("subCategoryNameEN") var subCategoryNameEN: String?,
    @SerializedName("subCategoryNameAR") var subCategoryNameAR: String?,
    @SerializedName("subCategoryNameFR") var subCategoryNameFR: String?,
    @SerializedName("subCategoryNameTR") var subCategoryNameTR: String?,
    @SerializedName("subCategoryId") var subCategoryId: Int?,
    @SerializedName("subCategoryVersion") var subCategoryVersion: Int?,
    @SerializedName("itemTypeID") var itemTypeID: Int?,
    @SerializedName("branchId") var branchId: String?,
    @SerializedName("itemPlace") var itemPlace: Int?,
    @SerializedName("imageUrl") var imageUrl: String?,
    @SerializedName("discount") var discount: Double?,
    @SerializedName("facePrice") var facePrice: Int?,
    @SerializedName("loyaltyPoints") var loyaltyPoints: Int?,
    @SerializedName("isOpenPrice") var isOpenPrice: Boolean?,
    @SerializedName("isOpenQuantity") var isOpenQuantity: Boolean?,
    @SerializedName("maximumPrice") var maximumPrice: Int?,
    @SerializedName("minimumPrice") var minimumPrice: Int?,
) : Parcelable