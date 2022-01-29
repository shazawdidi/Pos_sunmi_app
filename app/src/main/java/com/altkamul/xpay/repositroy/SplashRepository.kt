package com.altkamul.xpay.repositroy

import com.altkamul.xpay.db.DataAccessObject
import com.altkamul.xpay.model.LocalMainConfiguration
import javax.inject.Inject

class SplashRepository @Inject constructor(private val dataAccessObject: DataAccessObject) {

    /** This Function Will Get Local Main Configuration*/
    suspend fun getMainConfig(): LocalMainConfiguration? = dataAccessObject.getMerchantConfig()

    /** This Function Will Get Local Merchant Info*/
    suspend fun getMerchantInfoLocally() =
        dataAccessObject.getMerchantInfo()

    /** This Function Will Return Branch With Data*/
    suspend fun getCurrentBranch(branchID: String) = dataAccessObject.getBranchWithDataByID(branchID)

    /** This Function Will Return Terminal by TerminalID*/
    suspend fun getTerminal(terminalID: String) = dataAccessObject.getTerminal(terminalID)

}