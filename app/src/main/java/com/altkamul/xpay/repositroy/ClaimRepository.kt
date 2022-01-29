package com.altkamul.xpay.repositroy

import com.altkamul.xpay.api.ApiClientImp
import com.altkamul.xpay.db.DataAccessObject
import com.altkamul.xpay.model.Transaction
import javax.inject.Inject

class ClaimRepository @Inject constructor(
    private val dataAccessObject: DataAccessObject,
    private val apiClientImp: ApiClientImp
) {
    /** This Function Will Return Last Transaction Locally*/
    suspend fun getLastTransactionLocally() = dataAccessObject.getLastTransaction()

    /** This Function Will Return Transaction by ID Locally*/
    suspend fun getTransactionByIDLocally(transactionID: Int) =
        dataAccessObject.getTransactionWithId(transactionID)

    /** This FUnction Will Claim Transaction By ID*/
    suspend fun claimTransactionByID(transactionID: Int) =
        apiClientImp.claimTransactionByID(transactionID)

    /** This Function Will Update Transaction By ID After Claimed*/
    suspend fun updateTransaction(transaction: Transaction) =
        dataAccessObject.insertTransaction(transaction)
}