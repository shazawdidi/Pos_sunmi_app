package com.altkamul.xpay.repositroy

import com.altkamul.xpay.api.ApiClientImp
import com.altkamul.xpay.db.DataAccessObject
import com.altkamul.xpay.model.User
import com.altkamul.xpay.model.request.ChangePasswordRequest
import com.altkamul.xpay.sealed.ServerResponse
import javax.inject.Inject

class ChangePasswordRepository @Inject constructor(
    private val dataAccessObject: DataAccessObject,
    private val apiClientImp: ApiClientImp
) {
    suspend fun getUserPassword(userId: String) = dataAccessObject.getUserPasswordWithId(userId = userId)

    suspend fun updatePassword(oldPassword: String, newPassword: String) =
        apiClientImp.changePassword(
            changePasswordRequest = ChangePasswordRequest(
                oldPassword = oldPassword,
                newPassword = newPassword,
            )
        )

    suspend fun updateLocalUser(userId: String, newPassword: String) {
        dataAccessObject.updateUserPassword(userId = userId, newPassword = newPassword)
    }
}