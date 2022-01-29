package com.altkamul.xpay.views.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberImagePainter
import com.altkamul.xpay.R
import com.altkamul.xpay.model.Item
import com.altkamul.xpay.model.PaymentMethod
import com.altkamul.xpay.sealed.DiscountCategory
import com.altkamul.xpay.sealed.DiscountType
import com.altkamul.xpay.ui.theme.Dimension
import com.altkamul.xpay.ui.theme.FontSize
import com.altkamul.xpay.ui.theme.lightGray
import com.altkamul.xpay.utils.*
import timber.log.Timber
import java.util.*


@Composable
fun CartItemLayout(
    modifier: Modifier = Modifier,
    qty: Int,
    item: Item,
    appliedDiscount: Double,
    discountType: DiscountType,
    onQuantityIncreased: () -> Unit,
    onQuantityDecreased: () -> Unit,
    onItemDeleted: () -> Unit,
    onDiscountAdded: (value: Double, appliedDiscountType: DiscountType) -> Unit,
    onDiscountRemoved: () -> Unit,
) {
    Timber.d("item with id ${item.itemId} is re-composing now ... ")
    var dialogIsShown by remember { mutableStateOf(false) }
    /** Single item discount's dialog section */
    if (dialogIsShown) {
        SingleItemDiscountDialog(
            maxDiscount = item.discount ?: 0.0,
            appliedDiscountType = discountType,
            appliedDiscount = appliedDiscount,
            onDiscountAdded = onDiscountAdded,
            onDiscountRemoved = onDiscountRemoved,
            onDialogDismissed = {
                dialogIsShown = false
            }
        )
    }
    /** Cart's item UI */
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = Dimension.surfaceElevation, shape = MaterialTheme.shapes.small)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colors.surface)
    ){
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
                .clickable {
                    /** Only show the dialog when this merchant's discount type is not DiscountType.None */
                    if (discountType != DiscountType.None && item.discount ?: 0.0 > 0.0)
                        dialogIsShown = true
                }
        ) {
            var height by remember { mutableStateOf(0) }
            /** Item's image */
            Image(
                painter = rememberImagePainter(
                    // data is url of the image to be loaded
                    data = item.imageUrl,
                ),
                contentDescription = "image",
                modifier = Modifier
                    .weight(0.2f)
                    .height(height = height.getDp())
                    .background(MaterialTheme.colors.onSurface),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .onGloballyPositioned {
                        /** Get the height that the column occupied */
                        height = it.size.height
                    }
                    .weight(0.8f)
                    .padding(Dimension.pagePadding),
            ) {
                /** Cart's item header, which contains item's name and the total price */
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = item.getName(),
                        style = MaterialTheme.typography.body2.copy(fontSize = FontSize.lg),
                        color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f),
                    )
                    /** Total price for the whole quantity */
                    Column {
                        val itemTotalPrice = item.facePrice?.toDouble()?.getPriceAfterDiscount(
                            discountType = discountType,
                            discountValue = appliedDiscount,
                            qty = qty
                        )
                        Text(
                            text = stringResource(R.string.total),
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.secondaryVariant,
                        )
                        Text(
                            text = buildAnnotatedString {
                                append("$itemTotalPrice")
                                /** Currency with the smaller font */
                                withStyle(MaterialTheme.typography.subtitle2.toSpanStyle()) {
                                    append(" ${stringResource(id = R.string.aed_currency)}")
                                }
                            },
                            style = MaterialTheme.typography.h3,
                            color = MaterialTheme.colors.primary,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(Dimension.xs))
                /** Item's subcategory name */
                Text(
                    text = item.getSubcategoryName(),
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.secondaryVariant.copy(alpha = 0.7f),
                )
                Spacer(modifier = Modifier.height(Dimension.xs))
                /** The price for a single item */
                Text(
                    text = buildAnnotatedString {
                        append("${item.facePrice}")
                        /** Currency with the smaller font */
                        withStyle(MaterialTheme.typography.subtitle2.copy(fontSize = FontSize.xs)
                            .toSpanStyle()) {
                            append(" ${stringResource(id = R.string.aed_currency)}")
                        }
                    },
                    style = MaterialTheme.typography.subtitle2,
                    color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f),
                )
                Spacer(modifier = Modifier.height(Dimension.xs))

                /** Cart's item footer, contain user profile and name.
                 *  Also contain increment/decrement icons
                 */
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ){

                        /** Profile image coil painter */
                        val painter = rememberImagePainter(
                            // data is url of the image to be loaded
                            data = LoggedMerchantPref.merchant?.branches?.first()?.images?.defaultlogo ?: "No profile",
                        )
                        /** Our actual footer's UI */
                        Image(
                            painter = painter,
                            contentDescription = "image",
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colors.primary)
                                .size(Dimension.md)
                        )
                        Spacer(modifier = Modifier.width(Dimension.xs))
                        /** User name */
                        Text(
                            text = LoggedMerchantPref.user?.name ?: "No Name",
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.secondaryVariant,
                            maxLines = 1,
                        )
                    }
                    /** Counter which contain - qty + */
                    QuantitySection(
                        qty = qty,
                        onIncrease = {
                            /** Catching the event of increasing the quantity */
                            onQuantityIncreased()
                        },
                        onDecrease = {
                            /** Catching the event of decreasing the quantity */
                            onQuantityDecreased()
                        }
                    )
                }
            }
        }
        Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = "delete icon",
            tint = MaterialTheme.colors.background,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(Dimension.xs)
                .size(Dimension.sm)
                .clip(CircleShape)
                .background(MaterialTheme.colors.secondaryVariant.copy(alpha = 0.5f))
                .clickable {
                    onItemDeleted()
                }
        )
    }
}

