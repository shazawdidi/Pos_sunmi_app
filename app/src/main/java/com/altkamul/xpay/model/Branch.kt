package com.altkamul.xpay.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "MerchantBranch_table")
@Serializable
data class Branch(
    @PrimaryKey val id: String,
    val businessId: String?,
    val businessType: String?,
    val businessTypeId: Int?,
    val email: String?,
    val firstPhoneNumber: String?,
    val isDeleted: Boolean?,
    val isDisabled: Boolean?,
    val languageId: Int?,
    val name: String?,
    val notificationBase: String?,
    val notificationDetail: String?,
    val secondPhoneNumber: String?,
    val smsCredit: String?,
    val subscriptionType: String?,
    val subscriptionTypeId: Int?,

    ) {
    @Ignore
    var address: Address? = null

    @Ignore
    var language: Language? = null

    @Ignore
    var terminals: List<Terminal>? = null

    @Ignore
    @SerialName("users")
    var users: List<BranchesUsers>? = null

    @Ignore
    var images: Images? = null
}