package com.altkamul.xpay.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "MerchantLanguage_table")
@Serializable
data class Language(
    var branchId: String? = null,
    @PrimaryKey val id: Int,
    val name: String?
)