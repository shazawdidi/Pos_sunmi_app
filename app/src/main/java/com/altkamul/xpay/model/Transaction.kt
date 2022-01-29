package com.altkamul.xpay.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.altkamul.xpay.model.response.TransactionPayment
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey val id: Int? = null,
    val transactionMasterId: Int? = id,
    val terminalId: String?,
    val merchantId: String?,
    val branchId: String?,
    val branchName: String?,
    val transactionDateTime: String,
    val payments: String,
    val worker: String,
    val userId: String,
    val voucherNO: Int? = null,
    val carNo: String? = null,
    val customerNo: String? = null,
    val totalQty: Double,
    val totalAmount: Double,
    val totalDiscount: Double,
    val totalTaxAmount: Double,
    var total: Double,
    val address: String? = null,
    val city: String? = null,
    val country: String? = null,
    val phone: String? = null,
    val qr: String? = null,
    val barcode: String? = null,
    val param1: String? = null,
    val param2: String? = null,
    val param3: String? = null,
    val couponCode: String? = null,
    val mobileNumber: String?,
    val printingConfirmation: Boolean = false,
    val coupon: String? = null,
    val isDeleted: Boolean= false,
    val isClaimed: Boolean? = false,
    @Transient var isClaimedOffline: Boolean? = false,
    @Transient var isOffline: Boolean? = false,
    @Transient var isClaim: Boolean? = false,
){
    @Ignore
    var transactionDetail = listOf<TransactionItem>()
    @Ignore
    var transactionPayment: List<TransactionPayment> = listOf()
}
