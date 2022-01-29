package com.altkamul.xpay.views.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.altkamul.xpay.R
import com.altkamul.xpay.ui.theme.Dimension
import com.altkamul.xpay.ui.theme.FontSize
import com.altkamul.xpay.ui.theme.lightGray
import com.altkamul.xpay.viewmodel.ContactUsViewModel
import com.altkamul.xpay.views.components.TiltedEmailPhone

@Composable
fun ContactUsScreen(
    contactUsViewModel: ContactUsViewModel = hiltViewModel()
) {
    val data by contactUsViewModel.contactUsData.observeAsState()
    Column(
        Modifier
            .fillMaxSize()
            .padding(Dimension.pagePadding),
        verticalArrangement = Arrangement.spacedBy(Dimension.md)
    ) {
        Text(
            text = stringResource(R.string.contact_us),
            style = MaterialTheme.typography.h1,
            color = MaterialTheme.colors.secondaryVariant
        )
        Text(
            text = stringResource(R.string.contact_us_slug),
            lineHeight = FontSize.xl,
            style = MaterialTheme.typography.h6,
            color = lightGray,
            maxLines = 2
        )
        TiltedEmailPhone(
            email = data?.supportEmail ?: "No Data",
            phone = data?.supportPhone ?: "No Data",
            text = stringResource(R.string.support_team)
        )
        TiltedEmailPhone(
            email = data?.salesEmail ?: "No Data",
            phone = data?.salesPhone ?: "No Data",
            text = stringResource(R.string.sales_team)
        )
    }
}