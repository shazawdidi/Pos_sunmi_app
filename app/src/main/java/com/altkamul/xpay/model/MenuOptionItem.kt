package com.altkamul.xpay.model

import androidx.annotation.DrawableRes

data class MenuOptionItem(
    val id: Int,
    @DrawableRes val icon: Int,
    val title: String,
    val route: String? = null,
    val isChecked: Boolean = false
)