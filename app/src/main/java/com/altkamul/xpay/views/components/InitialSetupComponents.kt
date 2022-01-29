package com.altkamul.xpay.views.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.altkamul.xpay.R
import com.altkamul.xpay.ui.theme.Dimension
import com.altkamul.xpay.ui.theme.FontSize
import com.altkamul.xpay.ui.theme.lightGray
import com.altkamul.xpay.utils.Constants
import com.altkamul.xpay.viewmodel.InitialSetupViewModel

@Composable
fun ScanQRCodeScreen(
    initialSetupViewModel: InitialSetupViewModel,
    closeScanQRCodeScreen: (scanResult: String) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.82f))
            .padding(Dimension.pagePadding)
    ) {
        /** This Variable Case its True Showing Camera If its False Showing Terminal Text Field*/
        var showingFirstTab by remember {
            mutableStateOf(true)
        }

        /** Title Text*/
        val title =
            if (showingFirstTab) stringResource(id = R.string.scan_qr_code)
            else stringResource(R.string.use_terminal_id)

        /** Sub Title Text*/
        val subTitle =
            if (showingFirstTab) stringResource(id = R.string.point_your_camera)
            else stringResource(R.string.enter_provided_terminal_id)
        Icon(
            imageVector = Icons.Filled.Cancel,
            contentDescription = "cancel icon",
            tint = MaterialTheme.colors.background,
            modifier = Modifier
                .clickable {
                    closeScanQRCodeScreen("")
                }
                .padding(bottom = Dimension.xxl)
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimension.xs.div(2)),
            text = title,
            style = MaterialTheme.typography.h2.copy(color = MaterialTheme.colors.background),
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimension.pagePadding.times(2)),
            text = subTitle,
            style = MaterialTheme.typography.h6.copy(
                color = MaterialTheme.colors.background.copy(
                    alpha = 0.7f
                )
            ),
            textAlign = TextAlign.Center,
        )
        /** Show QR Camera*/
        if (showingFirstTab)
            FirstTab(initialSetupViewModel) {
                closeScanQRCodeScreen(it)
            }

        /** Show Text Field*/
        if (!showingFirstTab)
            SecondTab()

        /** Button Tab To Navigate Between Camera And TextField*/
        ButtonTab(showingFirstTab) {
            showingFirstTab = it
        }
    }
}

@Composable
fun FirstTab(
    initialSetupViewModel: InitialSetupViewModel,
    ScanQRCodeResult: (qrCodeResult: String) -> Unit
) {
    val scannerResult = initialSetupViewModel.setupQRCodeConfiguration(LocalContext.current)
    val barCodeResult by scannerResult.barCodeScanResult.observeAsState()
    AndroidView(modifier = Modifier
        .fillMaxWidth()
        .height(300.dp),
        factory = {
            scannerResult.view
        })

    barCodeResult?.let {
        if (it.isNotBlank()) {
            if (it.length >= Constants.MIN_TerminalID_LENGTH) {
                ScanQRCodeResult(it)
            }
        }
    }
}

@Composable
fun SecondTab() {
    /** Text Field Variable*/
    var text by remember {
        mutableStateOf("")
    }
    CustomInputField(
        backgroundColor = MaterialTheme.colors.background,
        textColor = MaterialTheme.colors.onBackground,
        value = text,
        placeholder = stringResource(id = R.string.enter_terminal_id),
        keyboardType = KeyboardType.Number,
        onValueChange = {
            text = it
        }
    )
}

@Composable
fun ButtonTab(showingFirstTab: Boolean, onTabClick: (showingFirstTab: Boolean) -> Unit) {
    Row(
        Modifier
            .padding(top = Dimension.md)
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colors.surface).padding(Dimension.xs.div(4))
    ) {
        /** Scan Qr Code Button*/
        Button(
            onClick = {
                onTabClick(true)
            },
            modifier = Modifier
                .weight(0.5f)
                .height(TextFieldDefaults.MinHeight),
            elevation = ButtonDefaults.elevation(defaultElevation = if (!showingFirstTab) Dimension.zero else Dimension.md),
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface)
        ) {
            Text(
                text = stringResource(R.string.scan_qr_code),
                style = MaterialTheme.typography.h6.copy(fontSize = FontSize.smX),
                color = if (!showingFirstTab) MaterialTheme.colors.secondaryVariant
                else MaterialTheme.colors.primary,
                textAlign = TextAlign.Center
            )
        }

        /** Use Terminal ID Button*/
        Button(
            onClick = {
                onTabClick(false)
            },
            modifier = Modifier
                .weight(0.5f)
                .height(TextFieldDefaults.MinHeight),
            elevation = ButtonDefaults.elevation(defaultElevation = if (showingFirstTab) Dimension.zero else Dimension.md),
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface)
        ) {
            Text(
                text = stringResource(R.string.use_terminal_id),
                style = MaterialTheme.typography.h6.copy(fontSize = FontSize.smX),
                color = if (showingFirstTab) MaterialTheme.colors.secondaryVariant
                else MaterialTheme.colors.primary,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
fun ColumnScope.ButtonWithTextField(
    onClickTheButton: (terminalID: String) -> Unit,
    onClickScanQRCodeButton: () -> Unit,
) {
    Box(modifier = Modifier.weight(0.4f)) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimension.sm)) {
            /** Getting Terminal ID From TextField*/
            var terminalID by remember { mutableStateOf("") }
            Text(
                text = stringResource(R.string.initial_setup),
                style = MaterialTheme.typography.body1.copy(
                    color = MaterialTheme.colors.secondaryVariant,
                )
            )
            var isError by remember {
                mutableStateOf(false)
            }
            CustomInputField(
                backgroundColor = lightGray.copy(alpha = 0.3f),
                value = terminalID,
                placeholder = stringResource(R.string.enter_terminal_id),
                keyboardType = KeyboardType.Number, isError = isError,
                onValueChange = {
                    terminalID = it
                    isError = it.isEmpty() || it.length > 6
                }
            )


            CustomButton(
                buttonColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary,
                text = stringResource(R.string.submit),
                onButtonClicked = {
                    onClickTheButton(terminalID)
                },
                elevationEnabled = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier
                    .clickable {
                        onClickScanQRCodeButton()
                    }
                    .height(TextFieldDefaults.MinHeight)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Filled.QrCodeScanner,
                    contentDescription = "QR Code Icon",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .size(Dimension.smIconSize)
                )
                Spacer(modifier = Modifier.width(Dimension.xs))
                Text(
                    text = stringResource(R.string.scan_qr_code),
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.button,
                )
            }
        }
    }
}


@Composable
fun ColumnScope.BunchOfText() {
    Box(modifier = Modifier.weight(0.3f), contentAlignment = Alignment.Center) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimension.xs)) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Welcome to",
                style = MaterialTheme.typography.body2.copy(
                    color = MaterialTheme.colors.primary,
                    fontSize = FontSize.md
                )
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Smart E-Pay",
                style = MaterialTheme.typography.h2.copy(
                    color = MaterialTheme.colors.secondaryVariant
                )
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Your number 1 business partner",
                style = MaterialTheme.typography.h6.copy(
                    color = MaterialTheme.colors.secondaryVariant.copy(
                        0.5f
                    )
                )
            )
        }
    }
}


