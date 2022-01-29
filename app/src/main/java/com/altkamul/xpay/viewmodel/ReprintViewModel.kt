package com.altkamul.xpay.viewmodel


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altkamul.xpay.model.Transaction
import com.altkamul.xpay.repositroy.TransactionOperationsRepository
import com.altkamul.xpay.sealed.ServerResponse
import com.altkamul.xpay.utils.LoggedMerchantPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A View model with hiltViewModel annotation that is used to access this view model everywhere needed
 */
@HiltViewModel
class ReprintViewModel @Inject constructor(
    private val reprintRepository: TransactionOperationsRepository
) : ViewModel() {
    private val _transaction = MutableLiveData<Transaction>()
    val transaction: LiveData<Transaction> = _transaction
    val transactionId = mutableStateOf(0)
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf("")

    /** Get the transaction with a specified ID */
    fun getTransactionWithId(transactionId: Int) {
        isLoading.value = true
        /** First we check if we had that transaction in our local */
        viewModelScope.launch {
            reprintRepository.getTransactionByIdFromLocal(transactionId = transactionId).let { transaction->
                if(transaction == null){
                    /** We should get it from the API and then store it locally in case we needed it later */
                    getTransactionByIdFromApi(transactionId = transactionId)
                } else {
                    isLoading.value = false
                    _transaction.value = transaction
                }
            }
        }
    }

    /** Get the transaction with a specified ID from API */
    private suspend fun getTransactionByIdFromApi(transactionId: Int) {
        reprintRepository.getTransactionByIdFromApi(transactionId = transactionId).let{ response->
            /** stop loading */
            isLoading.value = false
            /** Check the response that we had got ! */
            when(response){
                is ServerResponse.Success -> {
                    /** Expose it to our UI */
                    _transaction.value = response.data
                    /** Then save it locally */
                    val apiTrx = response.data ?: throw IllegalArgumentException("Fetched transaction should not be null")
                    reprintRepository.saveTransactionLocally(transaction = apiTrx)
                }
                is ServerResponse.Error -> {
                    errorMessage.value = response.message ?: "Unknown error"
                }
            }
        }
    }

    /** Get the last transaction from Local */
    fun getLastTransaction() {
        isLoading.value = true
        /** First we check if we had a last transaction in our local*/
        viewModelScope.launch {
            reprintRepository.getLastTransactionFromLocal().let { transaction->
                transaction?.let {
                    isLoading.value = false
                    /** Expose it to our UI */
                    _transaction.value = it
                    transactionId.value = it.transactionMasterId ?: throw IllegalArgumentException("Transaction should have a master id !")
                } ?: getLastTransactionFromApi()
            }
        }
    }

    /** Get the last transaction from API */
    private suspend fun getLastTransactionFromApi() {
        reprintRepository.getLastTransactionFromApi(
            branchId = LoggedMerchantPref.branch?.id ?: "",
            terminalId = LoggedMerchantPref.terminalId ?: ""
        ).let{ response->
            /** stop loading */
            isLoading.value = false
            /** Check the response that we had got ! */
            when(response){
                is ServerResponse.Success -> {
                    isLoading.value = false
                    /** Then save it locally */
                    val apiTrx = response.data ?: throw IllegalArgumentException("Fetched transaction should not be null")
                    reprintRepository.saveTransactionLocally(transaction = apiTrx)
                    /** Expose it to our UI */
                    transactionId.value = apiTrx.transactionMasterId ?: throw IllegalArgumentException("Transaction should have a master id !")
                    _transaction.value = apiTrx
                }
                is ServerResponse.Error -> {
                    errorMessage.value = response.message ?: "Unknown error"
                }
            }
        }
    }

    fun clearPreviousSearch() {
        _transaction.value?.let {
            _transaction.value = null
        }
    }


}