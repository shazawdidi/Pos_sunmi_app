package com.altkamul.xpay.utils

import android.content.Context
import android.widget.Toast

object Common {
    fun createToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

}