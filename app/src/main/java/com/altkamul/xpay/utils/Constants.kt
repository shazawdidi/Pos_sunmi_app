package com.altkamul.xpay.utils

object Constants {
    const val BASE_URL = "http://213.159.5.155:887/api/"
    const val MERCHANT_TEAM_LOGIN_URL = "users/Login"
    const val LOGIN_WITH_ENCRYPTED_PASSWORD = "users/CashierLogin"
    const val SUPPORT_LOGIN_URL = "users/supportLogin"
    const val GET_ALL_CASHIERS = "users/GetAllCashiers"
    const val GET_ALL_ROLES = "users/GetAllRoles"
    const val SYNC_CATEGORIES_URL = "Items/GetAllSyncCategories"
    const val SYNC_SUBCATEGORIES_URL = "Items/GetAllSyncSubCategories"
    const val SYNC_ITEMS_URL = "Items/GetAllSyncItems"

    // ----------------------------------- POS----------------------------------/
    const val DATA_VERSION_URL = "POS/GetDataVersion"
    const val MAIN_CONFIG_URL = "POS/GetMainConfiguration"
    const val LAYOUT_INVOICE_URL = "POS/GetLayout/F3731DEC-9D96-42DC-9AB1-75C99DD2CA7E"

    //----------------------------------- ITEMS---------------------------------/
    const val ALL_CATEGORIES_URL = "Items/GetAllCategories"
    const val ALL_SUBCATEGORIES_URL = "Items/GetAllSubCategories"
    const val ALL_ITEMS_URL = "Items/GetAllItems"

    const val BasicInfo_Url = "POS/GetBasicInfo"
    const val ContactUs_URL = "POS/ContactUs"
    const val MIN_TerminalID_LENGTH = 6

    const val MIN_PASSWORD_LENGTH = 4
//    const val MIN_PASSWORD_LENGTH = 6 to be used after production phase finished , because testing account had only 4 digits !

    const val CHECKOUT_URL = "Transactions/Checkout"
    const val GET_TRANSACTION = "Transactions/TransactionOperation"
    const val GET_LAST_TRANSACTION = "Transactions/LastTransaction"
    const val CLAIM_TRANSACTION_BY_ID = "Transactions/Claim/"
    const val GET_TRANSACTION_HISTORY = "Transactions/TransactionHistory"
    const val CHANGE_PASSWORD = "Users/ChangeCashierPassword"
    const val ADD_USER = "Users/AddCashier"
    const val EDIT_USER = "Users/EditCashier"
    const val DELETE_USER = "Users/DeleteCashier"

    /** Screen size ranges */
    val smallDevicesRange = IntRange(300, 600)
    val largeDevicesRange = IntRange(900, 1366)

    const val APP_VERSION = "Smart E-Pay v. 2.0.0"
}