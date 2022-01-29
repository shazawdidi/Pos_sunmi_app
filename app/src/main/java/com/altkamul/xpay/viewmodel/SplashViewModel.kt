package com.altkamul.xpay.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altkamul.xpay.repositroy.SplashRepository
import com.altkamul.xpay.utils.LoggedMerchantPref
import com.altkamul.xpay.utils.TERMINAL_ID_KEY
import com.altkamul.xpay.utils.dataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class SplashViewModel
@Inject constructor(
    private val splashRepository: SplashRepository,
    context: Context,
) :
    ViewModel() {
    /** Get TerminalID From DataStore*/
    val terminalID: Flow<String> = context.dataStore.data.map {
        it[TERMINAL_ID_KEY] ?: ""
    }

    val checkLocalRoom = mutableStateOf(true)

    /** This Function Will Check If Room Was Empty*/
    fun checkIfRoomIsEmpty(terminalID: String) {
        viewModelScope.launch {
            /** Get Merchant From Local*/
            val merchant = splashRepository.getMerchantInfoLocally()
            merchant?.let {

                /** Get Main Configuration From Local*/
                val localConfig = splashRepository.getMainConfig()

                /** first getting Terminal by TerminalID */
                val terminal = splashRepository.getTerminal(terminalID)

                /** Second getting Branch With Data*/
                val branchWithData =
                    splashRepository.getCurrentBranch(terminal.branchId ?: "")

                /** Assigning Local Branch To There LoggedMerchantPref Branch*/
                branchWithData?.branch?.images = branchWithData?.image
                branchWithData?.branch?.terminals = branchWithData?.terminal
                branchWithData?.branch?.users = branchWithData?.userBranch
                branchWithData?.branch?.language = branchWithData?.language
                branchWithData?.branch?.address = branchWithData?.address
                LoggedMerchantPref.branch = branchWithData?.branch

                /** Assigning Local Merchant To There Object In LoggedMerchantPref*/
                LoggedMerchantPref.merchant = it

                /** Assigning Local Main Configuration To There Object In LoggedMerchantPref*/
                LoggedMerchantPref.configuration = localConfig

                LoggedMerchantPref.terminalId = terminalID
                LoggedMerchantPref.pos = terminal.posId
                /** Change UI Status To ' Find Local Merchant Successfully'*/
                checkLocalRoom.value = false
            }
        }
    }
}