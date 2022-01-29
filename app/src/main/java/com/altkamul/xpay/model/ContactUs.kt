package com.altkamul.xpay.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "Contact_table")
@Serializable
data class ContactUs(
    @PrimaryKey val salesEmail: String,
    val salesPhone: String,
    val supportEmail: String,
    val supportPhone: String
)