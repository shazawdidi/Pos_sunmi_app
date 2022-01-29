package com.altkamul.xpay.model.response

import com.altkamul.xpay.model.TransactionItem
import kotlinx.serialization.Serializable

@Serializable
data class CheckoutTransaction(
    val branchId: String? = null,
    val coupon: String? = null,
    val isDeleted: Boolean,
    val mobileNumber: String,
    val param1: String,
    val param2: String,
    val param3: String,
    val printingConfirmation: Boolean,
    val terminalId: String? = null,
    val totalAmount: Double,
    val totalDiscount: Double,
    val totalTaxAmount: Double,
    val transactionDateTime: String,
    val transactionMasterId: Int,
    val userId: String,
    val userName: String,
    val voucherNO: Int,
    val transactionDetails: List<TransactionItem>,
//    val transactionPayment: TransactionPayment? = null,
    val transactionPayment: List<TransactionPayment>? = listOf(),
)
