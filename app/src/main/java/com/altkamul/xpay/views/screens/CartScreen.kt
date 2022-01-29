package com.altkamul.xpay.views.screens

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.altkamul.xpay.R
import com.altkamul.xpay.model.CartItem
import com.altkamul.xpay.model.Item
import com.altkamul.xpay.sealed.Screen
import com.altkamul.xpay.ui.theme.Dimension
import com.altkamul.xpay.utils.Constants
import com.altkamul.xpay.utils.ScreenDimensions
import com.altkamul.xpay.utils.largerThan
import com.altkamul.xpay.viewmodel.CartViewModel
import com.altkamul.xpay.viewmodel.ParentViewModel
import com.altkamul.xpay.views.components.*
import timber.log.Timber

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CartScreen(
    navController: NavHostController,
    cartViewModel: CartViewModel = hiltViewModel(),
    parentViewModel: ParentViewModel =
        hiltViewModel(LocalContext.current as ComponentActivity)
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        /** Getting the items passed from home and map it to a cart items */
        LaunchedEffect(key1 = true) {
            /** Items coming from home screen */
            val items = navController.previousBackStackEntry
                ?.savedStateHandle?.get<MutableList<Item>>("items")
                ?: throw IllegalStateException("Items passed passed from home should not be null!")
            val cartItems = navController.previousBackStackEntry
                ?.savedStateHandle?.get<MutableList<CartItem>>("cartItem")
                ?: throw IllegalStateException("Cart items passed passed from home should not be null!")

            /** Initiate the cart screen */
            cartViewModel.updateCartItems(items = items,cartItems = cartItems)
        }
        /** Getting our items and cart items from the view model */
        val items: MutableList<Item> = cartViewModel.items
        val cartItems: MutableList<CartItem> = cartViewModel.cartItems
        /** Is items mapped to cartItems or not yet */
        val isItemsMapped by remember { cartViewModel.isItemsMapped }
        /** When cart items decreased until it's empty, then we should navigate back to home */
        LaunchedEffect(key1 = cartItems.isEmpty() && isItemsMapped){
            if(cartItems.isEmpty() && isItemsMapped){
                navController.popBackStack()
            }
        }
        /** The discount types with its current values */
        val discounts = cartViewModel.discounts
        /** Our supported payment methods - till now */
        val paymentMethods = cartViewModel.paymentMethods
        /** The current selected payment method */
        val paymentMethodId by remember { cartViewModel.paymentMethodId }
        /** Screen scrolling state */
        val scrollState = rememberScrollState()
        /** Catch the event of user's scrolling, by default it's false  */

        /** Cart's overall summary */
        val netPrice by remember { cartViewModel.netPrice }
        val discountSummaryPercent by remember { cartViewModel.discountSummaryPercent }
        val discountSummary by remember { cartViewModel.discountSummary }
        val tax by remember { cartViewModel.tax }
        val taxPercent by remember { cartViewModel.taxPercent }
        val overallPrice by remember { cartViewModel.overallPrice }
        /** Finally, observe the transaction object that we had made */
        val transaction = cartViewModel.transaction.observeAsState()

        /** Dialogs section */
        /** Whether or not should show result dialog */
        val checkoutSummaryShown by remember { cartViewModel.shouldShowTransactionSummaryDialog }
        if(checkoutSummaryShown){
            CheckoutResultDialog(
                overallPrice = overallPrice,
                paymentMethod = paymentMethods.find { it.id == paymentMethodId }
                    ?: throw IllegalArgumentException("Payment method can't be null !"),
                onDialogDismissed = {
                    /** catch the event of auto hide timer end or user choose to hide it*/
                    cartViewModel.shouldShowTransactionSummaryDialog.value = false
                    /** Then we should make our transaction now ! */
                    cartViewModel.makeTransaction(
                        onTransactionSuccess = {
                            /** Transaction succeed, print the receipt now  */
                            transaction.value?.let { transaction ->
                                /** First we should check for data updates */
                                parentViewModel.checkForDataUpdates()
                                /** Print the transaction */
                                parentViewModel.callPrinter(
                                    transaction
                                )
                                Timber.d("transaction id is ${transaction.transactionMasterId}")
                                /** Then navigate to home */
                                navController.navigate(Screen.Home.route){
                                    popUpTo(Screen.Home.route){
                                        inclusive = true
                                    }
                                }
                            } ?: throw IllegalStateException("Transaction shouldn't be null !")
                        },
                        onTransactionFailed = { reason ->
                            /** Transaction saved offline and will be send to server when there are a connection, print the receipt now  */
                            transaction.value?.let { transaction ->
                                /** First we should check for data updates */
                                parentViewModel.checkForDataUpdates()
                                /** Print the transaction */
                                parentViewModel.callPrinter(
                                    transaction,
                                )
                                Timber.d("transaction id is ${transaction.transactionMasterId}")
                                /** Then navigate to home */
                                navController.navigate(Screen.Home.route){
                                    popUpTo(Screen.Home.route){
                                        inclusive = true
                                    }
                                }
                            } ?: throw IllegalStateException("Transaction shouldn't be null !")
                        }
                    )
                },
                onRollback = {
                    /** when the user click roll back button we should ignore all we did and hide dialog*/
                    cartViewModel.toggleReceiptDialogState()
                }
            )
        }
        /** A dialog that tell the user that transaction is being on fire and he should wait */
        val transactionIsOnFire by remember { cartViewModel.isTransactionLoading }
        if (transactionIsOnFire) {
            Dialog(
                onDismissRequest = {},
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false,
                )
            ){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .shadow(
                            elevation = Dimension.surfaceElevation,
                            shape = MaterialTheme.shapes.medium,
                        )
                        .clip(shape = MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colors.surface),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val composition = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_dots))
                    LottieAnimation(
                        modifier = Modifier
                            .width(Dimension.xxxl)
                            .aspectRatio(1f),
                        composition = composition.value,
                        restartOnPlay = true
                    )
                    Text(
                        text = stringResource(id = R.string.purchasing),
                        style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.primary)
                    )
                }
            }
        }

        /** Actual Screen UI */
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(enabled = true, state = scrollState)
                .background(MaterialTheme.colors.background)
                .padding(
                    start = Dimension.pagePadding,
                    end = Dimension.pagePadding,
                    bottom = Dimension.pagePadding,
                ),
        ) {

            /** Page's title */
            Text(
                text = stringResource(R.string.cart),
                style = MaterialTheme.typography.h2
            )
            Spacer(modifier = Modifier.height(Dimension.pagePadding))
            /** Our items List */
            val itemsSpan =
                if(ScreenDimensions.Width largerThan Constants.largeDevicesRange.first.dp) 4 else 1
            CustomLazyGrid(
                items = items,
                gridSpan = itemsSpan,
                scrollable = false,
                contentPadding = PaddingValues(Dimension.zero)
            ) { width, item ->
                val index = items.indexOf(item)
                if(index != -1){
                    val cartItem = cartItems[index]
                    CartItemLayout(
                        modifier = Modifier.width(width = width),
                        item = item,
                        qty = cartItem.qty,
                        discountType = cartItem.discountType,
                        appliedDiscount = cartItem.discountValue,
                        onDiscountAdded = { discountValue, discountType ->
                            /** Showing item's discount dialog */
                            cartViewModel.updateDiscountOnItem(
                                index = index,
                                discountValue = discountValue,
                                discountType = discountType)
                        },
                        onDiscountRemoved = {
                            cartViewModel.updateDiscountOnItem(index = index,discountValue = 0.0,discountType = cartItem.discountType)
                        },
                        onQuantityIncreased = {
                            /** Increase quantity of that item in our cart */
                            cartViewModel.updateItemQuantity(index = index, cartItem.qty.inc())
                        },
                        onQuantityDecreased = {
                            /** Decrease quantity of that item in our cart */
                            cartViewModel.updateItemQuantity(index = index, cartItem.qty.dec())
                        },
                        onItemDeleted = {
                            /** Remove that item from our cart list */
                            /** Decrease quantity of that item in our cart */
                            cartViewModel.updateItemQuantity(index = index, quantity = 0)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(Dimension.xs))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ){
                Text(
                    text = stringResource(R.string.forgot_something),
                    style = MaterialTheme.typography.body1,
                )
                /** Add more button */
                Text(
                    text = stringResource(R.string.add_more),
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .shadow(
                            elevation = Dimension.surfaceElevation,
                            shape = MaterialTheme.shapes.medium,
                        )
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colors.secondary)
                        .clickable {
                            val itemsToReturn = mutableListOf<Item>().also {
                                it.addAll(items)
                            }
                            val cartItemsToReturn = mutableListOf<CartItem>().also {
                                it.addAll(cartItems)
                            }
                            navController.previousBackStackEntry?.savedStateHandle?.set("item",itemsToReturn )
                            navController.previousBackStackEntry?.savedStateHandle?.set("cart", cartItemsToReturn)
                            navController.popBackStack()
                        }
                        .padding(horizontal = Dimension.pagePadding, vertical = Dimension.xs),
                    color = MaterialTheme.colors.onSecondary,
                )
            }
            Spacer(modifier = Modifier.height(Dimension.xs))
            /** Our payment info section */
            PaymentInfoSection(
                paymentMethods = paymentMethods,
                currentMethodId = paymentMethodId,
                onDiscountAdded = { discountCategoryIndex, value ->
                    cartViewModel.updateOverallDiscount(discountCategoryIndex = discountCategoryIndex,
                        value = value)
                },
                onDiscountRemoved = {

                },
                discountCategories = discounts,
                onPaymentSelected = { selectedPaymentMethod ->
                    /** We should update #paymentMethodId */
                    cartViewModel.updatePaymentMethod(selectedPaymentMethod)
                }
            )
        }
        /** Summary section */
        SummarySection(
            scrolling = true,
            net = netPrice,
            discountPercent = discountSummaryPercent,
            discount = discountSummary,
            taxPercent = taxPercent,
            tax = tax,
            onCheckout = {
                /** When user click payment button, show summary dialog with a timer */
                cartViewModel.toggleReceiptDialogState()
            },
        )
    }
}