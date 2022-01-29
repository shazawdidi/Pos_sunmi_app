package com.altkamul.xpay.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altkamul.xpay.model.Transaction
import com.altkamul.xpay.repositroy.ClaimRepository
import com.altkamul.xpay.sealed.ServerResponse
import com.altkamul.xpay.utils.Common
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ClaimViewModel
@Inject constructor(
    private val claimRepository: ClaimRepository,
    private val context: Context
) :
    ViewModel() {
    private var _transaction = MutableLiveData<Transaction>()
    val transaction: LiveData<Transaction> = _transaction
    val foundTransaction = mutableStateOf(false)
    var isLoading = mutableStateOf(false)

    /** This Function Will Getting Last Transaction Locally*/
    fun getLastTransactionLocally() {
        isLoading.value = true
        viewModelScope.launch {
            val transaction = claimRepository.getLastTransactionLocally()
            isLoading.value = false
            if (transaction != null) {
                _transaction.value = transaction.transaction
                foundTransaction.value = true
            } else {
                foundTransaction.value = false
                Common.createToast(context, "There No Transactions Locally")
            }
        }
    }

    /** This Function Will Getting Transaction By ID Locally*/
    fun getTransactionByIDLocally(transactionID: Int) {
        isLoading.value = true
        viewModelScope.launch {
            val transaction = claimRepository.getTransactionByIDLocally(transactionID)
            if (transaction != null) {
                _transaction.value = transaction.transaction
                foundTransaction.value = true
                isLoading.value = false
            } else {
                isLoading.value = false
                Common.createToast(context, "There No Transactions With This ID")
            }
        }
    }

    /** This FUnction Will Claim Transaction By ID*/
    fun claimCurrentTransaction() {
        isLoading.value = true
        viewModelScope.launch {
            if (_transaction.value?.isClaim == false) {
                /** Check If It was Offline Or Not*/
                if (_transaction.value?.isOffline == true)
                    claimingOffline()
                else
                    claimingOnline()

            } else {
                isLoading.value = false
                Common.createToast(context, "This Transaction its Already Claimed")
                foundTransaction.value = false
            }
        }
    }

    private suspend fun claimingOnline() {
        when (val response = claimRepository.claimTransactionByID(
            _transaction.value?.transactionMasterId ?: 0
        )) {
            is ServerResponse.Success -> {
                val message = response.data?.message
                if (message?.contains("Transaction Claimed Successfully") == true) {
                    updateLocalRoom()
                    Common.createToast(context, "Transaction Claimed Successfully")
                } else
                    Common.createToast(context, "$message")

                isLoading.value = false
                foundTransaction.value = false
            }
            is ServerResponse.Error -> {
                Common.createToast(context, "${response.message}")
                isLoading.value = false
            }
        }
    }

    private suspend fun claimingOffline() {
        updateLocalRoom()
        Common.createToast(context, "Successfully Claiming !!")
        isLoading.value = false
        foundTransaction.value = false
    }

    private suspend fun updateLocalRoom() {
        transaction.value?.total = -(transaction.value?.total ?: 0.0)
        transaction.value?.isClaim = true
        _transaction.value?.let { claimRepository.updateTransaction(it) }

    }
}