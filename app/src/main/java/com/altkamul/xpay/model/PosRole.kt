package com.altkamul.xpay.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "Roles_table")
@Serializable
data class PosRole(
    @PrimaryKey val posRoleId: Int?,
    val posRoleName: String?,
)