package com.altkamul.xpay.repositroy

import com.altkamul.xpay.db.DataAccessObject
import javax.inject.Inject

class ContactUsRepository @Inject constructor(
    private val dataAccessObject: DataAccessObject
) {

    /** This Function Will Return Contact Data From The Local*/
    suspend fun getContactDataLocally() = dataAccessObject.getContactData()

}