package com.altkamul.xpay.db

import androidx.room.*
import com.altkamul.xpay.model.*
import com.altkamul.xpay.model.Transaction
import com.altkamul.xpay.model.response.TransactionPayment
import com.altkamul.xpay.model.response.local.BranchWithData
import com.altkamul.xpay.model.response.local.TransactionWithItems
import com.altkamul.xpay.model.response.print.Data


@Dao
interface DataAccessObject {

    /** This Function Will Insert Merchant Info*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMerchantInfo(merchant: Merchant)

    /** This Function  Will Insert List OF Merchant Branches*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBranches(branch: List<Branch>)

    /** This Function Will Insert List Of Branches Address*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBranchesAddress(address: List<Address>)

    /** This Function Will Insert List Of Address City's*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBranchesAddressCites(city: List<City>)

    /** This Function Will Insert List Of Branches Images*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBranchesImages(images: List<Images>)

    /** This Function Will Insert List Of Branches Languages*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBranchesLanguages(language: List<Language>)

    /** This Function Will Insert List Of Branches Terminals*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBranchesTerminals(terminal: List<Terminal>)

    /** This Function Will Insert Branches Users*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBranchesUsers(branchesUsers: List<BranchesUsers>)

    /** This Function Will Insert Branches Users Permissions*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBranchesUsersPermissions(permission: List<Permission>)

    /** This Function Will Return Merchant Info*/
    @Query("select * from Merchant_table limit 1")
    suspend fun getMerchantInfo(): Merchant?

    /** This Function Will Return List Of Branches*/
    @Query("select * from MerchantBranch_table")
    suspend fun getBranches(): List<Branch>

    /** This Function Will Return Terminal by TerminalID*/
    @Query("select * from MerchantTerminal_table where terminalId = :terminalID limit 1")
    suspend fun getTerminal(terminalID: String): Terminal

    /** This Function Will Return Branch With Its Data*/
    @Query("SELECT * FROM MerchantBranch_table where id = :branchID limit 1")
    suspend fun getBranchWithDataByID(branchID: String): BranchWithData?

    /** This Function Will Insert Contact Data*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContactInfo(contactUs: ContactUs)

    /** This Function Will Return Contact Data*/
    @Query("select * from contact_table limit 1")
    suspend fun getContactData(): ContactUs?

    @Query("SELECT * FROM versions LIMIT 1")
    suspend fun getDataVersions(): LocalDataVersions?

    /** Synchronization required operations , don't touch it fellas */

    @Update
    suspend fun updateCategories(categories: List<Category>)

    /** Update category version number */
    @Query("UPDATE versions SET categories = :lastVersion where categories = :currentVersion")
    suspend fun updateCategoryVersion(currentVersion: Int, lastVersion: Int)

    @Update
    suspend fun updateSubCategories(subCategories: List<SubCategory>)

    /** Update subcategory version number */
    @Query("UPDATE versions SET subCategories = :lastVersion where subCategories = :currentVersion")
    suspend fun updateSubcategoryVersion(currentVersion: Int, lastVersion: Int)

    @Update
    suspend fun updateItems(items: List<Item>)

    /** Update items version number */
    @Query("UPDATE versions SET items = :lastVersion where items = :currentVersion")
    suspend fun updateItemsVersion(currentVersion: Int, lastVersion: Int)

    @Update
    suspend fun updateDateVersions(dataVersions: LocalDataVersions)

    /** QUERY TO INSERT  DATA VERSION  */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataVersion(dataVersion: LocalDataVersions)

    /** INSERT MAIN CONFIG DATA */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMainConfigData(mainConfig: LocalMainConfiguration)

    /** Get main configuration data */
    @Query("SELECT * FROM MainConfig_table LIMIT 1")
    suspend fun getMerchantConfig(): LocalMainConfiguration?

