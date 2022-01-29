package com.altkamul.xpay.repositroy

import com.altkamul.xpay.db.DataAccessObject
import javax.inject.Inject

class HomeScreenRepository @Inject constructor(private val dataAccessObject: DataAccessObject) {

    /** This Function Will Get All Local Category**/
    suspend fun getAllCategory() = dataAccessObject.getCategories()

    /** This Function Will Get All Local Sub Category**/
    suspend fun getAllSubCategory() = dataAccessObject.getSubCategories()

    /** This Function Will Get All Local Items **/
    suspend fun getAllItems() = dataAccessObject.getAllItems()

    /** This Function Will Return List Of Quick Access Items*/
    suspend fun getAllQuickAccessItems() = dataAccessObject.getAllQuickAccessItems()

}