@Composable
fun SingleItemDiscountDialog(
    maxDiscount: Double,
    appliedDiscount: Double,
    appliedDiscountType: DiscountType,
    onDialogDismissed: () -> Unit,
    onDiscountAdded: (value: Double, appliedType: DiscountType) -> Unit,
    onDiscountRemoved: () -> Unit,
) {
    Dialog(
        /** Catching the event of clicking out of the dialog and dismiss it */
        onDismissRequest = onDialogDismissed
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = Dimension.surfaceElevation, shape = MaterialTheme.shapes.medium)
                .clip(shape = MaterialTheme.shapes.medium)
                .background(MaterialTheme.colors.surface)
                .padding(Dimension.pagePadding)
        ) {
            /** Current applied discount value and type */
            var currentDiscount by remember { mutableStateOf(appliedDiscount) }
            var currentDiscountType by remember { mutableStateOf(appliedDiscountType) }
            /** A boolean to determine whether or not to show the discount type selector */
            val supportedDiscountType = LoggedMerchantPref.configuration?.discountType ?: DiscountType.Both
            Timber.d("Supported discount type is $supportedDiscountType")
            val isBothDiscountTypesSupported = supportedDiscountType is DiscountType.Both
            /** UI Section */
            /** Dialog's title */
            Text(
                text = stringResource(R.string.item_discount),
                style = MaterialTheme.typography.body2.copy(fontSize = FontSize.lg),
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.8f),
            )
            Spacer(modifier = Modifier.height(Dimension.lgLineMargin))
            CustomInputField(
                value = "${if (currentDiscount > 0) currentDiscount else ""}",
                placeholder = stringResource(R.string.enter_discount_value),
                keyboardType = KeyboardType.Number,
                onValueChange = { discount ->
                    /** New discount value */
                    val newDiscount = if(discount.isNotEmpty()) discount.toDouble() else 0.0
                    /** We have to check if the entered discount doesn't exceed the discount range specified */
                    currentDiscount = if(newDiscount > maxDiscount) maxDiscount else newDiscount
                },
            )
            /** Discount's type selector in case both types are supported */
            if (isBothDiscountTypesSupported) {
                val byValueDiscount = DiscountType.ByValue()
                val byPercentDiscount = DiscountType.ByPercent()

                Spacer(modifier = Modifier.height(Dimension.lgLineMargin))
                Row{
                    DiscountTypeOption(
                        title = stringResource(id = byValueDiscount.name),
                        selected = currentDiscountType.id == byValueDiscount.id,
                        onDiscountTypeSelected = {
                            /** If its not the current already , update it */
                            currentDiscountType = byValueDiscount
                        }
                    )
                    Spacer(modifier = Modifier.width(Dimension.pagePadding))
                    DiscountTypeOption(
                        title = stringResource(id = byPercentDiscount.name),
                        selected = currentDiscountType.id == byPercentDiscount.id,
                        onDiscountTypeSelected = {
                            /** If its not the current already , update it */
                            currentDiscountType = byPercentDiscount
                        }
                    )
                }
            } else {
                val discountTypeName =
                    if(supportedDiscountType is DiscountType.ByValue) stringResource(id = R.string.by_value)
                    else stringResource(id = R.string.by_percent)

                Timber.d("Supported discount is discount by $discountTypeName")
                Spacer(Modifier.height(Dimension.xs))
                Text(
                    text = "Supported discount is discount by $discountTypeName",
                    style = MaterialTheme.typography.subtitle2,
                    color = MaterialTheme.colors.secondary.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.height(Dimension.lgLineMargin))
            /** Apply discount button */
            CustomButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = Dimension.elevation, shape = MaterialTheme.shapes.small)
                    .clip(MaterialTheme.shapes.small),
                buttonColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary,
                text = stringResource(R.string.apply),
                onButtonClicked = {
                    /** On apply button clicked , updated our current discount if it had changed */
                    if (currentDiscount != appliedDiscount || currentDiscountType != appliedDiscountType) {
                        /** Edit the old discount value */
                        onDiscountAdded(currentDiscount, currentDiscountType)
                    }
                    /** And dismiss the dialog */
                    onDialogDismissed()
                }
            )
            Spacer(modifier = Modifier.height(Dimension.lgLineMargin))
            /** Remove current discount button */
            CustomButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small),
                buttonColor = Color.Transparent,
                contentColor = MaterialTheme.colors.primary,
                elevationEnabled = false,
                text = stringResource(R.string.remove_discount),
                onButtonClicked = {
                    /** On remove discount button clicked , delete our current discount */
                    onDiscountRemoved()
                    /** Dismiss the dialog */
                    onDialogDismissed()
                }
            )
        }
    }
}

