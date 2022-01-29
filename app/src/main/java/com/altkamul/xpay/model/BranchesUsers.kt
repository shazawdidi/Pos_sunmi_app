package com.altkamul.xpay.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "user_table")
@Serializable
data class BranchesUsers(
    var branchId: String? = null,
    @PrimaryKey val userId: String,
    val userName: String?,
    val userType: String?,
    val userPassword: String?,
) {
    @Ignore
    val permissions: List<Permission>? = null
}