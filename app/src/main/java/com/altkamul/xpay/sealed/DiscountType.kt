package com.altkamul.xpay.sealed

import com.altkamul.xpay.R

sealed class DiscountType(val id: Int){
    /** Doesn't have an actual value */
    object None : DiscountType(id = 0)
    object Both : DiscountType(id= 3)

    /** This have an actual value which is determined by the cashier */
    class ByPercent(val name: Int = R.string.by_percent) : DiscountType(id = 2)
    class ByValue(val name: Int = R.string.by_value) : DiscountType(id = 1)
}
