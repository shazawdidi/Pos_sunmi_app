package com.altkamul.xpay.views.screens

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Print
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.altkamul.xpay.R
import com.altkamul.xpay.model.Transaction
import com.altkamul.xpay.model.TransactionItem
import com.altkamul.xpay.sealed.ReportType
import com.altkamul.xpay.ui.theme.Dimension
import com.altkamul.xpay.utils.*
import com.altkamul.xpay.viewmodel.ParentViewModel
import com.altkamul.xpay.viewmodel.ReportsViewModel
import com.altkamul.xpay.views.components.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReportsScreen(
    controller: NavHostController,
    reportsViewModel: ReportsViewModel = hiltViewModel(),
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(horizontal = Dimension.pagePadding)
    ) {

        val parentViewModel: ParentViewModel = viewModel(LocalContext.current as ComponentActivity)
        /** Firstly , we define scroll states for all tabs */
        val transactionLazyState = rememberLazyListState()
        val productsLazyState = rememberLazyListState()
        val cashierLazyState = rememberLazyListState()

        val currentType by remember { reportsViewModel.currentTab }
        val isLoading by remember { reportsViewModel.isLoading }
        val errMessage by remember { reportsViewModel.errorMessage }
        val longDate by remember { reportsViewModel.date }
        val userIndex by remember { reportsViewModel.userIndex }
        val transactions = reportsViewModel.transactions
        val products = reportsViewModel.products
        val cashierTransactions = reportsViewModel.cashierTransactions
        /** List of all cashiers */
        val cashiers = reportsViewModel.cashiers
        val cashierFilterItems = mutableListOf<String>().also {
            /** Add the default option */
            it.add(stringResource(R.string.select_cashier))
            it.addAll(cashiers.map { user -> user.name ?: "Default cashier name" })
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ){
            /** Page title */
            Text(
                text = stringResource(id = R.string.reports),
                style = MaterialTheme.typography.h2.copy(color = MaterialTheme.colors.secondaryVariant)
            )
            /** Print button  */
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .clickable {
                        transactions.forEach {transaction->
                            parentViewModel.callPrinter(
                                transaction
                            )
                        }
                    }
                    .padding(horizontal = Dimension.xs),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimension.pagePadding.div(2))
            ){
                Text(
                    text = stringResource(R.string.print),
                    style = MaterialTheme.typography.subtitle1,
                    color = MaterialTheme.colors.onBackground,
                )
                Icon(
                    modifier = Modifier
                        .size(Dimension.mdIconSize)
                        .clip(CircleShape),
                    imageVector = Icons.Outlined.Print,
                    contentDescription = "print icon",
                    tint = MaterialTheme.colors.onBackground.copy(alpha = 0.8f),
                )
            }
        }
        Spacer(modifier = Modifier.height(Dimension.pagePadding.div(2)))
        ReportsTypesTabs(
            selected = currentType,
            onTabSelected = {
                /** Catching the event of clicking on a new tab & update it */
                reportsViewModel.updateCurrentTab(it)
            },
        )
        if (currentType == ReportType.CashiersReport){
            /** Cashiers filter */
            Spacer(modifier = Modifier.height(Dimension.xs))
            CashiersSelector(
                value = cashierFilterItems[userIndex],
                items = cashierFilterItems,
                onValueChanged = {index->
                    reportsViewModel.filterTransactions(date = longDate, index = index)
                }
            )
        }
        /** Date filters */
        Row(
            modifier = Modifier
                .padding(vertical = Dimension.pagePadding)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ){
            /** Date selector */
            DatePicker(
                modifier = Modifier
                    .weight(2f),
                onDateUpdated = {newDate->
                    reportsViewModel.date.value = newDate
                    if(newDate == 0L) {
                        /** Directly clear the date filters */
                        reportsViewModel.filterTransactions(
                            date = newDate,
                            index = if(currentType == ReportType.CashiersReport) userIndex else null
                        )
                    }
                },
                currentPicked = longDate,
                placeholder = stringResource(id = R.string.select_date)
            )
            Spacer(modifier = Modifier.width(Dimension.pagePadding))
            /** Apply button */
            CustomButton(
                modifier = Modifier
                    .weight(1f)
                    .clip(MaterialTheme.shapes.small),
                buttonColor = MaterialTheme.colors.secondary,
                contentColor = MaterialTheme.colors.onSecondary,
                text = stringResource(id = R.string.apply),
                onButtonClicked = {
                    /** On apply */
                    reportsViewModel.filterTransactions(
                        date = longDate,
                        index = if(currentType == ReportType.CashiersReport) userIndex else null
                    )
                }
            )
        }
        /** Tab content */
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
        ) {
            if(isLoading){
                LoadingTabContent()
            }
            else {
                if(transactions.isEmpty()){
                    /** If not loading and also the transaction is empty, print a message to the user */
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = errMessage,
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.secondaryVariant,
                        textAlign = TextAlign.Center,
                    )
                }
                else {
                    when (currentType) {
                        is ReportType.TransactionsReport -> {
                            TransactionsTabContent(
                                transactions = transactions,
                            )
                        }
                        is ReportType.ProductsReport -> {
                            ProductsTabContent(
                                products = products,
                            )
                        }
                        is ReportType.CashiersReport -> {
                            CashierTabContent(
                                transactions = cashierTransactions,
                            )
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun ReportsTypesTabs(
    tabs: List<ReportType> = listOf(ReportType.TransactionsReport,
        ReportType.ProductsReport,
        ReportType.CashiersReport),
    selected: ReportType,
    onTabSelected: (type: ReportType) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(state = rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        tabs.forEachIndexed { index, reportType ->
            var width by remember { mutableStateOf(0) }
            Column(
                modifier = Modifier
                    .padding(end = Dimension.pagePadding)
                    .clip(MaterialTheme.shapes.small)
                    .clickable { onTabSelected(reportType) }
                    .padding(horizontal = Dimension.xs),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    modifier = Modifier.onGloballyPositioned {
                        width = it.size.width
                    },
                    text = stringResource(id = reportType.title),
                    style = MaterialTheme.typography.body1,
                    color = if (selected == reportType) MaterialTheme.colors.primary else MaterialTheme.colors.onBackground.copy(
                        alpha = 0.5f),
                )
                Spacer(
                    modifier = Modifier
                        .padding(top = Dimension.xs)
                        .width(width = width.getDp())
                        .height(Dimension.surfaceElevation)
                        .clip(CircleShape)
                        .background(if (selected == reportType) MaterialTheme.colors.primary else Color.Transparent)
                )
            }
        }
    }
}

@Composable
fun BoxScope.LoadingTabContent() {
    /** Show a loading progress when it's loading */
    val composition =
        rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_dots))
    val progress = animateLottieCompositionAsState(composition.value, iterations = 10)
    LottieAnimation(
        modifier = Modifier
            .fillMaxSize(0.6f)
            .aspectRatio(1f)
            .align(Alignment.Center),
        composition = composition.value,
        progress = progress.value,
    )
}

/** Content of transactions reports tabs */
@Composable
fun TransactionsTabContent(
    transactions: List<Transaction>,
) {
    /** Transaction list */
    val gridSpan = if(ScreenDimensions.Width largerThan Constants.largeDevicesRange.first.dp) 3 else 1
    CustomLazyGrid(
        items = transactions,
        gridSpan = gridSpan,
        contentPadding = PaddingValues(vertical = Dimension.pagePadding)
    ) {width, trx ->
        val date = trx.transactionDateTime.split("T").first()
        val time = trx.transactionDateTime.split("T").last()
        /** Here is the layout of transaction item layout that show item #trx */
        TransactionCardLayout(
            trxModifier = Modifier.width(width = width),
            date = date,
            time = time,
            amount = stringResource(id = R.string.x_aed, trx.total),
            payments = trx.transactionPayment,
            onClick = {
                /** No event for it now */
            },
            title = stringResource(id = R.string.transaction_no, trx.transactionMasterId ?: 0)
        )
    }
}


/** Content of transactions and products reports tabs */
@Composable
fun ProductsTabContent(
    products: List<TransactionItem>,
) {
    /** Products list */
    val gridSpan = if(ScreenDimensions.Width largerThan Constants.largeDevicesRange.first.dp) 3 else 1
    CustomLazyGrid(
        modifier = Modifier.fillMaxWidth(),
        items = products,
        gridSpan = gridSpan,
        contentPadding = PaddingValues(vertical = Dimension.pagePadding)
    ) {width, product ->
        TransactionItemLayout(
            modifier = Modifier.width(width = width),
            title = stringResource(id = R.string.receipt_no,product.transactionMasterId),
            itemName = product.itemName,
            qty = product.qty ?: 1,
            discount = product.discount ?: 0.0,
            totalPrice = product.totalPrice,
        )
    }
}

@Composable
fun CashierTabContent(
    transactions: List<Transaction>,
) {
    /** Transaction list */
    val gridSpan = if(ScreenDimensions.Width largerThan Constants.largeDevicesRange.first.dp) 3 else 1
    CustomLazyGrid(
        modifier = Modifier.fillMaxWidth(),
        items = transactions,
        gridSpan = gridSpan,
        contentPadding = PaddingValues(vertical = Dimension.pagePadding)
    ) {width, trx ->
        val date = trx.transactionDateTime.split("T").first()
        val time = trx.transactionDateTime.split("T").last()
        /** Here is the layout of transaction item that the selected cashier made */
        TransactionCardLayout(
            trxModifier = Modifier.width(width = width),
            date = date,
            time = time,
            amount = stringResource(id = R.string.x_aed, trx.total),
            payments = trx.transactionPayment,
            onClick = {
                /** No event for it now */
            },
            title = stringResource(id = R.string.transaction_no,
                trx.transactionMasterId ?: 0)
        )
    }
}
