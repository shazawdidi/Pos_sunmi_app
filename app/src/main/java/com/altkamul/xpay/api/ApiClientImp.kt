package com.altkamul.xpay.api

import com.altkamul.xpay.model.*
import com.altkamul.xpay.model.request.*
import com.altkamul.xpay.model.response.CategoryResponse
import com.altkamul.xpay.model.response.CheckoutResponse
import com.altkamul.xpay.model.response.ItemsResponse
import com.altkamul.xpay.model.response.SubCategoryResponse
import com.altkamul.xpay.model.request.BasicInfoRequest
import com.altkamul.xpay.model.request.ChangePasswordRequest
import com.altkamul.xpay.model.request.CheckoutRequest
import com.altkamul.xpay.model.request.LoginRequest
import com.altkamul.xpay.model.response.*
import com.altkamul.xpay.model.response.print.InvoiceLayoutResponse
import com.altkamul.xpay.sealed.ServerResponse
import com.altkamul.xpay.utils.Constants
import com.altkamul.xpay.utils.Constants.ALL_CATEGORIES_URL
import com.altkamul.xpay.utils.Constants.ALL_ITEMS_URL
import com.altkamul.xpay.utils.Constants.ALL_SUBCATEGORIES_URL
import com.altkamul.xpay.utils.Constants.BASE_URL
import com.altkamul.xpay.utils.Constants.BasicInfo_Url
import com.altkamul.xpay.utils.Constants.CHECKOUT_URL
import com.altkamul.xpay.utils.Constants.CLAIM_TRANSACTION_BY_ID
import com.altkamul.xpay.utils.Constants.ContactUs_URL
import com.altkamul.xpay.utils.Constants.LAYOUT_INVOICE_URL
import com.altkamul.xpay.utils.Constants.MAIN_CONFIG_URL
import com.altkamul.xpay.utils.Constants.MERCHANT_TEAM_LOGIN_URL
import com.altkamul.xpay.utils.LoggedMerchantPref
import com.altkamul.xpay.utils.getMessage
import com.altkamul.xpay.utils.handleResponseException
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import javax.inject.Inject


