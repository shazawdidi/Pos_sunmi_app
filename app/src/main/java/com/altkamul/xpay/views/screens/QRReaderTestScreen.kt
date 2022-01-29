package com.altkamul.xpay.views.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.QrCode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.altkamul.xpay.R
import com.altkamul.xpay.ui.theme.Dimension
import com.altkamul.xpay.utils.Common
import com.altkamul.xpay.viewmodel.QRReaderTestViewModel

@Composable
fun QRReaderTestScreen(
    qrReaderTestViewModel: QRReaderTestViewModel = hiltViewModel(),
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        val startQrReader by remember { qrReaderTestViewModel.showQrReader }
        val qrText by remember { qrReaderTestViewModel.qrText }
        val context = LocalContext.current
        if (startQrReader) {
            QrReader(
                testCenterViewModel = qrReaderTestViewModel,
                context = context,
                onQrReadComplete = {
                    /** Now hide the QR reader */
                    qrReaderTestViewModel.updateQrReaderState()
                    qrReaderTestViewModel.updateQrText(text = it)
                },
            )
        } else {
            if(qrText.isNotEmpty()){
                Column(
                    modifier = Modifier
                        .padding(Dimension.pagePadding)
                ){
                    Text(
                        text = stringResource(R.string.qr_reader_test),
                        style = MaterialTheme.typography.h2,
                        color = MaterialTheme.colors.secondaryVariant,
                    )
                    Column (
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ){
                        Image(
                            imageVector = Icons.Rounded.QrCode,
                            contentDescription = null,
                            modifier = Modifier.size(128.dp),
                            colorFilter = ColorFilter.tint(MaterialTheme.colors.secondaryVariant)
                        )
                        Spacer(modifier = Modifier.height(Dimension.pagePadding))
                        Text(
                            text = "The QR code that you scanned is",
                            style = MaterialTheme.typography.body1,
                        )
                        Spacer(modifier = Modifier.height(Dimension.pagePadding))
                        Text(
                            text = qrText,
                            style = MaterialTheme.typography.body2,
                            color = MaterialTheme.colors.secondaryVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QrReader(
    testCenterViewModel: QRReaderTestViewModel,
    context: Context,
    onQrReadComplete: (text: String) -> Unit,
) {
    testCenterViewModel.setupQRCodeConfiguration(context = context).let {scannerResult->
        val barCodeResult by scannerResult.barCodeScanResult.observeAsState()
        barCodeResult?.let {
            if (it.isNotBlank()) {
                onQrReadComplete(it)
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.onBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AndroidView(modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .aspectRatio(1f),
                    factory = {
                        scannerResult.view
                    }
                )
                Spacer(modifier = Modifier.height(Dimension.pagePadding))
                Text(
                    text = stringResource(id = R.string.point_your_camera),
                    style = MaterialTheme.typography.body2,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}