package com.altkamul.xpay.repositroy

import com.altkamul.xpay.api.ApiClientImp
import com.altkamul.xpay.db.DataAccessObject
import com.altkamul.xpay.model.Terminal
import com.altkamul.xpay.model.request.CheckoutRequest
import javax.inject.Inject

class ChangeBranchRepository @Inject constructor(
    private val dataAccessObject: DataAccessObject,
    private val apiClientImp: ApiClientImp
) {

    /** This FUnction Will Return List Of Branches Locally*/
    suspend fun getBranches() = dataAccessObject.getBranches()

    /** This Function Will Return Branch With ID And There Data*/
    suspend fun getBranchWithDataByID(branchID: String) =
        dataAccessObject.getBranchWithDataByID(branchID)

    /** This FUnction Will Clear All Users Table*/
    suspend fun resetTheDatabase() {
        dataAccessObject.resetItemsTable()
        dataAccessObject.resetCategoryTable()
        dataAccessObject.resetCashiersTable()
        dataAccessObject.resetContactUsTable()
        dataAccessObject.resetDataVersionTable()
        dataAccessObject.resetQuickAccessTable()
        dataAccessObject.resetSubCategoryTable()
        dataAccessObject.resetTransactionTable()
        dataAccessObject.resetInvoiceLayoutTable()
        dataAccessObject.resetTransactionItemsTable()
        dataAccessObject.resetMainConfigurationTable()
        dataAccessObject.resetTransactionPaymentTypeTable()
    }

    /** This Function Will Return List Of Transaction*/
    suspend fun getLocalOfflineTransactions(isOffline: Boolean) =
        dataAccessObject.getLocalOfflineTransactions(isOffline)

    /** This Function For Uploading Offline Transaction*/
    suspend fun uploadOfflineTransaction(checkoutRequest: CheckoutRequest) =
        apiClientImp.makeTransaction(checkoutRequest)

    /** This Function Will Getting All Cashiers*/
    suspend fun getAllCashiers() = dataAccessObject.getCashierList()

    /** This Function For Auto Login*/
    suspend fun autoLogin(
        pos: String,
        terminal: String,
        password: String,
        isSupportLogin: Boolean,
        isLoginWithEncryptedPassword: Boolean
    ) = apiClientImp.checkUserAuthentication(pos, terminal, password, isSupportLogin, isLoginWithEncryptedPassword)
}