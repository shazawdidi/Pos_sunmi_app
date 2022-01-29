package com.altkamul.xpay.application

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.altkamul.xpay.BuildConfig
import com.altkamul.xpay.utils.LocaleHelper
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject


@HiltAndroidApp
class XPayApp : Application(), Configuration.Provider {
    @Inject lateinit var workFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }
    }
    override fun getWorkManagerConfiguration() = Configuration
        .Builder()
        .setWorkerFactory(workFactory)
        .build()
}