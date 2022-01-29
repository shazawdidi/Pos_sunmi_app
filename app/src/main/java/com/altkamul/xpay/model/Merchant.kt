package com.altkamul.xpay.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "Merchant_table")
@Serializable
data class Merchant(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val merchantId: String,
    val merchantName: String?,
) {

    @Ignore
    val branches: List<Branch>? = null
}
