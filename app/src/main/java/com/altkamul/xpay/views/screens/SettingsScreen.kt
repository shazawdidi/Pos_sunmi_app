package com.altkamul.xpay.views.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.altkamul.xpay.R
import com.altkamul.xpay.model.DrawerItem
import com.altkamul.xpay.sealed.Screen
import com.altkamul.xpay.ui.theme.Dimension
import com.altkamul.xpay.views.components.AppVersion
import com.altkamul.xpay.views.components.DrawItems

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingsScreen(
    navController: NavHostController
) {

    val commonSettingsDestinations = listOf(
        DrawerItem(
            id = 1, screen = Screen.Accounts, stringResource(id = R.string.account)
        ),
        DrawerItem(
            id = 2, screen = Screen.ChangePassword, stringResource(id = R.string.change_password)
        ),
        DrawerItem(
            id = 3, screen = Screen.TestCenter, stringResource(id = R.string.test_center)
        )
    )

    val secondarySettingDestinations = listOf(
        DrawerItem(id = 1, screen = Screen.Languages, stringResource(id = R.string.language)),
        DrawerItem(id = 2, screen = Screen.ContactUs, stringResource(id = R.string.contact_us))
    )
    val currentRoute = getActiveRoute(navController = navController)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimension.pagePadding),
        verticalArrangement = Arrangement.spacedBy(Dimension.md)
    ) {

        Text(
            text = stringResource(R.string.settings),
            style = MaterialTheme.typography.h1,
            color = MaterialTheme.colors.secondaryVariant,
        )


        DrawItems(
            contentText = stringResource(R.string.common),
            navController = navController,
            currentRoute = currentRoute,
            destinations = commonSettingsDestinations
        )


        DrawItems(
            contentText = stringResource(R.string.secondary),
            navController = navController,
            currentRoute = currentRoute,
            destinations = secondarySettingDestinations
        )

        AppVersion()
    }

}





