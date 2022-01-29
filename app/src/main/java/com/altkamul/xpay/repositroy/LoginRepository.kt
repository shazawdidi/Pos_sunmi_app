package com.altkamul.xpay.repositroy

import com.altkamul.xpay.api.ApiClientImp
import com.altkamul.xpay.db.DataAccessObject
import com.altkamul.xpay.model.LocalDataVersions
import com.altkamul.xpay.model.User
import com.altkamul.xpay.sealed.ServerResponse
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val api: ApiClientImp,
    private val dao: DataAccessObject,
) {

    /**
     * Get user authentication , take pos - IMEI - and terminalId and also the password
     * it also take a Boolean to indicate if it's a support login or not , it's false by default X-)
     */
    suspend fun getUserAuthentication(
        pos: String,
        terminalId: String,
        password: String,
        isSupportLogin: Boolean,
    ) : ServerResponse<User>{
        return api.checkUserAuthentication(
            pos = pos,
            terminalId = terminalId,
            password = password,
            isSupportLogin = isSupportLogin
        )
    }

    /**
     * A function to check if the app loaded the data before by getting data's version stored locally
     * return a DataVersion
     */
    suspend fun getLocalDataVersions() : LocalDataVersions? {
        return dao.getDataVersions()
    }
}