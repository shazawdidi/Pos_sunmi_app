package com.altkamul.xpay.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altkamul.xpay.model.Cashiers
import com.altkamul.xpay.model.PosRole
import com.altkamul.xpay.model.request.AddUserRequest
import com.altkamul.xpay.model.request.EditUserRequest
import com.altkamul.xpay.repositroy.AccountRepository
import com.altkamul.xpay.sealed.ServerResponse
import com.altkamul.xpay.utils.Common
import com.altkamul.xpay.utils.LoggedMerchantPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val context: Context,
) : ViewModel() {

    private var _cashiers = MutableLiveData<List<Cashiers>>()
    val cashiers: LiveData<List<Cashiers>> = _cashiers

    private var _roles = MutableLiveData<List<PosRole>>()
    val roles: LiveData<List<PosRole>> = _roles

    init {
        getAllCashierAndRolesFromLocal()
    }


    /** This Function Will Assign  Account Data To _cashiers*/
    private fun getAllCashierAndRolesFromLocal() {
        viewModelScope.launch {
            val listOfCashiers = accountRepository.getAllCashierLocally()
            _cashiers.value = listOfCashiers
            val listOfRoles = accountRepository.getAllRolesLocally()
            _roles.postValue(listOfRoles)
        }
    }


    private fun checkUserInputs(
        name: String,
        type: String,
        password: String,
        confirmPassword: String
    ) =
        name.isNotBlank() && type.isNotBlank() && password.isNotBlank() && password == confirmPassword

    /** ADD NEW USER AND UPDATE LOCAL LIST  */
    fun addNewUser(
        name: String,
        type: String,
        password: String,
        confirmPassword: String,
        onCompleteAddingUser: () -> Unit
    ) {
        val checkUserInputs = checkUserInputs(name, type, password, confirmPassword)
        if (checkUserInputs)
            viewModelScope.launch {
                val branchID = LoggedMerchantPref.branch?.id
                val typeID = _roles.value?.find {
                    it.posRoleName == type
                }
                val addUserRequest = AddUserRequest(
                    branchId = branchID,
                    cashierName = name,
                    cashierPassword = password,
                    posRoleId = typeID?.posRoleId
                )
                when (val response = accountRepository.addAccountInServer(addUserRequest)) {
                    is ServerResponse.Success -> {
                        val userID = response.data?.cashierId
                        val message = response.data?.message
                        if (message?.contains("cashier added successfully") == true) {
                            val list = _cashiers.value?.toMutableList()
                            val cashiers =
                                getCashierObject(name, type, password, userID = userID ?: "Null")
                            list?.add(cashiers)
                            _cashiers.postValue(list)
                            Common.createToast(context, "Account Added Successfully")
                            updateUserLocally(
                                name = name,
                                type = type,
                                password = password,
                                userID = userID ?: "Null"
                            )
                        } else {
                            Common.createToast(context, "Account Already Exist")
                        }
                        onCompleteAddingUser()
                    }
                    is ServerResponse.Error -> {
                        Common.createToast(context, "Server Error ${response.message}")
                    }
                }
            }
        else
            Common.createToast(context, "Please Fill The Form Correctly")
    }

    /** EDIT ONE USER AND UPDATE IT LOCALLY  */
    fun editUser(
        name: String,
        type: String,
        password: String,
        userID: String
    ) {
        viewModelScope.launch {
            val typeID = _roles.value?.find {
                it.posRoleName == type
            }
            val editUserRequest = EditUserRequest(
                cashierName = name,
                cashierPassword = password,
                cashierType = typeID?.posRoleId
            )
            when (val response = accountRepository.editAccountInServer(
                userID = userID,
                editUserRequest = editUserRequest
            )) {
                is ServerResponse.Success -> {
                    val message = response.data?.message
                    if (message?.contains("success") == true) {
                        val list = _cashiers.value?.toMutableList()
                        val cashiers = list?.find {
                            it.userId == userID
                        }.apply {
                            this?.userName = name
                            this?.userType = type
                            this?.userPassword = password
                        }
                        list?.remove(list.find { it.userId == userID })
                        _cashiers.value = list
                        if (cashiers != null) {
                            list?.add(cashiers)
                        }
                        _cashiers.value = list
                        Common.createToast(context, "Account Editing Successfully")
                        updateUserLocally(name, type, password, userID)
                    } else
                        Common.createToast(context, "$message")
                }
                is ServerResponse.Error -> {
                    Common.createToast(context, "Server Error ${response.message}")
                }
            }

        }
    }

    fun deleteUser(isDelete: Boolean, cashiers: Cashiers) {
        viewModelScope.launch {
            if (isDelete) {
                when (val response = accountRepository.deleteUserInServer(cashiers.userId ?: "")) {
                    is ServerResponse.Success -> {
                        val message = response.data?.message
                        if (message?.contains("success") == true) {
                            deleteUserFromLocal(cashiers.userId ?: "No Data")
                            val list = _cashiers.value?.toMutableList()
                            list?.remove(cashiers)
                            _cashiers.value = list
                            Common.createToast(context, "Account Delete Successfully")
                        } else
                            Common.createToast(context, "$message")
                    }
                    is ServerResponse.Error -> {
                        Common.createToast(context, "Server Error ${response.message}")
                    }
                }

            }
        }
    }

    private suspend fun deleteUserFromLocal(userID: String) {
        accountRepository.deleteUserInLocal(userID)
    }

    /** This Function Will Insert All Roles Locally */
    private suspend fun updateUserLocally(
        name: String,
        type: String,
        password: String,
        userID: String
    ) {
        val cashiers = getCashierObject(name, type, password, userID)
        deleteUserFromLocal(userID)
        accountRepository.insertUserLocally(listOf(cashiers))
    }

    private fun getCashierObject(
        name: String,
        type: String,
        password: String,
        userID: String = ""
    ): Cashiers {
        val branchID = LoggedMerchantPref.branch?.id
        val currentUser = LoggedMerchantPref.user
        val currentTime = Calendar.getInstance().time.toString()
        return Cashiers(
            id = null,
            branchId = branchID,
            userId = userID,
            userName = name,
            userPassword = password,
            userType = type,
            createdBy = currentUser?.name,
            createdOn = currentTime,
            isDeleted = false,
            lastModifiedBy = currentUser?.name,
            lastModifiedOn = currentTime
        )

    }
}