    /** INSERT CATEGORIES */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<Category>)

    /** Get All Category */
    @Query("select * from categories_table")
    suspend fun getCategories(): List<Category>

    /** Insert quick access items */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertQuickAccessItems(items: MutableList<QuickItem>)

    /** Insert All Sub Category*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubCategories(subCategories: List<SubCategory>)

    /** Get All Sub Category*/
    @Query("select * from subcategory_table")
    suspend fun getSubCategories(): List<SubCategory>

    /** INSERT Items*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(item: List<Item>)

    /** Get All Items*/
    @Query("select * from items_table")
    suspend fun getAllItems(): List<Item>

    /** This Function Will Return List Of Quick Access Items*/
    @Query("select * from QuickItem_table")
    suspend fun getAllQuickAccessItems(): List<QuickItem>

    //invoice data
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoiceData(data: List<Data>)

    @Query("select * from InvoiceData_table")
    suspend fun getPrinterData(): List<Data>

    /** A function that is used to save the transaction that we made locally so that we can access it later */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    /** A function that is used to insert transaction items locally */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactionItems(transactionItems: List<TransactionItem>)

    /** A function that is used to insert transaction payment methods locally */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactionPayments(transactionPayments: List<TransactionPayment>)

    /** Search for a specified transaction */
    @Query("SELECT * FROM transactions WHERE transactionMasterId= :transactionId")
    suspend fun getTransactionWithId(transactionId: Int): TransactionWithItems?

    /** Get the last transaction */
    @Query("SELECT * FROM transactions WHERE id = (SELECT id FROM transactions ORDER BY id DESC LIMIT 1)")
    suspend fun getLastTransaction(): TransactionWithItems?

    /** A function to get all the transactions exist in the local storage */
    @Query("SELECT * FROM transactions")
    suspend fun getLocalTransactions(): List<TransactionWithItems>

    /** A function to get all the offline transactions exist in the local storage */
    @Query("SELECT * FROM transactions where isOffline = :isOffline")
    suspend fun getLocalOfflineTransactions(isOffline: Boolean): List<TransactionWithItems>

    /** A function this save List of cashiers locally */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCashierList(cashiers: List<Cashiers>)

    /** GET LIST OF CASHIERS */
    @Query("SELECT * FROM cashiers_table")
    suspend fun getCashierList(): List<Cashiers>


    /** This Function Will Clear Data Version Table*/
    @Query("DELETE FROM versions")
    suspend fun resetDataVersionTable()

    /** This Function Will Clear Category Table*/
    @Query("DELETE FROM Categories_table")
    suspend fun resetCategoryTable()

    /** This Function Will Clear SubCategory Table*/
    @Query("DELETE FROM SubCategory_table")
    suspend fun resetSubCategoryTable()

    /** This Function Will Clear Items Table*/
    @Query("DELETE FROM Items_table")
    suspend fun resetItemsTable()

    /** This Function Will Clear Invoice Layout Table*/
    @Query("DELETE FROM InvoiceData_table")
    suspend fun resetInvoiceLayoutTable()

    /** This Function Will Clear MainConfiguration Table*/
    @Query("DELETE FROM MainConfig_table")
    suspend fun resetMainConfigurationTable()

    /** A function this save List of roles locally */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRolesList(cashiers: List<PosRole>)

    /** GET LIST OF ROLES */
    @Query("SELECT * FROM Roles_table")
    suspend fun getRolesList(): List<PosRole>

    /** UPDATE USER LIST */
    @Update
    suspend fun updateUsers(users: List<Cashiers>)

    /** UPDATE ONE LIST */
    @Update
    suspend fun updateOneUser(user: Cashiers)

    /** This function should get the password of the user that had been passed as a parameter */
    @Query("SELECT userPassword FROM user_table WHERE userId = :userId")
    suspend fun getUserPasswordWithId(userId: String): String?

    /** This Function Will Clear Transaction Table*/
    @Query("DELETE FROM transactions")
    suspend fun resetTransactionTable()

    /** This Function Will Clear TransactionItems Table*/
    @Query("DELETE FROM transactionItems")
    suspend fun resetTransactionItemsTable()

    /** This Function Will Clear PaymentType Table*/
    @Query("DELETE FROM transactionPayments")
    suspend fun resetTransactionPaymentTypeTable()

    /** This Function Will Clear QuickAccess Table*/
    @Query("DELETE FROM QuickItem_table")
    suspend fun resetQuickAccessTable()

    /** This Function Will Clear ContactUs Table*/
    @Query("DELETE FROM Contact_table")
    suspend fun resetContactUsTable()

    /** This Function Will Clear Cashiers Table*/
    @Query("DELETE FROM Cashiers_table")
    suspend fun resetCashiersTable()

    /** This Function Will Delete User From Cashiers Table*/
    @Query("DELETE  FROM Cashiers_table WHERE userId = :userID")
    suspend fun deleteUser(userID: String)

    @Query("UPDATE user_table SET userPassword = :newPassword WHERE userId = :userId")
    suspend fun updateUserPassword(userId: String, newPassword: String)

}