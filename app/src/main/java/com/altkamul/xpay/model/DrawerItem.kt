package com.altkamul.xpay.model

import com.altkamul.xpay.sealed.Screen

data class DrawerItem(
    val id: Int,
    val screen: Screen,
    val title: String,
)