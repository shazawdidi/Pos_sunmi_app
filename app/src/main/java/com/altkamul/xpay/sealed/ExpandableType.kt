package com.altkamul.xpay.sealed

sealed class ExpandableType{
    object Category: ExpandableType()
    object SubCategory: ExpandableType()
}
