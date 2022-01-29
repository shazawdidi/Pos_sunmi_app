package com.altkamul.xpay.views.screens

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.altkamul.xpay.R
import com.altkamul.xpay.sealed.Screen
import com.altkamul.xpay.ui.theme.Dimension
import com.altkamul.xpay.ui.theme.lightShadowOfGray
import com.altkamul.xpay.viewmodel.LoadingViewModel
import com.altkamul.xpay.viewmodel.ParentViewModel


/**
 * Loading Screen
 * Complete Recall Operation
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoadingScreen(
    loadingViewModel: LoadingViewModel = hiltViewModel(),
    navController: NavController
) {
    /** This Indicator For Progress Bar*/
    val loadingProgressIndicator by remember {
        loadingViewModel.loadingProgressIndicator
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimension.pagePadding)
            .background(MaterialTheme.colors.background)
    ) {
        val downloadProcessText = loadingViewModel.downloadProgressText
        val downloadProgressIsCompleteColor = loadingViewModel.downloadProgressIsComplete
        Box(
            modifier = Modifier
                .weight(0.85f)
                .fillMaxWidth(), contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.Center
            ) {
                itemsIndexed(downloadProcessText) { index, item ->
                    Row(
                        modifier = Modifier.padding(top = Dimension.xxl),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.check),
                            modifier = Modifier
                                .size(Dimension.smIconSize)
                                .padding(end = Dimension.xs),
                            contentDescription = "loading icon",
                            tint = if (downloadProgressIsCompleteColor[index])
                                MaterialTheme.colors.secondary
                            else
                                MaterialTheme.colors.secondaryVariant
                        )
                        Text(
                            text = "${index + 1}/${downloadProcessText.size} $item",
                            style = MaterialTheme.typography.h6.copy(color = MaterialTheme.colors.secondaryVariant)
                        )
                    }
                }
            }
        }
        val parentViewModel: ParentViewModel =
            hiltViewModel(LocalContext.current as ComponentActivity)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.15f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            LinearProgressIndicator(
                progress = loadingProgressIndicator,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimension.md)
                    .height(Dimension.xs)
                    .clip(MaterialTheme.shapes.small),
                color = MaterialTheme.colors.primary,
                backgroundColor = lightShadowOfGray
            )
            Text(
                text = stringResource(R.string.loading_data),
                style = MaterialTheme.typography.h5.copy(color = MaterialTheme.colors.primary)
            )


            if (loadingProgressIndicator == 1.0f)
                LaunchedEffect(key1 = true) {
                    parentViewModel.lunchBackgroundTask()
                    parentViewModel.downloadBranchImages()
                    navController.popBackStack()
                    navController.navigate(Screen.QuickAccess.route)
                }
        }

    }
}

