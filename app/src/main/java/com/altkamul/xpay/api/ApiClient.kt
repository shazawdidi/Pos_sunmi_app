package com.altkamul.xpay.api

import com.altkamul.xpay.model.*
import com.altkamul.xpay.model.request.AddUserRequest
import com.altkamul.xpay.model.request.ChangePasswordRequest
import com.altkamul.xpay.model.request.CheckoutRequest
import com.altkamul.xpay.model.response.*
import com.altkamul.xpay.model.request.EditUserRequest
import com.altkamul.xpay.model.response.CategoryResponse
import com.altkamul.xpay.model.response.CheckoutResponse
import com.altkamul.xpay.model.response.ItemsResponse
import com.altkamul.xpay.model.response.SubCategoryResponse
import com.altkamul.xpay.model.response.print.InvoiceLayoutResponse
import com.altkamul.xpay.sealed.ServerResponse

interface ApiClient {

    /** A function that authenticate system's user . It takes pos, terminalId and also the user's password */
    suspend fun checkUserAuthentication(
        pos: String,
        terminalId: String,
        password: String,
        isSupportLogin: Boolean,
        isLoginWithEncryptedPassword: Boolean = false
    ): ServerResponse<User>

    /** Get the last versions that the server had */
    suspend fun getLatestDataVersion(): ServerResponse<List<DataVersion>>

    /** Syncing categories when it new version is available , take current version to know which version we are missing */
    suspend fun syncCategories(currentVersion: Int): ServerResponse<List<Category>>

    /** Syncing subcategories when it new version is available , take current version to know which version we are missing */
    suspend fun syncSubCategories(currentVersion: Int): ServerResponse<List<SubCategory>>

    /** Syncing items when it new version is available , take current version to know which version we are missing */
    suspend fun syncItems(currentVersion: Int): ServerResponse<List<Item>>

    /** This Function Will Get Main Configuration Data*/
    suspend fun getMainConfigurationAPI(): ServerResponse<MainConfiguration>
    suspend fun getInvoiceLayoutAPI(): ServerResponse<InvoiceLayoutResponse>

    /** This Function Will Getting All Category*/
    suspend fun getAllCategoriesAPI(): ServerResponse<CategoryResponse>

    /** This Function Will Getting All Sub Category*/
    suspend fun getAllSubCategoriesAPI(): ServerResponse<SubCategoryResponse>


    /** This Function Will Return Contact Data */
    suspend fun getContactData(): ServerResponse<ContactUs>

    /** GET ALLITEMS  API*/
    suspend fun getAllItemsAPI(): ServerResponse<ItemsResponse>

    /**
     * This Function Will Return Merchant Data
     */
    suspend fun getMerchantInfo(
        pos: String,
        terminalId: String
    ): ServerResponse<Merchant>


    /**
     * A function that is used to send a transaction request to the server
     * It takes the checkout request object #CheckoutRequest
     */
    suspend fun makeTransaction(checkoutRequest: CheckoutRequest): ServerResponse<CheckoutResponse>


    /** A function to get a specific transaction */
    suspend fun getTransactionWithId(transactionId: Int): ServerResponse<Transaction>

    /** A function to get the last transaction from the api for this branch */
    suspend fun getLastTransaction(
        branchId: String,
        terminalId: String
    ): ServerResponse<Transaction>


    /** A function to get list of cashiers from the api */
    suspend fun getAllCashiers(): ServerResponse<CashiersResponse>

    /** A function to get list of roles from the api */
    suspend fun getAllRoles(): ServerResponse<Roles>

    /** A function to get the transactions history from api on a specific date */
    suspend fun getTransactionsHistory(date: String): ServerResponse<List<Transaction>>

    /** A Function To Claim Transaction By ID*/
    suspend fun claimTransactionByID(transactionId: Int): ServerResponse<ClaimResponse>

    /** A function to Change Password*/
    suspend fun changePassword(changePasswordRequest: ChangePasswordRequest): ServerResponse<Any>

    /** A function to Add New Account or user */
    suspend fun addUser(addCashierRequest: AddUserRequest): ServerResponse<AccountOperationResponse>

    /** A function to Edit user Account or user */
    suspend fun editUser(
        userId: String,
        editUserRequest: EditUserRequest
    ): ServerResponse<AccountOperationResponse>

    /** A function to Edit user Account or user */
    suspend fun deleteUser(userId: String): ServerResponse<AccountOperationResponse>
}