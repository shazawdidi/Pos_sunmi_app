package com.altkamul.xpay.sealed

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.altkamul.xpay.R

sealed class Language(
    val code: String,
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
){
    object Arabic: Language(code = "ar",title = R.string.arabic,icon = R.drawable.ic_uae)
    object English: Language(code = "en",title = R.string.english,icon = R.drawable.ic_usa)
    object French: Language(code = "fr",title = R.string.french,icon = R.drawable.ic_france)
    object Urdu: Language(code = "ur",title = R.string.urdu,icon = R.drawable.ic_pakistan)
    object Turkish: Language(code = "tr",title = R.string.turkish,icon = R.drawable.ic_turkey)
}