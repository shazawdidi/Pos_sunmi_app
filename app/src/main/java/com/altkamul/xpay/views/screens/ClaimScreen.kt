package com.altkamul.xpay.views.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.altkamul.xpay.R
import com.altkamul.xpay.ui.theme.*
import com.altkamul.xpay.utils.Common
import com.altkamul.xpay.utils.Constants
import com.altkamul.xpay.utils.ScreenDimensions
import com.altkamul.xpay.utils.smallerThan
import com.altkamul.xpay.viewmodel.ClaimViewModel
import com.altkamul.xpay.views.components.ShowTransactionItems
import com.altkamul.xpay.views.components.TransactionCardLayout

@Composable
fun ClaimScreen(claimViewModel: ClaimViewModel = hiltViewModel()) {
    val isSmallDevice =
        ScreenDimensions.Width.smallerThan(value = Constants.largeDevicesRange.first.dp)

    /** This Context For Displaying Toast In User Screen*/
    val context = LocalContext.current

    /** This Variable For Observing Transaction If it Was Found*/
    val transaction by claimViewModel.transaction.observeAsState()

    /** Variable For Showing Transaction Items*/
    val showTransactionItems = remember {
        mutableStateOf(false)
    }

    /** This Variable For Save Current Button Text*/
    val buttonText = remember {
        mutableStateOf("Claim The Last Receipt")
    }

    /** Variable For Showing Transaction Card*/
    val findReceiptSuccessfully by remember {
        claimViewModel.foundTransaction
    }
    /** Reassigning Button Text To Claim This Receipt If We Found Transaction Successfully*/
    buttonText.value =
        if (findReceiptSuccessfully) "Claim This Receipt" else "Claim The Last Receipt"

    val isLoading by remember {
        claimViewModel.isLoading
    }

    /** Parent Layout Box Of Course :)*/
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(Dimension.pagePadding)
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.spacedBy(Dimension.sm)
        ) {

            /** The Primary Text*/
            Text(
                text = stringResource(id = R.string.claim_title),
                style = MaterialTheme.typography.h1.copy(color = MaterialTheme.colors.secondaryVariant)
            )

            /** The Secondary Text*/
            Text(
                text = stringResource(id = R.string.claim_sub_title),
                style = MaterialTheme.typography.h6.copy(color = gray)
            )

            /** Text Field Value*/
            var textFieldValue by remember {
                mutableStateOf("")
            }
            /** Text Field For Search*/
            TextField(
                modifier = Modifier
                    .fillMaxWidth(if (isSmallDevice) 1f else 0.5f)
                    .height(TextFieldDefaults.MinHeight), readOnly = showTransactionItems.value,
                value = textFieldValue,
                onValueChange = {
                    textFieldValue = it
                }, trailingIcon = {
                    /** Text Field Search Icon*/
                    Icon(
                        painter = painterResource(id = R.drawable.search_icon),
                        contentDescription = "Search Icon",
                        tint = gray,
                        modifier = Modifier
                            .padding(Dimension.xs)
                            .size(Dimension.lg)
                            .clickable {
                                /** If text Field Value was not Empty and We Found Transaction Successfully*/
                                if (textFieldValue.isNotEmpty()) {
                                    claimViewModel.getTransactionByIDLocally(textFieldValue.toInt())

                                } else
                                /** Showing Toast To User Screen Contain ..*/
                                    Common.createToast(context, "Pleas Enter Valid Receipt Number")
                            }
                    )
                },
                textStyle = TextStyle(color = Color.Black, fontSize = FontSize.md),
                placeholder = {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = Dimension.sm),
                        text = stringResource(id = R.string.claim_search_filed_text),
                        style = MaterialTheme.typography.h6,
                        color = gray
                    )
                },
                singleLine = true,
                shape = MaterialTheme.shapes.small,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = lightShadowOfGray,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            /** If We Find Transaction Successfully We Will Show This Card*/
            if (findReceiptSuccessfully) {
                transaction?.let {
                    TransactionCardLayout(
                        Modifier
                            .fillMaxWidth(if (isSmallDevice) 1f else 0.5f)
                            .padding(top = Dimension.xs)
                            .fillMaxWidth(),
                        date = it.transactionDateTime.split("T").first(),
                        time = it.transactionDateTime.split("T").last().take(5),
                        amount = "${it.totalAmount} ${stringResource(id = R.string.aed_currency)}",
                        payments = it.transactionPayment,
                        selected = true,
                        onClick = {
                            /** This Card Is Clickable OnClick -> show Transaction Item Pop Up Screen*/
                            //                    showTransactionItems.value = !showTransactionItems.value
                        },
                        title = "Receipt No: ${(it.transactionMasterId ?: 0)}"
                    )
                }
            } else
            /** If We Don't Find Transaction Successfully We Will Show This Layout*/
                Column(
                    Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Dimension.sm.div(2))
                ) {
                    /** Image That Contain Recipient Design*/
                    Image(
                        modifier = Modifier
                            .size(200.dp),
                        painter = painterResource(id = R.drawable.receipt_flat_icon),
                        contentDescription = stringResource(id = R.string.no_receipt)
                    )

                    /** The No Receipt Text*/
                    Text(
                        text = stringResource(id = R.string.no_receipt),
                        style = MaterialTheme.typography.h2.copy(color = MaterialTheme.colors.secondaryVariant)
                    )
                    /** The Please enter a receipt number.... Text*/
                    Text(
                        text = stringResource(id = R.string.claim_sub_title2),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.h6.copy(color = gray)
                    )
                }

            /** For Showing Dialog While Loading*/
            if (isLoading)
                ShowDialog()

            /** This Button For Claim The Transaction*/
            Button(
                onClick = {
                    if (buttonText.value == "Claim This Receipt")
                    /** Claim Operation Will Done Here*/
                        claimViewModel.claimCurrentTransaction()
                    else
                        claimViewModel.getLastTransactionLocally()
                },
                modifier = Modifier
                    .padding(top = Dimension.xs)
                    .fillMaxWidth(if (isSmallDevice) 1f else 0.5f)
                    .height(TextFieldDefaults.MinHeight),
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
            ) {
                Text(
                    text = buttonText.value,
                    style = MaterialTheme.typography.h6, color = MaterialTheme.colors.surface
                )
            }
        }

        /** This Condition For Showing Item List*/
        if (showTransactionItems.value)
            ShowTransactionItems(showTransactionItems, claimViewModel)
    }

}

@Composable
fun ShowDialog() {
    Dialog(
        onDismissRequest = { /**/ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        )
    ) {
        /** Loading dialog contents */
        /** Loading dialog contents */
        Row(
            modifier = Modifier
                .padding(horizontal = Dimension.pagePadding)
                .clip(shape = MaterialTheme.shapes.small)
                .background(MaterialTheme.colors.background)
                .padding(horizontal = Dimension.md, vertical = Dimension.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(Dimension.md),
                strokeWidth = Dimension.hoverEffectPadding
            )
            Spacer(modifier = Modifier.width(Dimension.md))
            Text(
                text = "Please wait",
                style = MaterialTheme.typography.body1.copy(fontSize = FontSize.sm),
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.8f),
            )
        }
    }
}