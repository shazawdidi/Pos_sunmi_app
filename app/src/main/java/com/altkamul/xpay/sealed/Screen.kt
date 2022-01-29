package com.altkamul.xpay.sealed

import com.altkamul.xpay.R

sealed class Screen(val route: String,val icon: Int = -1){
    object Splash : Screen(route = "splash")
    object InitialSetup : Screen(route = "initial-setup")
    object Loading : Screen(route = "loading")
    object Login : Screen(route = "login",)
    object QuickAccess : Screen(route = "quick-access")
    object Home : Screen(route = "home",icon = R.drawable.ic_home)
    object Cart : Screen(route = "cart",icon = R.drawable.shopping_cart)
    object Reprint : Screen(route = "reprint",icon = R.drawable.ic_printer)
    object Claim : Screen(route = "claim",icon = R.drawable.ic_claim)
    object TransactionHistory : Screen(route = "transaction-history",icon = R.drawable.ic_history)
    object Reports : Screen(route = "reports",icon = R.drawable.ic_report)
    object Settings : Screen(route = "settings",icon = R.drawable.ic_settings)
    object Logout : Screen(route = "logout",icon = R.drawable.ic_exit)
    object Accounts : Screen(route = "accounts",icon = R.drawable.ic_accounts)
    object ChangePassword : Screen(route = "change-password",icon = R.drawable.ic_change_password)
    object Languages : Screen(route = "languages",icon = R.drawable.ic_change_lang)
    object ChangeBranch : Screen(route = "change-branch")
    object ContactUs : Screen(route = "contact-us",icon = R.drawable.ic_contact_us)
    object TestCenter : Screen(route = "test-center",icon = R.drawable.ic_test_center)

    /** Test Center sub-screens */
    object NFCTest : Screen(route = "nfc-test")
    object QrMakerTest : Screen(route = "qr-reader-test")
    object QrReaderTest : Screen(route = "qr-maker-test")
    object InternetTest : Screen(route = "internet-test")

}
