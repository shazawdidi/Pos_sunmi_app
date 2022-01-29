package com.altkamul.xpay.model.response.print

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "InvoiceData_table")
data class Data(
    @PrimaryKey val id: Int? = 0,
    val align: String?,
    val bold: String?,
    val size: String?,
    val title: String,
    val value: String
)