package com.altkamul.xpay.model

import android.os.Parcelable
import com.altkamul.xpay.sealed.DiscountType
import kotlinx.parcelize.RawValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@kotlinx.parcelize.Parcelize
data class CartItem(
    @SerialName("itemId") val itemId: Int,
    @SerialName("qty") var qty: Int,
    @SerialName("price") var totalPrice: Double,
    @SerialName("discount") var discountValue: Double,
    @Transient var realPrice: Double = 0.0,
    @Transient var maxDiscount: Double = 0.0,
    @Transient var discountType: @RawValue DiscountType = DiscountType.ByValue(),
) : Parcelable
