package com.altkamul.xpay.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "versions")
data class LocalDataVersions(
    @PrimaryKey(autoGenerate = true) val id: Int? = 0,
    val items: Int,
    val categories: Int,
    val subCategories: Int,
)
