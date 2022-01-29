package com.altkamul.xpay.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altkamul.xpay.R
import com.altkamul.xpay.repositroy.ChangePasswordRepository
import com.altkamul.xpay.sealed.ServerResponse
import com.altkamul.xpay.utils.Constants
import com.altkamul.xpay.utils.LoggedMerchantPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val changePasswordRepository: ChangePasswordRepository,
    private val context: Context,
) : ViewModel() {

    val isLoading = mutableStateOf(false)
    /** First we should get the current user's password  */
    private val currentUserPassword = mutableStateOf<String?>("")

    /** New password required fields */
    val oldPassword = mutableStateOf("")
    val newPassword = mutableStateOf("")
    val confirmPassword = mutableStateOf("")

    init {
        viewModelScope.launch {
            currentUserPassword.value =
                changePasswordRepository.getUserPassword(userId = LoggedMerchantPref.user?.userId ?: "")
        }
    }

    /** A function to update the current password */
    fun updatePassword(
        onValidateFailed: (reason: String) -> Unit,
        onPasswordUpdated: () -> Unit,
        onPasswordUpdateFailed: (reason: String) -> Unit,
    ) {
        viewModelScope.launch {
            when(val currentPass = currentUserPassword.value){
                is String -> {
                    /** First we should validate the current password that the user had entered */
                    if(currentPass.equals(oldPassword.value, false)){
                        onValidateFailed(context.getString(R.string.old_password_not_correct))
                        return@launch
                    }
                    /** Validating the new password with it's confirmation*/
                    if(newPassword.value.length >= Constants.MIN_PASSWORD_LENGTH){
                        if( ! newPassword.value.equals(confirmPassword.value, false)){
                            onValidateFailed(context.getString(R.string.passwords_not_matching))
                            return@launch
                        }
                    } else {
                        onValidateFailed(context.getString(R.string.short_password)+" ${Constants.MIN_PASSWORD_LENGTH}")
                        return@launch
                    }
                    /** Now it's all alright , we should go for it now ! */
                    isLoading.value = true
                    changePasswordRepository.updatePassword(
                        oldPassword= newPassword.value,
                        newPassword = confirmPassword.value,
                    ).let{
                        isLoading.value = false
                        /** Checking the response that we had got */
                        when(it){
                            is ServerResponse.Success -> {
                                /** Update local user */
                                changePasswordRepository.updateLocalUser(
                                    userId = LoggedMerchantPref.user?.userId ?: "",
                                    newPassword = newPassword.value
                                )
                                onPasswordUpdated()
                            }
                            else -> {
                                onPasswordUpdateFailed(it.message ?: "Unknown error !")
                            }
                        }
                    }
                }
                else -> {
                    onValidateFailed(context.getString(R.string.no_current_pass_found))
                }
            }
        }
    }
}