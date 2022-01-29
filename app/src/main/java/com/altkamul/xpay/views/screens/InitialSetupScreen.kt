package com.altkamul.xpay.views.screens

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.altkamul.xpay.R
import com.altkamul.xpay.sealed.Screen
import com.altkamul.xpay.ui.theme.Dimension
import com.altkamul.xpay.viewmodel.InitialSetupViewModel
import com.altkamul.xpay.viewmodel.ParentViewModel
import com.altkamul.xpay.views.components.BunchOfText
import com.altkamul.xpay.views.components.ButtonWithTextField
import com.altkamul.xpay.views.components.ScanQRCodeScreen
import com.altkamul.xpay.views.components.ShowDialog

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InitialSetupScreen(
    navController: NavHostController,
    initialSetupViewModel: InitialSetupViewModel = hiltViewModel(),
    parentViewModel: ParentViewModel =
        hiltViewModel(LocalContext.current as ComponentActivity)
) {

    /** Showing Dialog In User Screen While Loading Data*/
    val isLoading by remember { initialSetupViewModel.isLoading }

    /** This Variable For Handling QR Button Clicked*/
    var showScanQRCodeScreen by remember { mutableStateOf(false) }

    /** Parent Layout Always Box :)*/
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            Modifier
                .fillMaxSize().padding(Dimension.pagePadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimension.md)
        ) {
            /** Showing Dialog Progress While Downloading the Data*/
            if (isLoading)
                ShowDialog()

            /** The Business Logo*/
            Image(
                modifier = Modifier.weight(0.3f),
                painter = painterResource(id = R.drawable.applogo2),
                contentDescription = "image",
            )

            /** Bunch Of Text like Welcome and bla bla bla*/
            BunchOfText()

            /** Button For Complete The Operation and Text Field For getting Terminal ID*/
            ButtonWithTextField(onClickTheButton = { terminalID ->
                /** Complete The Process After Terminal ID*/
                initialSetupViewModel.getMerchantInfo(terminalID = terminalID) {
                    navController.popBackStack()
                    navController.navigate(Screen.Login.route)
                }
            }, onClickScanQRCodeButton = {
                showScanQRCodeScreen = true
            })
        }

        /** If The User Wont To Use Camera For Scan The Terminal ID*/
        if (showScanQRCodeScreen) {
            ScanQRCodeScreen(initialSetupViewModel) { scanResult ->
                showScanQRCodeScreen = false
                /** Complete The Process After Getting Scan QR Code Result*/
                initialSetupViewModel.getMerchantInfo(terminalID = scanResult) {
                    navController.popBackStack()
                    navController.navigate(Screen.Login.route)
                }
            }
        }
    }
}