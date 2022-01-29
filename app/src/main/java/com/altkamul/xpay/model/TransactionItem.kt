package com.altkamul.xpay.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Entity(tableName = "transactionItems")
data class TransactionItem(
    /** Come from the server */
    @PrimaryKey @SerialName("transactionDetailId") val transactionDetailId: Int? = 1,
    @SerialName("itemId") val itemId: Int,
    @SerialName("transactionMasterId") val transactionMasterId: Int,
    @SerialName("customerPaidAmount") val customerPaidAmount: Double,
    @SerialName("isDeleted") val isDeleted: Boolean,

    /** Filled manually */
    @SerialName("qty") var qty: Int? = 1,
    @SerialName("discount") var discount: Double? = 0.0,
    @SerialName("taxRate") var taxRate: Double? = 0.0,
    @SerialName("taxAmount") var taxAmount: Double? = 0.0,
    @SerialName("faceValue") var faceValue: Double? = 0.0,

    /** For printing purpose */
    @Transient var totalPrice: Double = 0.0,
    @Transient var charges: Int = 0,
    @Transient var service: String = "",/** Subcategory name */
    @Transient var itemName: String = "",
)
