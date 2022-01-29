package com.altkamul.xpay.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.altkamul.xpay.sealed.DiscountType
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Entity(tableName = "MainConfig_table")
data class LocalMainConfiguration(
    @PrimaryKey val branchID: String,
    val businessDayAllowed: String,
    val businessShiftAllowed: Boolean,
    val claimAllowed: String,
    val customerAllowed: Boolean,
    val decimalPoint: Int,
    val discountAllowed: Int,
    val footerMessage: String,
    val isSucceeded: Boolean,
    val merchantCopy: String,
    val nfcProductSearch: Boolean,
    val payment: String,
    val printAllowed: Boolean,
    val queueAllowed: String,
    val rePrint: Boolean,
    val smsAllowed: Boolean,
    val statusCode: Int,
    val taxAllowed: Boolean,
    val trn: Double,
    val taxName: String?,
    val taxID: Int?,
    val taxValue: Double?,
    val taxTypeID: Int?,
    val taxTypeName: String?
){
    @Ignore @Transient val discountType = discountAllowed.let {
        when (it) {
            0 -> DiscountType.None
            1 -> DiscountType.ByValue()
            2 -> DiscountType.ByPercent()
            3 -> DiscountType.Both
            else -> DiscountType.None
        }
    }
}