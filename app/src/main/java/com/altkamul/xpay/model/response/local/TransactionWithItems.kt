package com.altkamul.xpay.model.response.local

import androidx.room.Embedded
import androidx.room.Relation
import com.altkamul.xpay.model.Transaction
import com.altkamul.xpay.model.TransactionItem
import com.altkamul.xpay.model.response.TransactionPayment

data class TransactionWithItems(
    @Embedded val transaction: Transaction,
    @Relation(
        parentColumn = "transactionMasterId",
        entityColumn = "transactionMasterId"
    )
    val items: List<TransactionItem>,
    @Relation(
        parentColumn = "transactionMasterId",
        entityColumn = "transactionMasterId"
    )
    val payments: List<TransactionPayment>

)