@Composable
fun DiscountTypeOption(
    title: String,
    selected: Boolean,
    onDiscountTypeSelected: ()-> Unit
) {
    Text(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .border(
                width = if (selected) 1.dp else Dimension.zero,
                color = if (selected) MaterialTheme.colors.primary else Color.Transparent,
                shape = MaterialTheme.shapes.small
            )
            .background(MaterialTheme.colors.background)
            .clickable {
                /** Catching the event of clicking on a discount's type */
                onDiscountTypeSelected()
            }
            .padding(horizontal = Dimension.xs, vertical = Dimension.pagePadding),
        text = title,
        style = MaterialTheme.typography.subtitle1.copy(
            if(selected) MaterialTheme.colors.primary
            else MaterialTheme.colors.secondaryVariant
        )
    )
}

@Composable
fun QuantitySection(
    modifier: Modifier = Modifier,
    onIncrease: () -> Unit = {},
    onDecrease: () -> Unit = {},
    qty: Int = 1,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        /** The Decrement button */
        HoverIcon(
            painter = painterResource(id = R.drawable.ic_less),
            tint = Color.Red,
            onClicked = onDecrease
        )
        Spacer(modifier = Modifier.width(Dimension.xs))
        /** The current quantity */
        Text(
            text = "$qty",
            style = MaterialTheme.typography.h3,
            color = MaterialTheme.colors.onBackground,
        )
        Spacer(modifier = Modifier.width(Dimension.xs))
        /** The Increment button */
        HoverIcon(
            painter = painterResource(id = R.drawable.ic_add),
            tint = MaterialTheme.colors.secondary,
            onClicked = onIncrease
        )
    }
}

