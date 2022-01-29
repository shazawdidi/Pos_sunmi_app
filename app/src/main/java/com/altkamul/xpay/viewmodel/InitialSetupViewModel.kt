package com.altkamul.xpay.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altkamul.xpay.R
import com.altkamul.xpay.model.*
import com.altkamul.xpay.repositroy.InitialSetupRepository
import com.altkamul.xpay.sealed.ServerResponse
import com.altkamul.xpay.utils.*
import com.example.scanqrcode.ScanQRCodeInitialize
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class InitialSetupViewModel
@Inject constructor(
    private val initialSetupRepository: InitialSetupRepository,
    private val context: Context,
) : ViewModel() {

    var isLoading = mutableStateOf(false)

    /** Get Merchant Information From The Server*/
    fun getMerchantInfo(
        terminalID: String = "074673",
        posId: String = "353007061714083",
        onDataFetched: () -> Unit,
    ) {
        val isTerminalIDValid = checkTerminalIDLength(terminalID = terminalID)
        if (isTerminalIDValid)
            viewModelScope.launch {
                isLoading.value = true
                val response = initialSetupRepository.getRemoteMerchantInfo("074673", posId)
                isLoading.value = false
                when (response) {
                    is ServerResponse.Success -> {
                        response.data?.let {
                            /** Insert Merchant Data Locally*/
                            insertFieldsIntoDataBase(it, "074673")
                            /** Assigning Merchant And TerminalD And POS To LoggedMerchantPref*/
                            assigningDataToLoggedUserPref(it, "074673", posId)
                            onDataFetched()
                        }
                    }
                    is ServerResponse.Error -> {
                        isLoading.value = false
                        Common.createToast(context, response.message ?: "Server Error")
                    }
                }
            }
        else
            Common.createToast(context, context.getString(R.string.terminal_id_too_short))
    }

    /** This Function Will Inserted All Merchant And Branches Data Locally */
    private suspend fun insertFieldsIntoDataBase(serverMerchant: Merchant?, terminalID: String) {
        val listOfAddress = mutableListOf<Address>()
        val listOfCity = mutableListOf<City>()
        val listOfImages = mutableListOf<Images>()
        val listOfLanguages = mutableListOf<Language>()
        serverMerchant?.let { merchant ->
            merchant.branches?.let { branches ->
                branches.forEach { branch ->
                    /** Collect Branches Address in One List*/
                    branch.address?.let { address ->
                        /** Assigning BranchID To There Address 1*/
                        address.branchId = branch.id
                        listOfAddress.add(address)
                        /** Collect Branches Address City's in One List*/
                        address.city?.let { city ->
                            /** Assigning BranchID To There City's 2*/
                            city.branchId = branch.id
                            listOfCity.add(city)
                        }
                    }
                    /** Collect Branches Images List*/
                    branch.images?.let { images ->
                        /** Assigning BranchID To There Image 3*/
                        images.branchId = branch.id
                        listOfImages.add(images)
                    }
                    /** Collect Branches Languages in One List*/
                    branch.language?.let { language ->
                        /** Assigning BranchID To There Language 4*/
                        language.branchId = branch.id
                        listOfLanguages.add(language)
                    }

                    branch.users?.let { users ->
                        /** This Nested Iteration For Assigning UserID To There Own Permissions*/
                        users.forEach { user ->
                            /** Assigning BranchID To There Users 5*/
                            user.branchId = branch.id
                            user.permissions?.let { permissions ->
                                permissions.forEach { permission ->
                                    permission.userId = user.userId
                                }
                                /** Insert List Of Branches User Permission */
                                initialSetupRepository.insertBranchesUsersPermissions(
                                    permissions
                                )
                            }
                        }
                        /** Insert List Of Branches Users*/
                        initialSetupRepository.insertBranchesUsers(users)
                    }

                    branch.terminals?.let { terminals ->
                        terminals.forEach { terminal ->
                            /** Assigning BranchID To There Language 6*/
                            terminal.branchId = branch.id
                        }
                        /** Insert List Of Branches Terminals*/
                        initialSetupRepository.insertBranchesTerminals(terminals)
                    }
                }

                /** Insert Merchant Info*/
                initialSetupRepository.insertMerchantInfo(merchant)
                /** Insert List Of Branches*/
                initialSetupRepository.insertMerchantBranches(branches)
                /** Insert Branches Address*/
                initialSetupRepository.insertBranchesAddress(listOfAddress)
                /** Insert Branches Address City's*/
                initialSetupRepository.insertBranchesAddressCites(listOfCity)
                /** Insert Branches Images*/
                initialSetupRepository.insertBranchesImages(listOfImages)
                /** Insert Branches Languages*/
                initialSetupRepository.insertBranchesLanguages(listOfLanguages)
            }

            /** And Insert TerminalID In Data Store*/
            insertTerminalIDInDataStore(terminalID)
        }
    }

    /** This Function For Assigning Merchant And TerminalD And POS To LoggedMerchantPref*/
    private fun assigningDataToLoggedUserPref(
        merchant: Merchant,
        terminalID: String,
        posId: String,
    ) {
        /** Assigning Server Merchant To There Object In LoggedMerchantPref*/
        LoggedMerchantPref.merchant = merchant
        /** Assigning Server Branch To There Object In LoggedMerchantPref*/
        LoggedMerchantPref.branch = merchant.branches?.firstOrNull {
            it.id == it.terminals?.find { terminal ->
                terminal.terminalId == terminalID
            }?.branchId
        } ?: throw IllegalStateException("Not Found Branch")
        /** Assigning POS To There LoggedMerchantPref Object*/
        LoggedMerchantPref.pos = posId
        /** Assigning Terminal To There LoggedMerchantPref Object*/
        LoggedMerchantPref.terminalId = terminalID

    }

    /** This Function For Saving TerminalID In Data Store*/
    private suspend fun insertTerminalIDInDataStore(terminalID: String) {
        context.dataStore.edit { settings ->
            settings[TERMINAL_ID_KEY] = terminalID
        }
    }

    /** Check The Terminal ID Length */
    private fun checkTerminalIDLength(terminalID: String) =
        (terminalID.length == Constants.MIN_TerminalID_LENGTH)

    /** Setup Configuration For QRCode Scan */
    fun setupQRCodeConfiguration(context: Context) =
        ScanQRCodeInitialize.setupQRCodeConfiguration(context)
}