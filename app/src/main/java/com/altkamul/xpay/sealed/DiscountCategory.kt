package com.altkamul.xpay.sealed

import androidx.annotation.StringRes
import com.altkamul.xpay.R

sealed class DiscountCategory(@StringRes var name: Int, var value: Int){
    class Coupon(value: Int = 0): DiscountCategory(name = R.string.coupon, value = value)
    class Phone(value: Int = 0): DiscountCategory(name = R.string.phone, value = value)
    class CarPlate(value: Int = 0): DiscountCategory(name = R.string.car_plate, value = value)
}