@Composable
fun PaymentInfoSection(
    modifier: Modifier = Modifier,
    span: Int = 3,
    discountCategories: List<DiscountCategory>,
    paymentMethods: MutableList<PaymentMethod>,
    currentMethodId: Int,
    onDiscountAdded: (discountCategoryIndex: Int, value: Int) -> Unit,
    onDiscountRemoved: (discountCategoryIndex: Int) -> Unit,
    onPaymentSelected: (methodId: Int) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        var dialogIsShown by remember { mutableStateOf(false) }
        /** Overall discount's dialog section */
        if (dialogIsShown) {
            OverallDiscountDialog(
                discountCategories = discountCategories,
                onDialogDismissed = { dialogIsShown = false },
                onDiscountAdded = onDiscountAdded,
                onDiscountRemoved = onDiscountRemoved
            )
        }
        /** Section header */
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.payment_info),
                style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onBackground.copy(
                    alpha = 0.8f))
            )
            /** The overall discount dialog trigger, supported in the next phase */
//            Text(
//                modifier = Modifier
//                    .clip(CircleShape)
//                    .background(MaterialTheme.colors.primary.copy(alpha = 0.3f))
//                    .clickable {
//                        /** Showing the Total discount dialog */
//                        dialogIsShown = !dialogIsShown
//                    }
//                    .padding(horizontal = Dimension.sm, vertical = Dimension.xs),
//                text = "${stringResource(id = R.string.discount)}  %",
//                style = MaterialTheme.typography.subtitle2.copy(color = MaterialTheme.colors.primary)
//            )
        }
        Spacer(modifier = Modifier.height(Dimension.pagePadding))
        /** Check if its the large sunmi device */
        val isLargeDevice = ScreenDimensions.Width largerThan Constants.largeDevicesRange.first.dp

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if(isLargeDevice) Arrangement.Start else Arrangement.SpaceBetween
        ) {
            val itemModifier = if(isLargeDevice) Modifier else Modifier.weight(1f)
            paymentMethods.forEach { method ->
                /** Payment method single item */
                PaymentItem(
                    modifier = itemModifier,
                    method = method,
                    current = currentMethodId == method.id,
                    onPaymentChange = {
                        /** On payment method changed, pass it up */
                        onPaymentSelected(method.id)
                    }
                )
            }
        }
    }
}

