package com.altkamul.printer.core

import android.util.Log
import com.altkamul.printer.BuildConfig
import timber.log.Timber
import timber.log.Timber.DebugTree


object Logger {
    init {
        if (BuildConfig.DEBUG && Timber.treeCount()==0)
            Timber.plant(object : DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String {
                    return   element.className + ": " +  element.methodName + ": " + element.lineNumber
                }
            })
    }
    @JvmStatic
     inline fun logd(message : Any?){
        Timber.d(message?.toString()?:"null")
        Log.d("XPay" , message?.toString()?:"null")
        // todo add to logger remote queue
    }
    @JvmStatic
    inline fun loge(e: Throwable?) {
        Timber.e(e)
    }
    @JvmStatic
    fun loge(message: Any?) {
        Timber.e(message?.toString()?:"null")
    }


}