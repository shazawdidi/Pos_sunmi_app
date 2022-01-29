package com.altkamul.xpay.views.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.altkamul.xpay.R
import com.altkamul.xpay.ui.theme.Dimension
import com.altkamul.xpay.utils.LoggedMerchantPref
import com.altkamul.xpay.viewmodel.QRMakerTestViewModel

@Composable
fun QRMakerTestScreen(
    qrMakerTestViewModel: QRMakerTestViewModel = hiltViewModel(),
) {
    val qr by qrMakerTestViewModel.qr.observeAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(Dimension.pagePadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        val text = LoggedMerchantPref.merchant?.merchantId ?: "fake-merchant-id-343734"
        LaunchedEffect(key1 = true){
            qrMakerTestViewModel.createQr(text = text)
        }

        Box{
            /** Page title */
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.qr_maker_test),
                style = MaterialTheme.typography.h2.copy(color = MaterialTheme.colors.secondaryVariant)
            )
        }

        Box(
            modifier = Modifier
                .size(300.dp),
            contentAlignment = Alignment.Center
        ){
            qr?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                )
            } ?: CircularProgressIndicator(
                Modifier
                    .size(Dimension.lgIconSize),
            )
        }

        Text(
            text = "This QR is generated for the text:\n$text",
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "You can scan this qr code to see if they are matched.",
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Center,
        )

    }
}