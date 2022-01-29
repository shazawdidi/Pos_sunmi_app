package com.altkamul.xpay.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "Cashiers_table")
@Serializable
data class Cashiers(
    @PrimaryKey val id: Int? = null,
    val branchId: String?,
    val userId: String?,
    var userName: String?,
    var userPassword: String?,
    var userType: String?,
    val createdBy: String?,
    val createdOn: String?,
    val isDeleted: Boolean?,
    val lastModifiedBy: String?,
    val lastModifiedOn: String?
)