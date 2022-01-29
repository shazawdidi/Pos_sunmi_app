package com.altkamul.xpay.views.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.altkamul.xpay.R
import com.altkamul.xpay.sealed.Screen
import com.altkamul.xpay.viewmodel.SplashViewModel


/**
 * Splash Screen
 * display lotti file in app Resource
 */
@Composable
fun SplashScreen(navController: NavController, splashViewModel: SplashViewModel = hiltViewModel()) {
    val terminalId by splashViewModel.terminalID.collectAsState(initial = "")
    if (terminalId.isNotEmpty())
        LaunchedEffect(key1 = true) {
            splashViewModel.checkIfRoomIsEmpty(terminalId)
        }
    val isLocalRoomWasEmpty by remember {
        splashViewModel.checkLocalRoom
    }
    Box(
        Modifier
            .fillMaxSize().background(MaterialTheme.colors.surface),
        contentAlignment = Alignment.Center,
    ) {

        // Lotti file location specified
        val composition = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.logo))

        // Add extra properties to lottie file object
        val progress = animateLottieCompositionAsState(composition.value)

        // Lotti file object

        LottieAnimation(
            composition.value,
            progress.value
        )

        // Navigate to next screen after lotti file complete

        if (progress.progress == 1.0f) {
            LaunchedEffect(key1 = true) {
                navController.popBackStack()
                if (isLocalRoomWasEmpty) {
                    navController.navigate(Screen.InitialSetup.route)
                } else {
                    navController.navigate(Screen.Login.route)
                }
            }
        }

    }
}