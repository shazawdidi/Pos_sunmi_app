package com.altkamul.xpay.views.screens

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.altkamul.xpay.R
import com.altkamul.xpay.ui.theme.Dimension
import com.altkamul.xpay.ui.theme.lightGray
import com.altkamul.xpay.utils.*
import com.altkamul.xpay.viewmodel.ParentViewModel
import com.altkamul.xpay.viewmodel.TransactionHistoryViewModel
import com.altkamul.xpay.views.components.CustomButton
import com.altkamul.xpay.views.components.CustomInputField
import com.altkamul.xpay.views.components.CustomLazyGrid
import com.altkamul.xpay.views.components.TransactionCardLayout

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionHistoryScreen(
    filterDialogShown: Boolean,
    trxViewModel: TransactionHistoryViewModel = hiltViewModel(),
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val context = LocalContext.current
        val transactionId by remember { trxViewModel.transactionId }
        val isLoading by remember { trxViewModel.isLoading }
        val errorMessage by remember { trxViewModel.errorMessage }
        val transactions = trxViewModel.transactions
        val selectedTransactionId by remember { trxViewModel.selectedTrxId }

        val parentViewModel: ParentViewModel = viewModel(context as ComponentActivity)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .padding(
                    start = Dimension.pagePadding,
                    end = Dimension.pagePadding,
                    bottom = Dimension.pagePadding,
                )
                .verticalScroll(state = rememberScrollState()),
        ) {
            /** Show a toast if #errorMessage is not blank, e.g: had a current value */
            if (errorMessage.isNotBlank()) {
                Common.createToast(context = context, message = errorMessage)
            }
            /** Page title */
            Text(
                modifier = Modifier.fillMaxWidth(0.8f),
                text = stringResource(id = R.string.transaction_history),
                style = MaterialTheme.typography.h2.copy(color = MaterialTheme.colors.secondaryVariant)
            )
            Spacer(modifier = Modifier.height(Dimension.smLineMargin))
            /** Title's slug */
            Text(
                text = stringResource(R.string.history_slug),
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.secondaryVariant.copy(alpha = 0.7f),
            )
            Spacer(modifier = Modifier.height(Dimension.pagePadding))
            /** Search input section */
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CustomInputField(
                    modifier = Modifier.weight(1f),
                    backgroundColor = lightGray.copy(alpha = 0.5f),
                    value = if (transactionId < 1) "" else "$transactionId",
                    keyboardType = KeyboardType.Number,
                    placeholder = stringResource(R.string.enter_receipt_number),
                    onValueChange = {
                        /** Update our temp input value */
                        trxViewModel.errorMessage.value = ""
                        if (it.isNotEmpty()) {
                            trxViewModel.transactionId.value = it.toInt()
                        } else {
                            trxViewModel.transactionId.value = 0
                            /** and then we should show all our transition back */
                            trxViewModel.filterTransactions(transactionId = 0)
                        }
                    }
                )
                Spacer(modifier = Modifier.width(Dimension.pagePadding))
                Icon(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .background(
                            if (transactionId > 0) MaterialTheme.colors.primary else MaterialTheme.colors.secondaryVariant
                        )
                        .clickable {
                            if (transactionId > 0) {
                                /** filter transactions */
                                trxViewModel.filterTransactions(transactionId = transactionId)
                            }
                        }
                        .padding(Dimension.sm)
                        .mirror()
                        .size(Dimension.smIconSize),
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "icon",
                    tint = MaterialTheme.colors.onPrimary,
                )
            }
            Spacer(modifier = Modifier.height(Dimension.pagePadding * 2))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (isLoading) {
                    val composition =
                        rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_dots))
                    val progress =
                        animateLottieCompositionAsState(composition.value, iterations = 10)
                    LottieAnimation(
                        modifier = Modifier
                            .fillMaxSize(0.6f)
                            .aspectRatio(1f)
                            .align(Alignment.Center),
                        composition = composition.value,
                        progress = progress.value,
                    )
                }
                else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = Dimension.pagePadding * 4),
                    ) {
                        val gridSpan = if(ScreenDimensions.Width largerThan Constants.largeDevicesRange.first.dp) 3 else 1

                        transactions.let { transactions ->
                            /** Transaction list */
                            CustomLazyGrid(
                                items = transactions,
                                gridSpan = gridSpan,
                                scrollable = false,
                                contentPadding = PaddingValues(Dimension.zero)
                            ) {width, trx ->
                                /** Here is the layout of transaction item layout that show item #trx */
                                val date = trx.transactionDateTime.split("T").first()
                                val time = trx.transactionDateTime.split("T").last()
                                TransactionCardLayout(
                                    trxModifier = Modifier
                                        .width(width = width),
                                    title = stringResource(id = R.string.transaction_no,
                                        trx.transactionMasterId ?: 0),
                                    date = date,
                                    time = time,
                                    selected = trx.transactionMasterId == selectedTransactionId,
                                    amount = stringResource(id = R.string.x_aed, trx.total),
                                    payments = trx.transactionPayment,
                                    onClick = {
                                        trxViewModel.selectedTrxId.value = trx.transactionMasterId ?: 0
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
        /** Buttons section - Claim and Reprint - which is only visible when there exist a current selected transaction from the list */
        if(selectedTransactionId != 0){
            /** Getting the selected transaction */
            val transaction = transactions.find { it.transactionMasterId == selectedTransactionId }
                ?: throw IllegalArgumentException("Selected transaction is null !")

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .shadow(elevation = Dimension.surfaceElevation)
                    .background(color = MaterialTheme.colors.surface)
                    .padding(Dimension.pagePadding)
                    .fillMaxWidth()
            ) {
                val claimTxt = if(transaction.isClaimed == true) stringResource(R.string.claimed) else stringResource(id = R.string.claim)
                val claimEnabled = transaction.isClaimed == false
                /** Claim button */
                CustomButton(
                    modifier = Modifier
                        .weight(1f)
                        .clip(MaterialTheme.shapes.small),
                    elevationEnabled = false,
                    buttonColor = MaterialTheme.colors.secondary.copy(alpha = 0.4f),
                    enabled = claimEnabled,
                    contentColor = MaterialTheme.colors.secondary,
                    text = claimTxt,
                    onButtonClicked = {
                        /** On Claim */
                        trxViewModel.claimTransactionById(transactionId = transaction.transactionMasterId ?: 0)
                    }
                )
                Spacer(modifier = Modifier.width(Dimension.sm))
                /** Reprint button */
                CustomButton(
                    modifier = Modifier
                        .weight(1f)
                        .clip(MaterialTheme.shapes.small),
                    buttonColor = MaterialTheme.colors.secondary,
                    contentColor = MaterialTheme.colors.onSecondary,
                    text = stringResource(id = R.string.reprint),
                    onButtonClicked = {
                        /** On reprint */
                        parentViewModel.callPrinter(
                            transaction = transaction,

                        )
                    }
                )
            }
        }
    }
}