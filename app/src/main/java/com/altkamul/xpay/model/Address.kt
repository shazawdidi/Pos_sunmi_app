package com.altkamul.xpay.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "MerchantAddress_table")
@Serializable
data class Address(
    var branchId: String? = null,
    val branchAddress: String?,
    @PrimaryKey val id: Int,
    val location: String?
) {
    @Ignore
    var city: City? = null
}