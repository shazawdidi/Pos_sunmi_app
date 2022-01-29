package com.altkamul.xpay.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import timber.log.Timber
import java.util.*

object LocaleHelper {
    fun applyLanguage(context: Context,language: String){
        Timber.d("Applying the new language $language")
        val locale = Locale(language)
        val res: Resources = context.resources
        val conf: Configuration = res.configuration
        Locale.setDefault(locale)
        conf.setLocale(locale)
        res.updateConfiguration(conf, null)
    }
}