@Composable
fun OverallDiscountDialog(
    discountCategories: List<DiscountCategory>,
    onDialogDismissed: () -> Unit,
    onDiscountAdded: (discountCategoryIndex: Int, value: Int) -> Unit,
    onDiscountRemoved: (discountCategoryIndex: Int) -> Unit,
) {
    Dialog(
        /** Catching the event of clicking out of the dialog and dismiss it */
        onDismissRequest = onDialogDismissed
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = Dimension.surfaceElevation, shape = MaterialTheme.shapes.medium)
                .clip(shape = MaterialTheme.shapes.medium)
                .background(MaterialTheme.colors.surface)
                .padding(Dimension.pagePadding)
        ) {

            /** The item to show as an outer item for discountCategories spinner */
            var menuDefaultItem =
                discountCategories.firstOrNull { it.value > 0 } ?: discountCategories.first()

            /** Control showing/hiding the discount types dropdown menu */
            var isMenuOpened by remember { mutableStateOf(false) }

            /** Dialog's header */
            Text(
                text = stringResource(R.string.add_discount),
                style = MaterialTheme.typography.body2.copy(fontSize = FontSize.lg),
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.8f),
            )
            Spacer(modifier = Modifier.height(Dimension.smLineMargin))
            Text(
                text = stringResource(R.string.overall_discount_slug),
                style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.Normal),
                color = MaterialTheme.colors.secondaryVariant
            )
            Spacer(modifier = Modifier.height(Dimension.lgLineMargin))

            /** Discounts type spinner */
            Box {
                /** Spinner's outer shape */
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small)
                        .background(lightGray)
                        .clickable { isMenuOpened = !isMenuOpened }
                        .padding(Dimension.sm),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(menuDefaultItem.name),
                        style = MaterialTheme.typography.body2.copy(fontSize = FontSize.lg),
                        color = MaterialTheme.colors.onBackground.copy(alpha = 0.8f),
                    )
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = "dropdown icon"
                    )
                }
                /** Our dropdown menu with its content */
                DropdownMenu(
                    modifier = Modifier
                        .padding(horizontal = Dimension.pagePadding * 2)
                        .fillMaxWidth()
                        .shadow(
                            elevation = Dimension.surfaceElevation,
                            shape = MaterialTheme.shapes.medium
                        )
                        .clip(shape = MaterialTheme.shapes.medium)
                        .background(Color.Transparent),
                    expanded = isMenuOpened,
                    onDismissRequest = { isMenuOpened = false }
                ) {
                    discountCategories.forEach { discount ->
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(lightGray)
                                .clickable {
                                    /** Update the current targeted discount type */
                                    isMenuOpened = !isMenuOpened
                                    menuDefaultItem = discount
                                }
                                .padding(Dimension.sm),
                            text = stringResource(discount.name),
                            style = MaterialTheme.typography.body2.copy(fontSize = FontSize.lg),
                            color = MaterialTheme.colors.onBackground.copy(alpha = 0.8f),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimension.lgLineMargin))
            /** DiscountCategory value input field */
            CustomInputField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = "${menuDefaultItem.value}",
                placeholder = stringResource(R.string.enter_discount_value),
                keyboardType = KeyboardType.Number,
                onValueChange = { discount ->
                    /** New discount value */
                    menuDefaultItem.value = if(discount.isNotEmpty()) discount.toInt() else 0
                },
            )
            Spacer(modifier = Modifier.height(Dimension.mdLineMargin))
            /** Applying button */
            CustomButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = Dimension.elevation, shape = MaterialTheme.shapes.small)
                    .clip(MaterialTheme.shapes.small),
                buttonColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary,
                text = stringResource(R.string.apply),
                onButtonClicked = {
                    /** On apply button clicked , updated our current discount by passing up discount category's index and its new value */
                    onDiscountAdded(
                        discountCategories.indexOf(menuDefaultItem),
                        menuDefaultItem.value
                    )
                }
            )
            Spacer(modifier = Modifier.height(Dimension.lgLineMargin))
            /** Removing current discount */
            CustomButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small),
                buttonColor = Color.Transparent,
                contentColor = MaterialTheme.colors.primary,
                elevationEnabled = false,
                text = stringResource(R.string.remove_discount),
                onButtonClicked = {
                    /** On remove discount button clicked , delete our current discount */
                    onDiscountRemoved(discountCategories.indexOf(menuDefaultItem))
                }
            )
        }
    }
}

