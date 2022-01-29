package com.altkamul.xpay.repositroy

import com.altkamul.xpay.api.ApiClientImp
import com.altkamul.xpay.db.DataAccessObject
import com.altkamul.xpay.model.Cashiers
import com.altkamul.xpay.model.request.AddUserRequest
import com.altkamul.xpay.model.request.EditUserRequest
import javax.inject.Inject

class AccountRepository @Inject constructor(
    private val apiClientImp: ApiClientImp,
    private val dataAccessObject: DataAccessObject
) {
    suspend fun getAllCashierLocally() = dataAccessObject.getCashierList()

    suspend fun getAllRolesLocally() = dataAccessObject.getRolesList()

    suspend fun insertUserLocally(cashiers: List<Cashiers>) =
        dataAccessObject.insertCashierList(cashiers)

    suspend fun addAccountInServer(addUserRequest: AddUserRequest) =
        apiClientImp.addUser(addUserRequest)

    suspend fun editAccountInServer(editUserRequest: EditUserRequest, userID: String) =
        apiClientImp.editUser(userID, editUserRequest)

    suspend fun deleteUserInServer(userID: String) = apiClientImp.deleteUser(userID)

    suspend fun deleteUserInLocal(userID: String) = dataAccessObject.deleteUser(userID)
}