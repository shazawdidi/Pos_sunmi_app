package com.altkamul.xpay.repositroy

import com.altkamul.xpay.api.ApiClientImp
import com.altkamul.xpay.db.DataAccessObject
import com.altkamul.xpay.model.*
import javax.inject.Inject

class InitialSetupRepository @Inject constructor(
    private val dataAccessObject: DataAccessObject,
    private val apiClientImp: ApiClientImp
) {

    /** This Function Will Insert Merchant Info*/
    suspend fun insertMerchantInfo(merchant: Merchant) =
        dataAccessObject.insertMerchantInfo(merchant)

    /** This Function  Will Insert List OF Merchant Branches*/
    suspend fun insertMerchantBranches(branch: List<Branch>) =
        dataAccessObject.insertBranches(branch)

    /** This Function Will Insert List Of Branches Address*/
    suspend fun insertBranchesAddress(address: List<Address>) =
        dataAccessObject.insertBranchesAddress(address)

    /** This Function Will Insert List Of Address City's*/
    suspend fun insertBranchesAddressCites(city: List<City>) =
        dataAccessObject.insertBranchesAddressCites(city)

    /** This Function Will Insert List Of Branches Images*/
    suspend fun insertBranchesImages(images: List<Images>) =
        dataAccessObject.insertBranchesImages(images)

    /** This Function Will Insert List Of Branches Languages*/
    suspend fun insertBranchesLanguages(language: List<Language>) =
        dataAccessObject.insertBranchesLanguages(language)

    /** This Function Will Insert Branches Users Permissions*/
    suspend fun insertBranchesTerminals(terminal: List<Terminal>) =
        dataAccessObject.insertBranchesTerminals(terminal)
    
    /** This Function Will Insert Branches Users*/
    suspend fun insertBranchesUsers(branchesUsers: List<BranchesUsers>) =
        dataAccessObject.insertBranchesUsers(branchesUsers)

    /** This Function Will Insert Branches Users Permissions*/
    suspend fun insertBranchesUsersPermissions(permission: List<Permission>) =
        dataAccessObject.insertBranchesUsersPermissions(permission)

    /** This Function Will Get Merchant Data Remotely*/
    suspend fun getRemoteMerchantInfo(terminalID: String, posId: String) =
        apiClientImp.getMerchantInfo(posId, terminalID)
}