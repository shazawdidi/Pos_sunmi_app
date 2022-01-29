package com.altkamul.xpay.repositroy

import com.altkamul.xpay.api.ApiClientImp
import com.altkamul.xpay.db.DataAccessObject
import com.altkamul.xpay.model.*
import com.altkamul.xpay.model.response.print.Data
import com.altkamul.xpay.sealed.ServerResponse
import javax.inject.Inject


class ParentRepository @Inject constructor(
    private val api: ApiClientImp,
    private val dao: DataAccessObject
) {

    /**
     * A function that used to get the current data version in use, it returns a LocalDataVersion
     */
    suspend fun getLocalDataVersions() : LocalDataVersions? {
        return dao.getDataVersions()
    }

    /** A function to get the latest data version from the server */
    suspend fun getLatestVersion() : ServerResponse<List<DataVersion>> {
        return api.getLatestDataVersion()
    }

    /** A function to get all categories from the local storage */
    suspend fun getAllCategories(): List<Category> {
        return dao.getCategories()
    }

    /** A function to get all subcategories from the local storage */
    suspend fun getAllSubcategories(): List<SubCategory> {
        return dao.getSubCategories()
    }

    /** A function to get all items from the local storage */
    suspend fun getAllItems(): List<Item> {
        return dao.getAllItems()
    }


    suspend fun getLocalInvoiceData() : List<Data> {
        return dao.getPrinterData()
    }

    /** Logout current logged user */
//    suspend fun logoutCurrentUser(): Any {
//
//    }

}