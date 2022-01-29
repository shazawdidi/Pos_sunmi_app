package com.altkamul.xpay.utils

import androidx.compose.ui.unit.dp
import com.altkamul.xpay.model.*

object LoggedMerchantPref {
    /**
     * The user who is logged in now - whatever the role of that user
     * Saving the user this way can help us avoid the possibility of different types of a logged user in the project
     */
    var merchant: Merchant? = null
    var branch: Branch? = null
    val address: Address?
        get() = branch?.address
    var user: User? = null
    var configuration: LocalMainConfiguration? = null
    var pos: String? = null
    val token: String?
        get() = user?.token
    var lang: String? = null
    var terminalId: String? = null
    var deviceSize: Size = Size(0.dp, 0.dp)
}