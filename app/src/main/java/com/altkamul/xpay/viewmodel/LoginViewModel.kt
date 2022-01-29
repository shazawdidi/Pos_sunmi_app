package com.altkamul.xpay.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altkamul.xpay.model.LocalDataVersions
import com.altkamul.xpay.repositroy.LoginRepository
import com.altkamul.xpay.sealed.ServerResponse
import com.altkamul.xpay.utils.Constants
import com.altkamul.xpay.utils.LoggedMerchantPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * A View model with hiltViewModel annotation that is used to access this view model everywhere needed
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {
    /** A value that hold Options menu state */
    val isOptionsMenuExpanded = mutableStateOf(false)
    /** A value that indicate if one of the support's team want to login , its false by default of course :-D */
    val isSupportLoginChecked = mutableStateOf(false)
    /** boolean to show/hide the loading progress bar */
    val isLoading = mutableStateOf(false)
    /** A value that hold password's input field state */
    val password = mutableStateOf("")

    fun validatePassword(onValidationFailed: () -> Unit, onLoggedCompleted: (version: LocalDataVersions?) -> Unit,onLoggingFailed: (message: String) -> Unit){
        /** We have to check the password's length , if its less than MIN_PASSWORD_LENGTH then return */
        val passwordTooShort =  password.value.length < Constants.MIN_PASSWORD_LENGTH
        if (passwordTooShort){
            onValidationFailed()
        } else {
            /** Now we are sure that the password is valid , so first we start requesting the authentication */
            checkUserAuthentication(
                onUserAuthenticated = { version->
                    onLoggedCompleted(version)
                },
                onUserAuthenticationFailed = { message ->
                    onLoggingFailed(message)
                }
            )
        }
    }

    private fun checkUserAuthentication(onUserAuthenticated : (version: LocalDataVersions?) -> Unit, onUserAuthenticationFailed: (message: String) -> Unit) {
        Timber.d("starting ... ")
        isLoading.value = true
        viewModelScope.launch {
            /** Check if user is authorized or not */
            val response = loginRepository.getUserAuthentication(
                password = password.value,
                terminalId = LoggedMerchantPref.branch?.terminals?.first()?.terminalId ?: "074673",
                pos = LoggedMerchantPref.pos ?: "353007061714083",
                isSupportLogin = isSupportLoginChecked.value
            )
            /** Stop loading */
            isLoading.value = false
            /** Checking the type of the response */
            when (response) {
                is ServerResponse.Success -> {
                    Timber.d("logged successfully !")
                    /** Setting our user instance */
                    LoggedMerchantPref.user = response.data
                    /** Now check if the data had loaded before and delegate the authentication event */
                    getCurrentDataVersion { currentVersion ->
                        onUserAuthenticated(currentVersion)
                    }
                }
                is ServerResponse.Error -> {
                    /** Authentication failed */
                    onUserAuthenticationFailed(response.message ?: "message should be available")
                }
            }
        }
    }

    /**
     * A function to check whether or not this app had fetched the data before
     */
    private fun getCurrentDataVersion(onDataVersionFetched: (version: LocalDataVersions?) -> Unit){
        viewModelScope.launch {
            /** If the fetched version is null - which shouldn't be the case - then we should return  */
            val version = loginRepository.getLocalDataVersions()
            onDataVersionFetched(version)
        }
    }
}
