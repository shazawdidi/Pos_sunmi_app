package com.altkamul.xpay.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altkamul.xpay.model.Branch
import com.altkamul.xpay.model.CartItem
import com.altkamul.xpay.model.request.CheckoutPaymentMode
import com.altkamul.xpay.model.request.CheckoutRequest
import com.altkamul.xpay.model.response.local.TransactionWithItems
import com.altkamul.xpay.repositroy.ChangeBranchRepository
import com.altkamul.xpay.sealed.ServerResponse
import com.altkamul.xpay.utils.Common
import com.altkamul.xpay.utils.LoggedMerchantPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ChangeBranchViewModel
@Inject constructor(
    private val changeBranchRepository: ChangeBranchRepository,
    private val context: Context,
) :
    ViewModel() {

    private val _branches = MutableLiveData<List<Branch>>()
    val branches: LiveData<List<Branch>> = _branches
    var isLoading = mutableStateOf(false)

    init {
        getListOfBranchesLocally()
    }

    /** This FUnction Will Return List Of Branches Locally*/
    private fun getListOfBranchesLocally() {
        viewModelScope.launch {
            val branches = changeBranchRepository.getBranches()
            _branches.value = branches
        }
    }

    /** This Function For Reset The Database And Sync All Un Synced Transaction*/
    fun completeSyncingAndResetDatabase(
        selectedBranch: String,
        onCompleteChangeBranch: () -> Unit
    ) {
        isLoading.value = true
        viewModelScope.launch {
            val branchID = _branches.value?.first { branch ->
                branch.name == selectedBranch
            }?.id

            /** Get Selected Branch From Local*/
            val branch = branchID?.let { changeBranchRepository.getBranchWithDataByID(it) }
            /** Assigning Selected Branch To There LoggedMerchantPref Branch*/
            branch?.branch?.images = branch?.image
            branch?.branch?.terminals = branch?.terminal
            branch?.branch?.users = branch?.userBranch
            branch?.branch?.language = branch?.language
            branch?.branch?.address = branch?.address
            LoggedMerchantPref.branch = branch?.branch

            /** Sync Every Offline Transaction With Server*/
            val transaction = changeBranchRepository.getLocalOfflineTransactions(isOffline = true)
            if (transaction.isNotEmpty()) {
                val uploadComplete = syncLocalTransactionWithServer(transaction)
                if (uploadComplete) {
                    /** Delete OR Reset The Database*/
                    resetTheDatabase()
                    onCompleteChangeBranch()
                } else
                    Common.createToast(context, "Something Wrong Please Call The Support!!")
            } else {
                /** Delete OR Reset The Database*/
                resetTheDatabase()
                onCompleteChangeBranch()
            }


            isLoading.value = false
        }
    }

    /** This FUnction Will Delete Category and SubCategory
     * And Items And Misconfiguration and invoice layout
     * And Transaction History and Transaction Tables*/
    private suspend fun resetTheDatabase() {
        changeBranchRepository.resetTheDatabase()
    }

    /** This Function WIll Sync All Offline Transaction With Server*/
    private suspend fun syncLocalTransactionWithServer(transaction: List<TransactionWithItems>): Boolean {
        /** First We Need User To Login For Getting Back a Token*/
        val autoLoginSuccess = autoLoginForGettingBackToken()
        Timber.d("autoLoginSuccess $autoLoginSuccess")
        if (!autoLoginSuccess)
            return false
        /** Second Upload Transaction*/
        transaction.forEach { transactionWithItems ->
            val cartItem = mutableListOf<CartItem>()
            val paymentMethod = mutableListOf<CheckoutPaymentMode>()
            transactionWithItems.items.forEach { transactionItem ->
                cartItem.add(
                    CartItem(
                        itemId = transactionItem.itemId,
                        qty = transactionItem.qty ?: 0,
                        totalPrice = transactionItem.totalPrice,
                        discountValue = transactionItem.discount ?: 0.0,
                    )
                )
            }
            transactionWithItems.payments.forEach { transactionPayment ->
                paymentMethod.add(
                    CheckoutPaymentMode(
                        amount = transactionPayment.amount,
                        paymentModeId = transactionPayment.paymentModeId
                    )
                )
            }
            val checkoutRequest = CheckoutRequest(
                checkoutItems = cartItem,
                checkoutPaymentModes = paymentMethod,
                couponCode = "",
                mobileNumber = "97123456789",
                param1 = "",
                param2 = "",
                param3 = ""
            )
            when (val response =
                changeBranchRepository.uploadOfflineTransaction(checkoutRequest = checkoutRequest)) {
                is ServerResponse.Success -> {
                    /** No Thing To Do Just Waiting Until Finishing This Iterator*/

                }
                is ServerResponse.Error -> {
                    Common.createToast(context, "${response.message} Please Try Again !!")
                    return false
                }
            }
        }
        return true
    }

    private suspend fun autoLoginForGettingBackToken(): Boolean {
        val listOfCashiers = changeBranchRepository.getAllCashiers()
        val user = listOfCashiers.first()
        return when (val response = changeBranchRepository.autoLogin(
            pos = LoggedMerchantPref.pos ?: "",
            terminal = LoggedMerchantPref.terminalId ?: "", password = user.userPassword ?: "",
            isSupportLogin = false,
            isLoginWithEncryptedPassword = true
        )) {
            is ServerResponse.Success -> {
                val userWithToken = response.data
                LoggedMerchantPref.user = userWithToken
                true
            }
            is ServerResponse.Error -> {
                false
            }
        }
    }
}