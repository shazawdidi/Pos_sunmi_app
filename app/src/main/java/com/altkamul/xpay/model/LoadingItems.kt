package com.altkamul.xpay.model

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.MutableLiveData
import com.altkamul.xpay.ui.theme.iconColor

data class LoadingItems(
    val title: String,
    var currentColor: MutableLiveData<Color> = MutableLiveData(iconColor)
)
