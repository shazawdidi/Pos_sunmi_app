package com.altkamul.xpay.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altkamul.xpay.R
import com.altkamul.xpay.model.*
import com.altkamul.xpay.model.request.CheckoutPaymentMode
import com.altkamul.xpay.model.request.CheckoutRequest
import com.altkamul.xpay.model.response.CheckoutTransaction
import com.altkamul.xpay.model.response.TransactionPayment
import com.altkamul.xpay.repositroy.TransactionOperationsRepository
import com.altkamul.xpay.sealed.DiscountCategory
import com.altkamul.xpay.sealed.DiscountType
import com.altkamul.xpay.sealed.ServerResponse
import com.altkamul.xpay.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.random.Random

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: TransactionOperationsRepository,
    private val context: Context,
) : ViewModel() {

    /** First we should get all the necessary configurations related to this merchant */
    private val configurations = LoggedMerchantPref.configuration

    /** The discount type supported by the merchant */
    private val merchantDiscountType: DiscountType =
        configurations?.discountType ?: DiscountType.Both

    /** Merchant tax percent */
    private val merchantTaxPercent = configurations?.taxValue ?: 0.0

    /** Merchant language */
    val language = context.dataStore.data.map {
        it[APP_LANGUAGE] ?: "en"
    }

    /** Items coming from home page */
    val items: MutableList<Item> = mutableStateListOf()

    /** Our mapped cart items */
    val cartItems: MutableList<CartItem> = mutableStateListOf()

    /** The overall discount categories with its current values */
    val discounts: MutableList<DiscountCategory> = mutableStateListOf(
        DiscountCategory.Coupon(),
        DiscountCategory.Phone(),
        DiscountCategory.CarPlate()
    )
    val paymentMethods: MutableList<PaymentMethod> = mutableListOf(
        PaymentMethod(1, R.string.cash, true),
        PaymentMethod(2, R.string.bank, true),
        PaymentMethod(3, R.string.nfc, false),
    )

    /** The current selected payment method */
    var paymentMethodId = mutableStateOf(1)

    /** Then all the summary data, it should be updated whenever there are a change on #netPrice or #priceAfterDiscount */
    val netPrice = mutableStateOf(0.0)
    private val priceAfterDiscount = mutableStateOf(0.0)
    val discountSummary = mutableStateOf(0.0)
    val discountSummaryPercent = mutableStateOf(0.0)
    val taxPercent = mutableStateOf(merchantTaxPercent)
    val tax = mutableStateOf(0.0)
    val overallPrice = mutableStateOf(0.0)

    /** Should show result dialog now or not */
    val shouldShowTransactionSummaryDialog = mutableStateOf(false)

    /** Is items mapped to cartItems */
    val isItemsMapped = mutableStateOf(false)

    /** Should show loading screen or not */
    val isTransactionLoading = mutableStateOf(false)

    /** Checkout error message */
    val checkoutErrMessage = mutableStateOf("")

    /** Finally, transaction object that we had made so far */
    private val _transaction = MutableLiveData<Transaction>()
    val transaction: LiveData<Transaction> = _transaction


    /** A function to map normal items to CartItems that can be used */
    fun updateCartItems(items: MutableList<Item>, cartItems: MutableList<CartItem>) {
        /** Assign our items list */
        this.items.addAll(items)
        this.cartItems.addAll(cartItems)
//        /** Here we should specify the type of discount we shall use, currently am gonna use the default - ByValue - */
//        items.map { item ->
//            cartItems.add(
//                CartItem(
//                    itemId = item.itemId ?: return@map,
//                    qty = 1,
//                    realPrice = item.facePrice?.toDouble() ?: 0.0,
//                    totalPrice = item.facePrice?.toDouble() ?: 0.0,
//                    maxDiscount = item.discount ?: 0.0,
//                    discountType = if (merchantDiscountType == DiscountType.Both) DiscountType.ByValue() else merchantDiscountType,
//                    discountValue = 0.0,
//                )
//            )
//        }
        isItemsMapped.value = true
        hoistNewPrices()
    }

    /** A function that is used to hoist the updates to all our states */
    private fun hoistNewPrices() {
        netPrice.value = cartItems.sumOf { it.realPrice * it.qty }.roundToTwoDecimal()
        priceAfterDiscount.value = cartItems.sumOf { it.totalPrice }.roundToTwoDecimal()
        discountSummary.value = netPrice.value - priceAfterDiscount.value.roundToTwoDecimal()
        discountSummaryPercent.value = 100 * (discountSummary.value / netPrice.value)
        tax.value = (netPrice.value * (merchantTaxPercent / 100)).roundToTwoDecimal()
        overallPrice.value =
            (netPrice.value - discountSummary.value + tax.value).roundToTwoDecimal()
    }

    /** Update the payment method that the customer want to pay with */
    fun updatePaymentMethod(id: Int) {
        Timber.d("Current payment method is ${paymentMethods[id]}")
        paymentMethodId.value = id
    }

    /** Update the current discount on an item and then hoist the new prices to the observers */
    fun updateDiscountOnItem(
        index: Int,
        discountValue: Double,
        discountType: DiscountType,
    ) {
        cartItems[index] = cartItems.elementAt(index = index).let {
            it.copy(
                discountType = discountType,
                discountValue = discountValue,
                totalPrice = it.realPrice.getPriceAfterDiscount(
                    discountType = discountType,
                    discountValue = discountValue,
                    qty = it.qty
                )
            )
        }
        hoistNewPrices()
    }

    /** Update the current item's quantity and then hoist the new prices to the observers */
    fun updateItemQuantity(index: Int, quantity: Int) {
        /** First we handle the case of item being decreased till #quantity = 0 , we should delete it and return  */
        if (quantity == 0) cartItems.removeAt(index = index).also {
            items.removeAt(index = index)
            /** Then hoist the update to the observers */
            hoistNewPrices()
            return
        }

        /** If the #quantity is > 0, just update the item's quantity */
        cartItems[index] = cartItems.elementAt(index = index).let {
            it.copy(
                qty = quantity,
                totalPrice = it.realPrice.getPriceAfterDiscount(
                    discountType = it.discountType,
                    discountValue = it.discountValue,
                    qty = quantity
                )
            )
        }
        hoistNewPrices()
    }

    /** update the overall discount that should be applied to all items together */
    fun updateOverallDiscount(discountCategoryIndex: Int, value: Int) {
        discounts[discountCategoryIndex] = when (discounts[discountCategoryIndex]) {
            is DiscountCategory.Coupon -> {
                DiscountCategory.Coupon(value = value)
            }
            is DiscountCategory.Phone -> {
                DiscountCategory.Phone(value = value)
            }
            is DiscountCategory.CarPlate -> {
                DiscountCategory.CarPlate(value = value)
            }
        }
        /** Now it's time to update our summary info */
        hoistNewPrices()
    }

    /** A function that is used to show receipt summary dialog with timer and rollback option */
    fun toggleReceiptDialogState() {
        shouldShowTransactionSummaryDialog.value = !shouldShowTransactionSummaryDialog.value
    }

    /** Function that is used to send the acutal checkout request */
    fun makeTransaction(
        onTransactionSuccess: () -> Unit,
        onTransactionFailed: (reason: String) -> Unit,
    ) {

        /** First we show loading's dialog */
        isTransactionLoading.value = true
        val paymentMode = paymentMethods.find { it.id == paymentMethodId.value }?.let {
            CheckoutPaymentMode(amount = overallPrice.value, paymentModeId = it.id)
        } ?: throw IllegalStateException("payment method shouldn't be null !")

        /** Build our checkout request */
        val checkoutRequest = CheckoutRequest(
            checkoutItems = cartItems,
            checkoutPaymentModes = mutableListOf(paymentMode),
            couponCode = "",
            mobileNumber = "97123456789",
            param1 = "",
            param2 = "",
            param3 = "",
        )
        /** Let FIGHT begins! */
        viewModelScope.launch {
            val response = cartRepository.makeTransaction(checkoutRequest = checkoutRequest)
            /** Hide loading's dialog */
            isTransactionLoading.value = false
            when (response) {
                is ServerResponse.Success -> {
                    Timber.d("Transaction is succeed !")
                    /** Sheers ! we had made the transaction successfully ! */
                    val data = response.data
                        ?: throw IllegalArgumentException("Checkout response should not be null !")

                    /** Time to build the transaction */
                    val transaction = buildTransactionObject(data = data.checkoutTransaction)

                    /** Fill the required data manually */
                    val transactionItems =
                        getStructuredTransactionsItems(data.checkoutTransaction.transactionDetails)
                    /** Assign the items to the transaction */
                    transaction.transactionDetail = transactionItems
                    /** Assign the payment methods */
                    transaction.transactionPayment =
                        data.checkoutTransaction.transactionPayment?.also { payment ->
                            payment.forEach {
                                it.transactionMasterId = transaction.transactionMasterId
                            }
                        } ?: mutableListOf()
                    /** Actually save the transaction */
                    saveTransactionLocally(transaction = transaction)
                    /** Update our reference */
                    _transaction.value = transaction
                    /** Pass completion event */
                    onTransactionSuccess()

                }
                is ServerResponse.Error -> {
                    /**
                     * Transaction failed unfortunately, we should be honest and tell the user man !
                     * also we gonna save it locally as offline, so that we can upload it whenever we had a internet connection
                     */
                    val uniqueFakeMasterId = Calendar.getInstance().timeInMillis.toInt()
                    val transaction = Transaction(
                        transactionMasterId = uniqueFakeMasterId,
                        terminalId = LoggedMerchantPref.terminalId ?: "125",
                        merchantId = LoggedMerchantPref.merchant?.merchantId ?: "1111",
                        branchId = LoggedMerchantPref.branch?.id,
                        branchName = LoggedMerchantPref.branch?.name ?: "No Name",
                        transactionDateTime = Date().getFormattedDate("yyyy-MM-dd HH:mm:ss")
                            .replace(" ", "T"),
                        payments = "",
                        worker = LoggedMerchantPref.user?.name ?: "User with no name",
                        userId = LoggedMerchantPref.user?.userId ?: "1",
                        voucherNO = 1,
                        carNo = null,
                        customerNo = "",
                        totalAmount = overallPrice.value,
                        totalQty = cartItems.sumOf { it.qty }.toDouble(),
                        totalDiscount = discountSummary.value,
                        totalTaxAmount = tax.value,
                        total = overallPrice.value,
                        address = LoggedMerchantPref.address?.branchAddress,
                        city = LoggedMerchantPref.address?.city?.name,
                        country = LoggedMerchantPref.address?.location,
                        phone = LoggedMerchantPref.branch?.firstPhoneNumber,
                        qr = "",
                        barcode = "",
                        param1 = "",
                        param2 = "",
                        param3 = "",
                        couponCode = "",
                        mobileNumber = "",
                        printingConfirmation = false,
                        coupon = "",
                        isDeleted = false,
                        isOffline = true,
                    )

                    /** Build our transaction items */
                    val transactionItems = cartItems.map { item ->
                        val uniqueFakeDetailsId = Calendar.getInstance().timeInMillis.toInt()
                        TransactionItem(
                            transactionMasterId = transaction.transactionMasterId ?: Random.nextInt(
                                1,
                                10000),
                            transactionDetailId = uniqueFakeDetailsId,
                            itemId = item.itemId,
                            customerPaidAmount = 0.0,
                            isDeleted = false,
                        )
                    }.also { getStructuredTransactionsItems(it) }
                    /** Assign the items to the transaction */
                    transaction.transactionDetail = transactionItems

                    /** Assign the payment methods */
                    val transactionPayments =
                        paymentMethods.filter { it.id == paymentMethodId.value }.map {
                            val uniqueFakePaymentId = Calendar.getInstance().timeInMillis.toInt()
                            TransactionPayment(
                                transactionPaymentId = uniqueFakePaymentId,
                                transactionMasterId = transaction.transactionMasterId
                                    ?: Random.nextInt(1, 10000),
                                paymentModeId = it.id,
                                amount = overallPrice.value,
                            )
                        }
                    transaction.transactionPayment = transactionPayments
                    /** Actually save the transaction */
                    saveTransactionLocally(transaction = transaction)
                    /** Update our reference */
                    _transaction.value = transaction
                    /** Pass completion event */
                    onTransactionFailed(response.message ?: "No message specified")
                }
            }
        }
    }

    private fun getStructuredTransactionsItems(transactionDetails: List<TransactionItem>): List<TransactionItem> {
        transactionDetails.forEach { trxItem ->
            trxItem.qty = cartItems.find { it.itemId == trxItem.itemId }?.qty ?: 1
            trxItem.totalPrice = cartItems.find { it.itemId == trxItem.itemId }?.totalPrice ?: 0.0
            trxItem.discount = cartItems.find { it.itemId == trxItem.itemId }?.discountValue ?: 0.0
            trxItem.taxRate = merchantTaxPercent
            trxItem.taxAmount = cartItems.find { it.itemId == trxItem.itemId }?.let {
                ((it.realPrice * it.qty) * (merchantTaxPercent / 100)).roundToTwoDecimal()
            } ?: 0.0
            trxItem.faceValue =
                items.find { it.itemId == trxItem.itemId }?.facePrice?.toDouble() ?: 0.0
            trxItem.charges = 0
            trxItem.service = items.find { it.itemId == trxItem.itemId }?.getSubcategoryName()
                ?: "No service name"
            trxItem.itemName =
                items.find { it.itemId == trxItem.itemId }?.getName() ?: "Nameless Item"
        }
        return transactionDetails
    }

    private fun saveTransactionLocally(transaction: Transaction) {
        viewModelScope.launch {
            cartRepository.saveTransactionLocally(transaction = transaction)
        }
    }

    private fun buildTransactionObject(data: CheckoutTransaction): Transaction {
        return Transaction(
            transactionMasterId = data.transactionMasterId,
            terminalId = data.terminalId ?: "125",
            merchantId = LoggedMerchantPref.merchant?.merchantId ?: "None",
            branchId = data.branchId ?: LoggedMerchantPref.branch?.id,
            branchName = LoggedMerchantPref.branch?.name ?: "None",
            transactionDateTime = data.transactionDateTime,
            payments = "",
            worker = data.userName,
            userId = data.userId,
            voucherNO = data.voucherNO,
            carNo = null,
            customerNo = data.mobileNumber,
            totalAmount = data.totalAmount,
            totalQty = data.totalAmount,
            totalDiscount = data.totalDiscount,
            totalTaxAmount = data.totalTaxAmount,
            total = data.totalAmount,
            address = LoggedMerchantPref.address?.branchAddress,
            city = LoggedMerchantPref.address?.city?.name,
            country = LoggedMerchantPref.address?.location,
            phone = LoggedMerchantPref.branch?.firstPhoneNumber,
            qr = "",
            barcode = "",
            param1 = data.param1,
            param2 = data.param2,
            param3 = data.param3,
            couponCode = data.coupon,
            mobileNumber = data.mobileNumber,
            printingConfirmation = data.printingConfirmation,
            coupon = data.coupon,
            isDeleted = data.isDeleted
        ).also {
            it.transactionPayment = data.transactionPayment ?: listOf()
        }
    }
}