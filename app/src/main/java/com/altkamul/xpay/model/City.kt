package com.altkamul.xpay.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "MerchantCity_table")
@Serializable
data class City(
    var branchId: String? = null,
    val country: String?,
    val countryId: Int?,
    @PrimaryKey val id: Int,
    val name: String?
)