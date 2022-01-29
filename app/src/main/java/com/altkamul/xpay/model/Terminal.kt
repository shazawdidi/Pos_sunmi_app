package com.altkamul.xpay.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "MerchantTerminal_table")
@Serializable
data class Terminal(
    var branchId: String? = null,
    @PrimaryKey val posId: String,
    val sim: String?,
    val terminalId: String?
)