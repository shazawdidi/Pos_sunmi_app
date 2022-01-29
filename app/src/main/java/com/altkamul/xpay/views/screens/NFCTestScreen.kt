package com.altkamul.xpay.views.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.altkamul.xpay.ui.theme.Dimension
import com.altkamul.xpay.viewmodel.NFCTestViewModel

@Composable
fun NFCTestScreen(
    nfcTestViewModel: NFCTestViewModel = hiltViewModel(),
) {
    val isNFCAvailable by remember { nfcTestViewModel.isNFCAvailable }
    val isNFCEnabled  by remember { nfcTestViewModel.isNFCEnabled }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimension.pagePadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "NFC is available ? $isNFCAvailable\nNFC is enabled? $isNFCEnabled")
    }
}