@Composable
fun PaymentItem(
    modifier: Modifier,
    method: PaymentMethod,
    current: Boolean,
    onPaymentChange: () -> Unit,
) {
    var readyToDraw by remember { mutableStateOf(false) }
    val style = MaterialTheme.typography.body1
    var textStyle by remember { mutableStateOf(style) }
    Text(
        onTextLayout = {
            if (it.didOverflowWidth) {
                textStyle = textStyle.copy(fontSize = textStyle.fontSize * 0.9)
            } else {
                readyToDraw = true
            }
        },
        modifier = modifier
            .padding(Dimension.hoverEffectPadding)
            .clip(MaterialTheme.shapes.medium)
            .border(
                width = if (current) 2.dp else Dimension.zero,
                color = if (current) MaterialTheme.colors.primary else Color.Transparent,
                shape = MaterialTheme.shapes.medium
            )
            .background(
                if (!method.enabled) MaterialTheme.colors.secondaryVariant.copy(alpha = 0.6f)
                else MaterialTheme.colors.onSecondary
            )
            .clickable {
                if (!current && method.enabled) {
                    /** Catching the event of clicking on method which is not the currently selected and also is enabled */
                    onPaymentChange()
                }
            }
            .padding(horizontal = Dimension.pagePadding * 2, vertical = Dimension.pagePadding)
            .drawWithContent {
                if (readyToDraw) {
                    drawContent()
                }
            },
        text = stringResource(id = method.name),
        maxLines = 1,
        softWrap = false,
        style = textStyle
            .copy(
                if (!method.enabled) MaterialTheme.colors.background
                else {
                    if (current) MaterialTheme.colors.primary
                    else MaterialTheme.colors.secondaryVariant
                }
            )
    )
}

@Composable
fun SummarySection(
    scrolling: Boolean,
    net: Double,
    discountPercent: Double,
    discount: Double,
    taxPercent: Double,
    tax: Double,
    onCheckout: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = Dimension.surfaceElevation,
                shape = RoundedCornerShape(topStart = Dimension.md, topEnd = Dimension.md)
            )
            .clip(shape = RoundedCornerShape(topStart = Dimension.md, topEnd = Dimension.md))
            .background(MaterialTheme.colors.surface)
            .padding(Dimension.pagePadding),
    ) {
        Column {
            val overallPrice = net - discount + tax
            if (scrolling) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    SummarySectionItem(
                        scrolling = scrolling,
                        title = buildAnnotatedString {
                            withStyle(
                                style = MaterialTheme.typography.body1.copy(
                                    color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f)
                                ).toSpanStyle()
                            ){
                                append(stringResource(id = R.string.net))
                            }
                        },
                        value = stringResource(id = R.string.x_aed, net),
                        tint = MaterialTheme.colors.secondary
                    )
                    SummarySectionItem(
                        modifier = Modifier,
                        scrolling = scrolling,
                        title = buildAnnotatedString {
                            withStyle(
                                style = MaterialTheme.typography.body1.copy(
                                    color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f)
                                ).toSpanStyle()
                            ){
                                append("${stringResource(R.string.discount)}(")
                            }
                            withStyle(
                                MaterialTheme.typography.body1.copy(
                                    color = MaterialTheme.colors.secondary).toSpanStyle()
                            ){
                                append("${discountPercent.toInt()}%")
                            }
                            withStyle(
                                style = MaterialTheme.typography.body1.copy(
                                    color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f)
                                ).toSpanStyle()
                            ){
                                append(")")
                            }
                        },
                        value = stringResource(id = R.string.x_aed, discount),
                        tint = MaterialTheme.colors.secondaryVariant.copy(alpha = 0.7f)
                    )
                    SummarySectionItem(
                        scrolling = scrolling,
                        title = buildAnnotatedString {
                            withStyle(
                                style = MaterialTheme.typography.body1.copy(
                                    color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f)
                                ).toSpanStyle()
                            ){
                                append("${stringResource(R.string.tax)}(")
                            }
                            withStyle(
                                MaterialTheme.typography.body1.copy(
                                    color = MaterialTheme.colors.primary).toSpanStyle()
                            ){
                                append("${taxPercent.toInt()}%")
                            }
                            withStyle(
                                style = MaterialTheme.typography.body1.copy(
                                    color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f)
                                ).toSpanStyle()
                            ){
                                append(")")
                            }
                        },
                        value = stringResource(id = R.string.x_aed, tax),
                        tint = MaterialTheme.colors.secondaryVariant.copy(alpha = 0.7f)
                    )
                }
            }
            else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                }
            }
            Spacer(modifier = Modifier.height(Dimension.lgLineMargin))
            /** Checkout button, tell the user to pay the required price after discount and tax */
            CustomButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = Dimension.elevation,
                        shape = MaterialTheme.shapes.small,
                    )
                    .clip(MaterialTheme.shapes.small),
                enabled = true,
                elevationEnabled = true,
                buttonColor = MaterialTheme.colors.secondary,
                contentColor = MaterialTheme.colors.onSecondary,
                text = stringResource(R.string.checkout_pay, "$overallPrice"),
                textStyle = MaterialTheme.typography.h3,
                onButtonClicked = {
                    /** Catching checkout event */
                    onCheckout()
                }
            )
        }
    }
}

