package com.altkamul.xpay.viewmodel


import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altkamul.xpay.model.Transaction
import com.altkamul.xpay.repositroy.TransactionOperationsRepository
import com.altkamul.xpay.sealed.ServerResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A View model with hiltViewModel annotation that is used to access this view model everywhere needed
 */
@HiltViewModel
class TransactionHistoryViewModel @Inject constructor(
    private val trxHistoryRepository: TransactionOperationsRepository
) : ViewModel() {

    /** Transactions list */
    private val allTransactions = MutableLiveData<MutableList<Transaction>>(mutableListOf())
    val transactions: MutableList<Transaction> = mutableStateListOf()

    /** The transaction id that is used to search for a specific transaction on the list */
    val transactionId = mutableStateOf(0)
    /** Is it loading now ? */
    val isLoading = mutableStateOf(false)
    /** Error message - in case it happened */
    val errorMessage = mutableStateOf("")
    /** Current selected transaction, by default its 0 which indicate that user not entered a transaction id yet !*/
    val selectedTrxId = mutableStateOf(0)

    init {
        /** Initially we should get the all transactions in page 1 and expose it to UI*/
        loadMoreTransactions()
    }

    private fun loadMoreTransactions() {
        isLoading.value = true
        viewModelScope.launch {
            trxHistoryRepository.getTransactionsFromLocal().let {
                isLoading.value = false
                if(it.isNotEmpty()){
                    allTransactions.value?.addAll(it)
                    transactions.addAll(it)
                }
            }
        }
    }

    /** Get the transaction with a specified ID */
    fun filterTransactions(transactionId: Int) {
        selectedTrxId.value = 0
        isLoading.value = true
        /**
         * If it's 0 , this mean that the user had cleared the search query and thus we should show him all the transactions as before
         * else we should filter our transactions list
         */
        allTransactions.value?.let {allTransactions->
            transactions.let {currentDisplayedTransactions->
                if(transactionId == 0){
                    currentDisplayedTransactions.clear().also {
                        currentDisplayedTransactions.addAll(allTransactions)
                    }
                } else {
                    /** Filtering transactions depending on transactionId passed to method */
                    currentDisplayedTransactions.clear()
                    currentDisplayedTransactions.addAll(allTransactions.filter{ it.transactionMasterId == transactionId })
                    /**
                     * If empty then we gonna assume that transaction with id = $transactionId is not exist in current part fetched from local
                     * We should search for it in local and if its not founded, then it should be fetched from server
                     */
                    if(currentDisplayedTransactions.isEmpty()){
                        viewModelScope.launch {
                            findTransactionById(transactionId = transactionId)
                        }
                    }
                }
            } ?: throw IllegalArgumentException("Trying to filter the current displayed transactions which is null !")
        } ?: throw IllegalArgumentException("There are no transactions at all !")
        isLoading.value = false
    }


    /** Get the transaction with a specified ID */
    private fun findTransactionById(transactionId: Int) {
        /** First we check if we had that transaction in our local */
        viewModelScope.launch {
            trxHistoryRepository.getTransactionByIdFromLocal(transactionId = transactionId).let { transaction->
                if(transaction == null){
                    /** We should get it from the API and then store it locally in case we needed it later */
                    getTransactionByIdFromApi(transactionId = transactionId)
                } else {
                    isLoading.value = false
                    transactions.add(transaction)
                }
            }
        }
    }

    /** Get the transaction with a specified ID from API */
    private suspend fun getTransactionByIdFromApi(transactionId: Int) {
        trxHistoryRepository.getTransactionByIdFromApi(transactionId = transactionId).let{ response->
            /** stop loading */
            isLoading.value = false
            /** Check the response that we had got ! */
            when(response){
                is ServerResponse.Success -> {
                    /** Then save it locally */
                    val apiTrx = response.data ?: throw IllegalArgumentException("Fetched transaction should not be null")
                    trxHistoryRepository.saveTransactionLocally(transaction = apiTrx)
                }
                is ServerResponse.Error -> {
                    errorMessage.value = response.message ?: "Unknown error"
                }
            }
        }
    }

    fun claimTransactionById(transactionId: Int){
        viewModelScope.launch {
            /** Claim transaction immediately locally */
            val index = transactions.indexOfFirst { transaction -> transaction.transactionMasterId == transactionId }
            transactions[index] = transactions.elementAt(index).copy(isClaimed = true)
            /** Then try to claim it on the server */
            trxHistoryRepository.claimTransactionWithId(transactionId = transactionId).let { response->
                when(response){
                    is ServerResponse.Success -> {
                        /** Claimed successfully */
                    }
                    else -> {
                        /** Failed to claim this transaction, we should queue it again using a worker */
                        transactions[index] = transactions.elementAt(index).copy(isClaimedOffline = true)
                    }
                }
            }
            trxHistoryRepository.saveTransactionLocally(transaction = transactions[index])
        }
    }
}