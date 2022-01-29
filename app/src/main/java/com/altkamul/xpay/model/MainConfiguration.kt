package com.altkamul.xpay.model

import androidx.room.Ignore
import kotlinx.serialization.Serializable

@Serializable
data class MainConfiguration(
    val branchID: String,
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
    val trn: Double
) {
    @Ignore
    val tax: Tax? = null
    @Ignore
    val taxType: TaxType? = null

}