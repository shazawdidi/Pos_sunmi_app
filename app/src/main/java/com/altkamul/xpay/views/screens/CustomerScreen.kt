package com.altkamul.xpay.views.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.altkamul.xpay.R
import com.altkamul.xpay.sealed.Screen
import com.altkamul.xpay.ui.theme.Dimension
import com.altkamul.xpay.ui.theme.FontSize
import com.altkamul.xpay.utils.Common
import com.altkamul.xpay.viewmodel.HomeScreenViewModel
import com.altkamul.xpay.views.components.CategoryAndSubCategoryAndItemsLayout

@Composable
fun CustomerScreen(
    navController: NavController,
    searchState: String,
    homeScreenViewModel: HomeScreenViewModel =
        hiltViewModel()
) {
    LaunchedEffect(key1 = searchState) {
        homeScreenViewModel.filteringBaseOnSearchField(filterBY = searchState)
    }
    Column(
        Modifier
            .fillMaxSize()
    ) {
        LaunchedEffect(key1 = true) {
            homeScreenViewModel.getCategoryAndSubCategoryAndItem()
        }
        /** This Function Will Draw Home Content*/
        HomeContent(homeScreenViewModel) {
            navController.navigate(Screen.QuickAccess.route) {
                this.popUpTo(Screen.Home.route) {
                    this.saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
        /** This Function Will Draw Home Bottom Bar*/
        HomeBottomBar(homeScreenViewModel, navController)
    }
}
