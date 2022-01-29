package com.altkamul.xpay.repositroy

import com.altkamul.xpay.api.ApiClientImp
import com.altkamul.xpay.db.DataAccessObject
import com.altkamul.xpay.model.Category
import com.altkamul.xpay.model.Item
import com.altkamul.xpay.model.LocalDataVersions
import com.altkamul.xpay.model.SubCategory
import javax.inject.Inject

class LoadingRepository @Inject constructor(
    private val api: ApiClientImp,
    private val dataAccessObject: DataAccessObject
) {

    /** This Function Will Get Data Version Remotely*/
    suspend fun getDataVersion() = api.getLatestDataVersion()

    /** This Function Will GET ALL CATEGORY Remotely */
    suspend fun getAllCategories() = api.getAllCategoriesAPI()

    /** This Function Will GET ALL SUB CATEGORY  Remotely */
    suspend fun getAllSubCategories() = api.getAllSubCategoriesAPI()

    /** This Function Will GET ALL ITEMS Remotely */
    suspend fun getAllItem() = api.getAllItemsAPI()

    /** This Function Will Insert Data Version Locally */
    suspend fun insertDataVersion(dataVersions: LocalDataVersions) =
        dataAccessObject.insertDataVersion(dataVersions)

    /** This Function Will Insert All Category Locally */
    suspend fun insertCategoriesList(category: List<Category>) =
        dataAccessObject.insertCategories(category)

    /** This Function Will Insert All Sub Category Locally */
    suspend fun insertSubCategoriesList(subCategory: List<SubCategory>) =
        dataAccessObject.insertSubCategories(subCategory)

    /** This Function Will Insert All Items Locally */
    suspend fun insertAllItemsList(item: List<Item>) = dataAccessObject.insertItems(item)

}