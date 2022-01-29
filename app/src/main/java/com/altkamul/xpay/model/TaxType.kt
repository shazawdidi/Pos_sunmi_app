package com.altkamul.xpay.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "tax_types")
@Serializable
data class TaxType(
    val branchParams: String?,
    @PrimaryKey val id: Int? = 0,
    val name: String?
)