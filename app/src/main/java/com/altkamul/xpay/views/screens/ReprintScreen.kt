package com.altkamul.xpay.views.screens

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.ReportProblem
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.altkamul.xpay.R
import com.altkamul.xpay.ui.theme.Dimension
import com.altkamul.xpay.ui.theme.lightGray
import com.altkamul.xpay.utils.Common
import com.altkamul.xpay.utils.mirror
import com.altkamul.xpay.viewmodel.ParentViewModel
import com.altkamul.xpay.viewmodel.ReprintViewModel
import com.altkamul.xpay.views.components.CustomButton
import com.altkamul.xpay.views.components.CustomInputField
import com.altkamul.xpay.views.components.TransactionCardLayout
import timber.log.Timber

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReprintScreen(
    reprintViewModel: ReprintViewModel = hiltViewModel(),
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(Dimension.pagePadding),
    ) {
        val context = LocalContext.current
        val noEnteredRecNumMess = stringResource(R.string.please_enter_receipt_num)
        val transactionId by remember { reprintViewModel.transactionId }
        val isLoading by remember { reprintViewModel.isLoading }
        val errorMessage by remember { reprintViewModel.errorMessage }
        val transaction by reprintViewModel.transaction.observeAsState()

        val parentViewModel: ParentViewModel = viewModel(context as ComponentActivity)
        /** When user delete the search query, then we should delete the previous result */
        if (transactionId == 0) {
            reprintViewModel.clearPreviousSearch()
        }

        /** Page title */
        Text(
            modifier = Modifier.fillMaxWidth(0.8f),
            text = stringResource(id = R.string.reprint),
            style = MaterialTheme.typography.h2.copy(color = MaterialTheme.colors.secondaryVariant)
        )
        Spacer(modifier = Modifier.height(Dimension.smLineMargin))
        /** Title's slug */
        Text(
            text = stringResource(R.string.reprint_slug),
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.secondaryVariant.copy(alpha = 0.7f),
        )
        Spacer(modifier = Modifier.height(Dimension.pagePadding))
        /** Search input section */
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ){
            CustomInputField(
                modifier = Modifier.weight(1f),
                value = if(transactionId < 1) "" else "$transactionId" ,
                backgroundColor = lightGray.copy(alpha = 0.5f),
                keyboardType = KeyboardType.Number,
                placeholder = stringResource(R.string.enter_receipt_number),
                onValueChange = {
                    /** Update our temp input value */
                    reprintViewModel.errorMessage.value = ""
                    reprintViewModel.transactionId.value = if(it.isNotEmpty()) it.toInt() else 0
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
                            /** find transaction with that id */
                            reprintViewModel.clearPreviousSearch()
                            reprintViewModel.getTransactionWithId(transactionId = transactionId)
                        } else {
                            Common.createToast(
                                context = context,
                                message = noEnteredRecNumMess
                            )
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

        /** Result container */
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(state = rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ){
            /** Check if its loading */
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(Dimension.xxl )
                )
            }
            else {
                if(transactionId == 0){
                    /** No receipt searched yet message */
                    Image(
                        modifier = Modifier
                            .width(160.dp)
                            .aspectRatio(0.7f),
                        painter = painterResource(id = R.drawable.receipt_flat_icon),
                        contentDescription = "receipt",
                    )
                    Text(
                        text = stringResource(id = R.string.no_receipt),
                        style = MaterialTheme.typography.h2.copy(color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f))
                    )
                    Spacer(modifier = Modifier.height(Dimension.pagePadding))
                    Text(
                        text = stringResource(R.string.reprint_search_slug),
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.secondaryVariant.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(Dimension.pagePadding))
                    CustomButton(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        elevationEnabled = true,
                        buttonColor = MaterialTheme.colors.primary,
                        contentColor = MaterialTheme.colors.onPrimary,
                        text = stringResource(R.string.reprint_last_receipt),
                        onButtonClicked = {
                            /** Should reprint the last receipt in our database */
                            reprintViewModel.getLastTransaction()
//                    .let {
//                    /** If loading finished, we ready to go now ! */
//                    if(! isLoading){
//                        transaction?.let {
//                            PrinterUtil(context = context).printerTester()
//                        } ?: throw IllegalArgumentException("Transaction to be reprinted should not be null !")
//                    }
//                }
                        }
                    )
                }
                else {
                    /** If it's not loading now and also the user had entered a receipt id number, check if there are an error message */
                    if (errorMessage.isNotEmpty()) {
                        /** Shit, we had a message which mean some error had occurred ! */
                        Image(
                            modifier = Modifier
                                .size(Dimension.lgIconSize),
                            imageVector = Icons.Rounded.ReportProblem,
                            contentDescription = "error",
                        )
                        Spacer(modifier = Modifier.height(Dimension.lgLineMargin))
                        Text(
                            text = stringResource(id = R.string.error_happened),
                            style = MaterialTheme.typography.h2.copy(color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f))
                        )
                        Spacer(modifier = Modifier.height(Dimension.smLineMargin))
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.secondaryVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                        )
                    } else {
                        /** We don't have an error message , time to show our transaction details */
                        transaction?.let {
                            var selected by remember { mutableStateOf(false) }
                            val date = it.transactionDateTime.split("T").first()
                            val time = it.transactionDateTime.split("T").last()
                            /** Transaction item layout here with click event to reprint it */
                            TransactionCardLayout(
                                trxModifier = Modifier,
                                date = date,
                                time = time,
                                amount = stringResource(id = R.string.x_aed, it.total),
                                payments = it.transactionPayment,
                                selected = selected,
                                onClick = {
                                    selected = true
                                    parentViewModel.callPrinter(
                                        transaction = it
                                    )
                                },
                                title = stringResource(id = R.string.receipt_no,it.transactionMasterId ?: 1)
                            )
                        } ?: Timber.d("Transaction is null which is supposed to not be !")
                    }
                }
            }
            Spacer(modifier = Modifier.height(Dimension.pagePadding * 2))
        }
    }
}