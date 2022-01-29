package com.altkamul.xpay

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.altkamul.printer.TkamulPrinterBase
import com.altkamul.xpay.sealed.Language
import com.altkamul.xpay.ui.theme.XPayAndroidTheme
import com.altkamul.xpay.utils.LocaleHelper
import com.altkamul.xpay.utils.LoggedMerchantPref
import com.altkamul.xpay.utils.getScreenSize
import com.altkamul.xpay.viewmodel.ParentViewModel
import com.altkamul.xpay.views.screens.ScreenHolder
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
        // hiding the default status bar
        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        super.onCreate(savedInstanceState)

        setContent {
            LoggedMerchantPref.deviceSize = getScreenSize()
            Timber.d("Device screen size is ${getScreenSize()} and printer type is ${TkamulPrinterBase.getPrinterType()}")
            val parentViewModel: ParentViewModel = viewModel(this)

            /** App language handling */
            val currentLanguageAsState = parentViewModel.currentLanguage.collectAsState("en")
            val currentLanguage = remember { currentLanguageAsState }
            /** Update pref whenever there are a new current language */
            LoggedMerchantPref.lang = currentLanguage.value
            /** Changing app's locally dynamically */
            LocaleHelper.applyLanguage(this@MainActivity, currentLanguage.value)
            val showRTL = currentLanguage.value in listOf(Language.Arabic.code, Language.Urdu.code)

            val primaryLocale = resources.configuration.locales[0].displayName
            Timber.d("current locale is $primaryLocale")

            XPayAndroidTheme {
                CompositionLocalProvider(LocalLayoutDirection provides if (showRTL) LayoutDirection.Rtl else LayoutDirection.Ltr) {
                    // A surface container using the 'background' color from the theme
                    Surface(color = MaterialTheme.colors.background) {
                        // the app should always start from the screen holder which control the app's navigation
                        ScreenHolder()
                    }
                }
            }
        }
    }


}