class ApiClientImp @Inject constructor(
    private val client: HttpClient,
) : ApiClient {

    /** A function that authenticate system's user . It takes pos, terminalId and also the user's password */
    override suspend fun checkUserAuthentication(
        pos: String,
        terminalId: String,
        password: String,
        isSupportLogin: Boolean,
        isLoginWithEncryptedPassword: Boolean
    ): ServerResponse<User> {
        /** The login url depend on the type of the user who want to login : merchant team member or one of support team */
        val loginUrl =
            when {
                isLoginWithEncryptedPassword -> Constants.LOGIN_WITH_ENCRYPTED_PASSWORD
                isSupportLogin -> Constants.SUPPORT_LOGIN_URL
                else -> MERCHANT_TEAM_LOGIN_URL
            }
                return try {
                    val response: HttpResponse = client.post(urlString = BASE_URL + loginUrl) {
                        body =
                            LoginRequest(posid = pos, terminalID = terminalId, password = password)
                        contentType(ContentType.Application.Json)
                    }
                    /** Cheers , welcome user */
                    ServerResponse.Success(data = response.receive())
                } catch (throwable: Throwable) {
                    /** We got a code another of 2xx , we should handle it */
                    throwable.handleResponseException()
                }
    }

    /** This Function will getting Merchant Information from the Server */
    override suspend fun getMerchantInfo(
        pos: String,
        terminalId: String
    ): ServerResponse<Merchant> {
        return try {
            val response: HttpResponse = client.request(urlString = "$BASE_URL$BasicInfo_Url") {
                method = HttpMethod.Post
                contentType(ContentType.Application.Json)
                body = BasicInfoRequest(terminalId, pos)
            }
            ServerResponse.Success(data = response.receive())
        } catch (throwable: Throwable) {
            throwable.handleResponseException()
        }
    }

    /** This Function will Return Main Configuration Merchant */
    override suspend fun getMainConfigurationAPI(): ServerResponse<MainConfiguration> {
        return try {
            val response: HttpResponse = client.request(urlString = "$BASE_URL$MAIN_CONFIG_URL") {
                this.method = HttpMethod.Get
                headers {
                    append(HttpHeaders.Authorization, "Bearer " + LoggedMerchantPref.token)
                }
            }
            ServerResponse.Success(data = response.receive())
        } catch (throwable: Throwable) {
            throwable.handleResponseException()
        }
    }

    /** This Function will Return All Category*/
    override suspend fun getAllCategoriesAPI(): ServerResponse<CategoryResponse> {
        return try {
            val response: HttpResponse =
                client.request(urlString = "$BASE_URL$ALL_CATEGORIES_URL") {
                    this.method = HttpMethod.Get
                    headers {
                        append(HttpHeaders.Authorization, "Bearer " + LoggedMerchantPref.token)
                    }
                }
            ServerResponse.Success(data = response.receive())
        } catch (throwable: Throwable) {
            throwable.handleResponseException()
        }
    }

    // MAINCONFIGURATION IMPLEMENTING  API CLIENT
    override suspend fun getInvoiceLayoutAPI(): ServerResponse<InvoiceLayoutResponse> {
        val layoutPrintUrl = LAYOUT_INVOICE_URL
        val response: HttpResponse = client.request(urlString = "$BASE_URL$layoutPrintUrl") {
            this.method = HttpMethod.Get
            headers {
                append(HttpHeaders.Authorization, "Bearer " + LoggedMerchantPref.token)
            }
        }
        return if (response.status.isSuccess()) {
            ServerResponse.Success(data = response.receive())
        } else {
            ServerResponse.Error(message = response.getMessage())
        }
    }


    /** This Function will Return All Sub Category*/
    override suspend fun getAllSubCategoriesAPI(): ServerResponse<SubCategoryResponse> {
        return try {
            val response: HttpResponse =
                client.request(urlString = "$BASE_URL$ALL_SUBCATEGORIES_URL") {
                    this.method = HttpMethod.Get
                    headers {
                        append(HttpHeaders.Authorization, "Bearer " + LoggedMerchantPref.token)
                    }
                }
            ServerResponse.Success(data = response.receive())
        } catch (throwable: Throwable) {
            throwable.handleResponseException()
        }
    }

    /** This Function will Return All SubCategory Items*/
    override suspend fun getAllItemsAPI(): ServerResponse<ItemsResponse> {
        return try {
            val response: HttpResponse = client.request(urlString = "$BASE_URL$ALL_ITEMS_URL") {
                this.method = HttpMethod.Get
                headers {
                    append(HttpHeaders.Authorization, "Bearer " + LoggedMerchantPref.token)
                }
            }
            ServerResponse.Success(data = response.receive())
        } catch (throwable: Throwable) {
            throwable.handleResponseException()
        }
    }

    /** //////////////////////////////////// SYNC DATA APIs //////////////////////// */
    /** A function that fetch the latest version of data from the server */
    override suspend fun getLatestDataVersion(): ServerResponse<List<DataVersion>> {
        return try {
            val response: HttpResponse =
                client.get(urlString = BASE_URL + Constants.DATA_VERSION_URL) {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer " + LoggedMerchantPref.token)
                    }
                }
            /** Cheers , new version */
            ServerResponse.Success(data = response.receive())
        } catch (throwable: Throwable) {
            /** We got a code another of 2xx , we should handle it */
            throwable.handleResponseException()
        }
    }

    override suspend fun syncCategories(currentVersion: Int): ServerResponse<List<Category>> {
        return try {
            val response: HttpResponse =
                client.get(urlString = BASE_URL + Constants.SYNC_CATEGORIES_URL + "/$currentVersion") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer " + LoggedMerchantPref.token)
                    }
                }
            /** Cheers , our updated categories */
            ServerResponse.Success(data = response.receive())
        } catch (throwable: Throwable) {
            /** We got a code another of 2xx , we should handle it */
            throwable.handleResponseException()
        }
    }

    override suspend fun syncSubCategories(currentVersion: Int): ServerResponse<List<SubCategory>> {
        return try {
            val response: HttpResponse =
                client.get(urlString = BASE_URL + Constants.SYNC_SUBCATEGORIES_URL + "/$currentVersion") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer " + LoggedMerchantPref.token)
                    }
                }
            /** Cheers , our updated categories */
            ServerResponse.Success(data = response.receive())
        } catch (throwable: Throwable) {
            /** We got a code another of 2xx , we should handle it */
            throwable.handleResponseException()
        }
    }

    override suspend fun syncItems(currentVersion: Int): ServerResponse<List<Item>> {
        return try {
            val response: HttpResponse =
                client.get(urlString = BASE_URL + Constants.SYNC_ITEMS_URL + "/$currentVersion") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer " + LoggedMerchantPref.token)
                    }
                }
            /** Cheers , our updated categories */
            ServerResponse.Success(data = response.receive())
        } catch (throwable: Throwable) {
            /** We got a code another of 2xx , we should handle it */
            throwable.handleResponseException()
        }
    }


    /** This Function Will Return Contact Us Data */
    override suspend fun getContactData(): ServerResponse<ContactUs> {
        return try {
            val response: HttpResponse = client.request(urlString = "$BASE_URL$ContactUs_URL") {
                method = HttpMethod.Get
                headers {
                    append(HttpHeaders.Authorization, "Bearer " + LoggedMerchantPref.token)
                }
            }
            ServerResponse.Success(data = response.receive())
        } catch (throwable: Throwable) {
            throwable.handleResponseException()
        }
    }

    /**
     * A function that is used to send a transaction request to the server
     * It takes the checkout request object #CheckoutRequest
     */
    override suspend fun makeTransaction(checkoutRequest: CheckoutRequest): ServerResponse<CheckoutResponse> {
        return try {
            val response: HttpResponse = client.post(urlString = BASE_URL + CHECKOUT_URL) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer " + LoggedMerchantPref.token)
                }
                contentType(ContentType.Application.Json)
                body = checkoutRequest
            }
            /** Got the transaction result successfully */
            ServerResponse.Success(data = response.receive())
        } catch (throwable: Throwable) {
            /** We got a code another of 2xx , we should handle it */
            throwable.handleResponseException()
        }
    }

    /** A function to get a specific transaction */
    override suspend fun getTransactionWithId(transactionId: Int): ServerResponse<Transaction> {
        return try {
            val response: HttpResponse =
                client.get(urlString = "$BASE_URL${Constants.GET_TRANSACTION}/$transactionId") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer " + LoggedMerchantPref.token)
                    }
                }
            /** Oh, we had the transaction indeed ! */
            ServerResponse.Success(data = response.receive())
        } catch (throwable: Throwable) {
            /** We got a code another of 2xx , we should handle it */
            throwable.handleResponseException()
        }
    }

    /** A function to get the last transaction from the api for this branch */
    override suspend fun getLastTransaction(
        branchId: String,
        terminalId: String
    ): ServerResponse<Transaction> {
        return try {
            val response: HttpResponse =
                client.get(urlString = "$BASE_URL${Constants.GET_LAST_TRANSACTION}/$branchId/$terminalId") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer " + LoggedMerchantPref.token)
                    }
                }
            /** Oh, we had the transaction indeed ! */
            ServerResponse.Success(data = response.receive())
        } catch (throwable: Throwable) {
            /** We got a code another of 2xx , we should handle it */
            throwable.handleResponseException()
        }
    }

    /** A function to get list of cashiers from the api for this branch */
    override suspend fun getAllCashiers(): ServerResponse<CashiersResponse> {
        return try {
            val response: HttpResponse =
                client.get(urlString = "$BASE_URL${Constants.GET_ALL_CASHIERS}") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer " + LoggedMerchantPref.token)
                    }
                }
            ServerResponse.Success(data = response.receive())
        } catch (throwable: Throwable) {
            throwable.handleResponseException()
        }
    }

    /** A Function To Claim Transaction By ID*/
    override suspend fun claimTransactionByID(transactionId: Int): ServerResponse<ClaimResponse> {
        return try {
            val response: HttpResponse =
                client.post(urlString = "$BASE_URL$CLAIM_TRANSACTION_BY_ID$transactionId") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer " + LoggedMerchantPref.token)
                    }
                    contentType(ContentType.Application.Json)
                }
            ServerResponse.Success(data = response.receive())
        } catch (throwable: Throwable) {
            throwable.handleResponseException()
        }
    }

    /** A function to Change Password & send update to the server*/
    override suspend fun changePassword(changePasswordRequest: ChangePasswordRequest): ServerResponse<Any> {
        return try {
            val response: HttpResponse =
                client.post(urlString = "$BASE_URL${Constants.CHANGE_PASSWORD}") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer " + LoggedMerchantPref.token)
                    }
                    body = changePasswordRequest
                    contentType(ContentType.Application.Json)
                }
            ServerResponse.Success(data = response.receive())
        } catch (throwable: Throwable) {
            throwable.handleResponseException()
        }
    }

    /** A function to Add New Cashier & send  to the server*/
    override suspend fun addUser(addCashierRequest: AddUserRequest): ServerResponse<AccountOperationResponse> {
        return try {
            val response: HttpResponse =
                client.post(urlString = "$BASE_URL${Constants.ADD_USER}") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer " + LoggedMerchantPref.token)
                    }
                    contentType(ContentType.Application.Json)
                    body = addCashierRequest
                }
            ServerResponse.Success(data = response.receive())
        } catch (throwable: Throwable) {
            throwable.handleResponseException()
        }
    }

    /** A function to Edit User  & send  to the server*/
    override suspend fun editUser(
        userId: String,
        editUserRequest: EditUserRequest
    ): ServerResponse<AccountOperationResponse> {
        return try {
            val response: HttpResponse =
                client.post(urlString = "$BASE_URL${Constants.EDIT_USER}/$userId") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer " + LoggedMerchantPref.token)
                    }
                    contentType(ContentType.Application.Json)
                    body = editUserRequest
                }
            /** Oh, we had the history indeed ! */
            ServerResponse.Success(data = response.receive())
        } catch (throwable: Throwable) {
            /** We got a code another of 2xx , we should handle it */
            throwable.handleResponseException()
        }
    }

    override suspend fun deleteUser(userId: String): ServerResponse<AccountOperationResponse> {
        return try {
            val response: HttpResponse =
                client.get(urlString = "$BASE_URL${Constants.DELETE_USER}/$userId") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer " + LoggedMerchantPref.token)
                    }
                    contentType(ContentType.Application.Json)
                }
            /** Oh, we had the history indeed ! */
            ServerResponse.Success(data = response.receive())
        } catch (throwable: Throwable) {
            /** We got a code another of 2xx , we should handle it */
            throwable.handleResponseException()
        }
    }

    /** A function to get the transactions history from api on a specific date */
    override suspend fun getTransactionsHistory(date: String): ServerResponse<List<Transaction>> {
        return try {
            val response: HttpResponse =
                client.get(urlString = "$BASE_URL${Constants.GET_TRANSACTION_HISTORY}/$date") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer " + LoggedMerchantPref.token)
                    }

                }
            /** Oh, we had the history indeed ! */
            ServerResponse.Success(data = response.receive())
        } catch (throwable: Throwable) {
            /** We got a code another of 2xx , we should handle it */
            throwable.handleResponseException()
        }
    }

    /** A function to get list of Roles from the api for this branch */
    override suspend fun getAllRoles(): ServerResponse<Roles> {
        return try {
            val response: HttpResponse =
                client.get(urlString = "$BASE_URL${Constants.GET_ALL_ROLES}") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer " + LoggedMerchantPref.token)
                    }
                }
            ServerResponse.Success(data = response.receive())
        } catch (throwable: Throwable) {
            throwable.handleResponseException()
        }
    }

}