/** summary section item - which could be as column or a row depending on whether or not the user is scrolling */
@Composable
fun SummarySectionItem(
    modifier: Modifier = Modifier,
    scrolling: Boolean,
    title: AnnotatedString,
    value: String,
    tint: Color,
) {
    if (scrolling) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /** The title */
            Text(
                text = title,
            )
            Spacer(modifier = Modifier.height(Dimension.mdLineMargin))
            /** Then the value */
            Text(
                text = value,
                style = MaterialTheme.typography.subtitle1.copy(color = tint),
            )
        }
    }
    //    else {
//        Row(
//            modifier = modifier.padding(vertical = Dimension.xs),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            /** The title */
//            Text(
//                text = title,
//                style = MaterialTheme.typography.body1,
//                color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f)
//            )
//            /** Then the value */
//            Text(
//                text = value,
//                style = MaterialTheme.typography.subtitle1,
//                color = tint,
//            )
//        }

}
@Composable
fun CheckoutResultDialog(
    overallPrice: Double,
    paymentMethod: PaymentMethod,
    onDialogDismissed: ()-> Unit,
    onRollback: ()-> Unit,
) {
    Dialog(
        onDismissRequest = onDialogDismissed,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(modifier = Modifier.fillMaxWidth()){
                /** Dialog's body */
                Column(
                    modifier = Modifier
                        .padding(top = Dimension.lg)
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colors.surface)
                        .padding(Dimension.pagePadding)
                ){
                    /** Result's header with auto hide progress */
                    CheckoutResultHeader(
                        onTimerFinished = onDialogDismissed
                    )
                    /** Date and time section */
                    CheckoutResultTimeSection()
                    Spacer(modifier = Modifier.height(Dimension.lgLineMargin))
                    /** Amount section */
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ){
                        Column {
                            Text(
                                text = stringResource(R.string.amount),
                                style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.secondaryVariant)
                            )
                            Text(
                                text = "$overallPrice ${stringResource(id = R.string.aed_currency)}",
                                style = MaterialTheme.typography.h3
                            )
                        }
                        Text(
                            text = stringResource(R.string.completed),
                            style = MaterialTheme.typography.subtitle1,
                            color = MaterialTheme.colors.secondaryVariant,
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    shape = MaterialTheme.shapes.small,
                                    color = MaterialTheme.colors.secondaryVariant
                                )
                                .padding(horizontal = Dimension.xs,
                                    vertical = Dimension.surfaceElevation)
                        )
                    }
                    /** Payment method used */
                    Row(
                        modifier = Modifier.padding(end = Dimension.pagePadding),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val painter = when (paymentMethod.id) {
                            1 -> painterResource(id = R.drawable.ic_cash)
                            2 -> painterResource(id = R.drawable.ic_bank)
                            else -> painterResource(id = R.drawable.nfc_card)
                        }
                        val payment = when (paymentMethod.id) {
                            1 -> stringResource(id = R.string.cash)
                            2 -> stringResource(id = R.string.bank)
                            else -> stringResource(id = R.string.nfc)
                        }
                        Icon(
                            modifier = Modifier
                                .size(Dimension.smIconSize)
                                .padding(end = 5.dp),
                            painter = painter,
                            contentDescription = "payment",
                            tint = MaterialTheme.colors.secondary,
                        )
                        /** Payment Type Text*/
                        Text(
                            text = payment,
                            style = MaterialTheme.typography.subtitle1.copy(color = MaterialTheme.colors.secondaryVariant)
                        )
                    }
                    Spacer(modifier = Modifier.height(Dimension.lgLineMargin))
                    /** Done button */
                    CustomButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(elevation = Dimension.elevation,
                                shape = MaterialTheme.shapes.small)
                            .clip(MaterialTheme.shapes.small),
                        buttonColor = MaterialTheme.colors.secondary,
                        contentColor = MaterialTheme.colors.onSecondary,
                        text = stringResource(R.string.done),
                        onButtonClicked = onDialogDismissed
                    )
                    Spacer(modifier = Modifier.height(Dimension.smLineMargin))
                    /** Rollback button */
                    CustomButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.small),
                        buttonColor = Color.Transparent,
                        contentColor = MaterialTheme.colors.secondaryVariant,
                        elevationEnabled = false,
                        text = stringResource(R.string.rollback),
                        onButtonClicked = onRollback
                    )
                }
                /** check mark icon on  the top */
                Icon(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .shadow(
                            elevation = Dimension.surfaceElevation,
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .background(MaterialTheme.colors.surface)
                        .padding(Dimension.xs)
                        .size(Dimension.xl),
                    imageVector = Icons.Rounded.Check,
                    contentDescription = "done",
                    tint = MaterialTheme.colors.secondary
                )
            }
            Spacer(modifier = Modifier.height(Dimension.mdLineMargin))
            /** Close icon */
            Icon(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.background)
                    .clickable {
                        /** Consider closing as timer out */
                        onDialogDismissed()
                    }
                    .padding(Dimension.xs)
                    .size(Dimension.mdIconSize),
                imageVector = Icons.Rounded.Close,
                contentDescription = "close",
                tint = MaterialTheme.colors.onBackground
            )
        }
    }
}

