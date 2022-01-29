package com.altkamul.xpay.repositroy

import com.altkamul.xpay.api.ApiClientImp
import com.altkamul.xpay.db.DataAccessObject
import com.altkamul.xpay.model.Cashiers
import com.altkamul.xpay.model.ContactUs
import com.altkamul.xpay.model.LocalMainConfiguration
import com.altkamul.xpay.model.PosRole
import com.altkamul.xpay.model.response.print.Data
import javax.inject.Inject

class BackgroundTasksRepository @Inject constructor(
    private val dataAccessObject: DataAccessObject,
    private val api: ApiClientImp
) {

    /** This Function Will Return Contact Data From The Server*/
    suspend fun getContactDataRemotely() = api.getContactData()

    /** This Function Will Get Main Configuration Data Remotely */
    suspend fun getMainConfiguration() = api.getMainConfigurationAPI()

    /** This Function Will GET INVOICE LAYOUT*/
    suspend fun getInvoiceLayout() = api.getInvoiceLayoutAPI()

    /** This Function Will Get All Cashiers*/
    suspend fun getAllCashiers() = api.getAllCashiers()

    /** This Function Will Get All Roles From Server*/
    suspend fun getAllRoles() = api.getAllRoles()

    /** This Function Will Insert List Of Cashiers*/
    suspend fun insertAllCashiers(cashiers: List<Cashiers>) =
        dataAccessObject.insertCashierList(cashiers)

    /** This Function Will Insert List Of Roles*/
    suspend fun insertRoles(roles: List<PosRole>) = dataAccessObject.insertRolesList(roles)

    /** This Function Will Insert Main Configuration Locally */
    suspend fun insertMainConfig(mainConfig: LocalMainConfiguration) =
        dataAccessObject.insertMainConfigData(mainConfig)

    /** This Function Will Insert Invoice Layout Locally */
    suspend fun insertInvoiceData(data: List<Data>) = dataAccessObject.insertInvoiceData(data)

    /** This Function Will Insert Contact Data*/
    suspend fun insertContactDataLocally(contactUs: ContactUs) {
        dataAccessObject.insertContactInfo(contactUs)
    }
}