package com.altkamul.xpay.viewmodel


import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altkamul.xpay.R
import com.altkamul.xpay.model.Transaction
import com.altkamul.xpay.model.TransactionItem
import com.altkamul.xpay.model.User
import com.altkamul.xpay.repositroy.TransactionOperationsRepository
import com.altkamul.xpay.sealed.ReportType
import com.altkamul.xpay.sealed.ServerResponse
import com.altkamul.xpay.utils.getFormattedDate
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * A View model with hiltViewModel annotation that is used to access this view model everywhere needed
 */
@HiltViewModel
@SuppressLint("StaticFieldLeak")
class ReportsViewModel @Inject constructor(
    private val reportsRepository: TransactionOperationsRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    val userIndex = mutableStateOf(0)
    val currentTab = mutableStateOf<ReportType>(ReportType.TransactionsReport)
    private val allTransactions = MutableLiveData<MutableList<Transaction>>(mutableListOf())
    val transactions: MutableList<Transaction> = mutableStateListOf()
    val products: MutableList<TransactionItem> = mutableStateListOf()
    val cashierTransactions: MutableList<Transaction> = mutableStateListOf()

    val cashiers: MutableList<User> = mutableStateListOf()

    /** Date Filter */
    val date = mutableStateOf(0L)

    /** Is it loading now ? */
    val isLoading = mutableStateOf(false)

    /** Error message - in case it happened */
    val errorMessage = mutableStateOf("")

    init {
        /** Then load our transactions */
        loadMoreTransactions()
    }

    private fun loadMoreTransactions() {
        isLoading.value = true
        viewModelScope.launch {
            reportsRepository.getTransactionsFromLocal().let {
                isLoading.value = false
                if (it.isNotEmpty()) {
                    /** All transactions that we have */
                    allTransactions.value?.addAll(it)
                    /** Then extract the all users who made this transactions */
                    cashiers.addAll(allTransactions.value?.map { transaction ->
                        User(userId = transaction.userId, name = transaction.worker)
                    } ?: mutableListOf())
                    hoistReportsState(tranx = it)
                }
            }
        }
    }

    private fun hoistReportsState(tranx: List<Transaction>) {
        /** First clear all lists */
        transactions.clear()
        products.clear()
        cashierTransactions.clear()
        /** All transactions that we can manipulate */
        transactions.addAll(tranx)
        /** Extract all the products from the transaction that had been made */
        transactions.forEach { transaction ->
            products.addAll(transaction.transactionDetail)
        }
        /** state of all cashiers transactions */
        cashierTransactions.addAll(tranx)
    }


    fun updateCurrentTab(newTab: ReportType) {
        currentTab.value = newTab
    }

    fun filterTransactions(date: Long? = null, index: Int? = null) {
        /** If it's not null and does not equal the previous date, then filter it */
        if (date != null) {
            val dateAsString = if (date == 0L) "" else Date(date).getFormattedDate("yyyy-MM-dd")
            Timber.d("date is $dateAsString")
            if (date == 0L) {
                Timber.d("Clearing previous date filters")
                /** Clear previous filters if exist */
                hoistReportsState(tranx = allTransactions.value ?: mutableListOf())
            } else {
                Timber.d("Applying filter by date !")
                allTransactions.value?.filter {
                    it.transactionDateTime.split("T").first() == dateAsString
                }?.let {
                    if (it.isEmpty()) {
                        Timber.d("No transaction on this day !")
                        getTransactionsHistoryFromApi(
                            dateAsString,
                            onSuccess = {
                                /** Check if transactions still empty, if so then there are no transaction on this day */
                                if (transactions.isEmpty()) {
                                    errorMessage.value =
                                        context.getString(R.string.no_transaction_on_this_day)
                                }
                            },
                            onFailed = { reason ->
                                hoistReportsState(tranx = listOf())
                                /** Handle failure possible cases */
                                errorMessage.value = when {
                                    reason.contains("timeout",
                                        true) -> "Server timeout, try again !"
                                    else -> reason
                                }
                            }
                        )
                    } else {
                        Timber.d("There are a transactions on this day !")
                        hoistReportsState(tranx = it)
                    }
                }
            }
        }

        if (index != null && currentTab.value == ReportType.CashiersReport) {
            userIndex.value = index
            /** If userId is empty, this mean the filtering by cashier is cleared, else apply the filter */
            val userId = if (index == 0) "" else cashiers[index - 1].userId
                ?: throw IllegalArgumentException("userId should not be null !")
            if (userId.isEmpty()) {
                cashierTransactions.addAll(transactions)
            } else {
                cashierTransactions.clear()
                cashierTransactions.addAll(transactions.filter { transaction -> transaction.userId == userId })
            }
        }
    }

    private fun getTransactionsHistoryFromApi(
        dateAsString: String,
        onSuccess: () -> Unit,
        onFailed: (reason: String) -> Unit,
    ) {
        isLoading.value = true
        viewModelScope.launch {
            reportsRepository.getTransactionHistory(date = dateAsString).let { response ->
                when (response) {
                    is ServerResponse.Success -> {
                        /** Save the new data coming from the server */
                        response.data?.forEach {
                            reportsRepository.saveTransactionLocally(it)
                        }
                        onSuccess()
                    }
                    else -> {
                        onFailed(response.message ?: "Unknown error !")
                    }
                }
            }
            isLoading.value = false
        }
    }

}