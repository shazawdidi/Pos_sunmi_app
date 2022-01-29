package com.altkamul.xpay.sealed

import androidx.annotation.StringRes
import com.altkamul.xpay.R

sealed class CashiersTypes(
    val id: Int,
    @StringRes val title: Int,

){
    object Supervisor : CashiersTypes(id = 1,title = R.string.supervisor )
    object Cashier: CashiersTypes(id = 2,title = R.string.cashier )
    object Worker: CashiersTypes(id = 3,title = R.string.worker )

}