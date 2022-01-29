package com.altkamul.xpay.model.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "transactionPayments")
data class TransactionPayment(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    var transactionMasterId: Int? = 1,
    val transactionPaymentId: Int,
    val paymentModeId: Int,
    val amount: Double= 100.0,
    val isDeleted: Boolean = false,
)