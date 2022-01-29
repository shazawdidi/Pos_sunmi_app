package com.altkamul.xpay.repositroy

import com.altkamul.xpay.api.ApiClientImp
import com.altkamul.xpay.db.DataAccessObject
import com.altkamul.xpay.model.Transaction
import com.altkamul.xpay.model.TransactionItem
import com.altkamul.xpay.model.request.CheckoutRequest
import com.altkamul.xpay.model.response.CheckoutResponse
import com.altkamul.xpay.model.response.TransactionPayment
import com.altkamul.xpay.sealed.ServerResponse
import com.altkamul.xpay.utils.getFormattedDate
import com.altkamul.xpay.utils.getStructureTransaction
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class TransactionOperationsRepository @Inject constructor(
    private val dao: DataAccessObject,
    private val api: ApiClientImp,
) {

    /** A function to send the checkout request to our api and return a transaction response */
    suspend fun makeTransaction(checkoutRequest: CheckoutRequest): ServerResponse<CheckoutResponse> {
        return api.makeTransaction(checkoutRequest = checkoutRequest)
    }

    suspend fun getTransactionsFromLocal(): List<Transaction> {
        return dao.getLocalTransactions().let { response ->
            response.map {
                it.transaction.apply {
                    this.transactionPayment = it.payments
                    this.transactionDetail = it.items
                }
            }
        }
    }

    suspend fun getTransactionByIdFromLocal(transactionId: Int): Transaction? {
        return dao.getTransactionWithId(transactionId = transactionId)?.getStructureTransaction()
    }

    suspend fun getTransactionByIdFromApi(transactionId: Int): ServerResponse<Transaction> {
        return api.getTransactionWithId(transactionId = transactionId)
    }

    suspend fun saveTransactionLocally(transaction: Transaction) {
        Timber.d("Saving Transaction fetched from server to be accessible .")
        dao.insertTransaction(transaction = transaction)
        dao.insertTransactionItems(transactionItems = transaction.transactionDetail)
        dao.insertTransactionPayments(transactionPayments = transaction.transactionPayment)
        Timber.d("Saving Transaction fetched from server completed .")

    }

    suspend fun getLastTransactionFromLocal(): Transaction? {
        return dao.getLastTransaction()?.getStructureTransaction()
    }

    suspend fun getLastTransactionFromApi(
        branchId: String,
        terminalId: String
    ): ServerResponse<Transaction> {
        return api.getLastTransaction(branchId = branchId, terminalId = terminalId)
    }

    suspend fun getTransactionHistory(date: String): ServerResponse<List<Transaction>> {
        return api.getTransactionsHistory(date)
    }


    /** Claim transaction with id #transactionId */
    suspend fun claimTransactionWithId(transactionId: Int) =
        api.claimTransactionByID(transactionId = transactionId)

    suspend fun insertFakeTransactions() {
        val transactions = mutableListOf<Transaction>()
        val items = mutableListOf<TransactionItem>()
        val payments = mutableListOf<TransactionPayment>()

        for (x in 1..25) {
            val dateWithTime = Calendar.getInstance().time.getFormattedDate("yyyy-MM-dd HH:mm")
            val date = dateWithTime.split(" ").first()
            val time = dateWithTime.split(" ").last()
            transactions.add(
                Transaction(
                    id = x,
                    transactionMasterId = x,
                    terminalId = "34938",
                    merchantId = "fjdf0fdjfdjf",
                    branchId = "fjdfjdk",
                    branchName = "Branch 1",
                    transactionDateTime = "${date}T${time}",
                    payments = "",
                    worker = "Mustafa",
                    userId = "${x / 10 + 1}",
                    voucherNO = 0,
                    carNo = "fd",
                    customerNo = "",
                    totalQty = 20.0,
                    totalAmount = 20.0,
                    totalDiscount = 20.0,
                    totalTaxAmount = 20.0,
                    total = 20.0,
                    address = "UAE - Dubai",
                    city = "Dubai",
                    country = "UAE",
                    phone = "",
                    qr = "",
                    barcode = "",
                    mobileNumber = "",
                    coupon = "",
                )
            )
            for (y in 1..10) {
                val counter = ((x * 10) - 10) + y
                items.add(
                    TransactionItem(
                        itemId = counter,
                        transactionDetailId = counter,
                        transactionMasterId = x,
                        customerPaidAmount = 10.0,
                        isDeleted = false,
                        qty = 10,
                        totalPrice = (x * 3).toDouble(),
                        discount = 5.0,
                        taxAmount = 10.0,
                        taxRate = 5.0,
                        faceValue = 25.0,
                        charges = 2,
                        service = "Service $x",
                        itemName = "Item $counter"
                    )
                )
            }
            for (z in 1..2) {
                payments.add(
                    TransactionPayment(
                        transactionMasterId = x,
                        transactionPaymentId = 1,
                        paymentModeId = z,
                        amount = 20.0
                    )
                )
            }
        }

        transactions.forEach {
            dao.insertTransaction(it)
        }
        dao.insertTransactionItems(items)
        dao.insertTransactionPayments(payments)
    }
}