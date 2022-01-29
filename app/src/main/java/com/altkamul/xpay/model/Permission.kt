package com.altkamul.xpay.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "user_permission_table")
@Serializable
data class Permission(
    var userId: String? = null,
    val posPermissionId: Int,
    val posPermissionNameEN: String?,
    val posPermissionNameAR: String?,
    val posPermissionNameTR: String?,
    val posPermissionNameFR: String?,
    val posPermissionNameUR: String?,
    @PrimaryKey val parentId: Int?
)
