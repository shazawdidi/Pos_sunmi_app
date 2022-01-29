package com.altkamul.xpay.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "MerchantImages_table")
@Serializable
data class Images(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    var branchId: String? = null,
    val defaultlogo: String?,
    val footerlogo: String?,
    val logo: String?,
    val printinglogo: String?
)