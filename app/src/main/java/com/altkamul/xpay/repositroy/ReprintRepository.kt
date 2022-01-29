package com.altkamul.xpay.repositroy

import com.altkamul.xpay.api.ApiClientImp
import com.altkamul.xpay.db.DataAccessObject
import com.altkamul.xpay.model.Transaction
import com.altkamul.xpay.model.response.local.TransactionWithItems
import com.altkamul.xpay.sealed.ServerResponse
import javax.inject.Inject

class ReprintRepository@Inject constructor(
    private val dao: DataAccessObject,
    private val api: ApiClientImp,
){
    suspend fun getTransactionByIdFromLocal(transactionId: Int) : TransactionWithItems? {
        return dao.getTransactionWithId(transactionId = transactionId)
    }

    suspend fun getLastTransactionFromLocal(): TransactionWithItems? {
        return dao.getLastTransaction()
    }

    suspend fun getTransactionByIdFromApi(transactionId: Int): ServerResponse<Transaction> {
        return api.getTransactionWithId(transactionId = transactionId)
    }

    suspend fun getLastTransactionFromApi(branchId: String, terminalId: String): ServerResponse<Transaction> {
        return api.getLastTransaction(branchId = branchId, terminalId = terminalId)
    }

}