@Composable
fun CheckoutResultHeader(
    onTimerFinished: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimension.xs)
    ){
        /** Auto hide timer progress */
        var progress by remember { mutableStateOf(1f) }
        val animatedProgress by animateFloatAsState(
            targetValue = progress,
            animationSpec = TweenSpec(
                durationMillis = 5000,
                easing = LinearEasing
            ),
            finishedListener = {
                /** pass the completion event */
                onTimerFinished()
            }
        ).also {
            /** Start the timer's animation */
            progress = 0f
        }

        /** Successful transaction message */
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(Dimension.xs),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.thank_you),
                style = MaterialTheme.typography.body1.copy(fontSize = FontSize.lg),
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.8f),
            )
            Spacer(modifier = Modifier.height(Dimension.pagePadding))
            Text(
                text = stringResource(R.string.successful_transaction),
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.secondaryVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(Dimension.pagePadding))
            /** Image Dots*/
            Image(
                modifier = Modifier
                    .padding(vertical = Dimension.pagePadding)
                    .fillMaxWidth()
                    .height(2.dp),
                painter = painterResource(id = R.drawable.dots),
                contentDescription = "Dots Image"
            )
        }
        /** The timer */
        CircularProgressIndicator(
            progress = animatedProgress,
            color = MaterialTheme.colors.primary,
            strokeWidth = 3.dp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(Dimension.smIconSize)
        )
    }
}

@Composable
fun CheckoutResultTimeSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ){
        val date = Date().getFormattedDate("yyyy-MM-dd HH:mm").replace(" ", "T")
        /** Show the date */
        HeaderWithText(
            header = stringResource(R.string.date),
            text = date.split("T").first().getStructuredDate(),
        )
        /** Now show the time */
        HeaderWithText(
            header = stringResource(id = R.string.time),
            text = date.split("T").last().get12SystemHour(),
        )
    }
}

@Composable
fun HeaderWithText(
    header: String,
    text: String,
) {
    Column {
        Text(
            text = header ,
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.secondaryVariant,
        )
        Text(
            text = text,
            style = MaterialTheme.typography.h4,
            color = MaterialTheme.colors.onBackground,
        )
    }
}