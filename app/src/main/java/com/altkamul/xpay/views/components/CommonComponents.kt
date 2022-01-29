package com.altkamul.xpay.views.components

import android.widget.CalendarView
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.altkamul.xpay.R
import com.altkamul.xpay.model.response.TransactionPayment
import com.altkamul.xpay.ui.theme.Dimension
import com.altkamul.xpay.ui.theme.FontSize
import com.altkamul.xpay.ui.theme.lightGray
import com.altkamul.xpay.utils.get12SystemHour
import com.altkamul.xpay.utils.getFormattedDate
import com.altkamul.xpay.utils.getStructuredDate
import com.altkamul.xpay.utils.mirror
import timber.log.Timber
import java.util.*
import kotlin.math.ceil

@Composable
fun <T> CustomLazyGrid(
    modifier: Modifier = Modifier,
    items: List<T>,
    gridSpan: Int,
    scrollable: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(horizontal = Dimension.pagePadding),
    content: @Composable (width: Dp, item: T) -> Unit,
) {
    val modifierWithScroll =
        if (scrollable) modifier.verticalScroll(state = rememberScrollState()) else modifier

    Column(
        modifier = modifierWithScroll
            .fillMaxWidth()
            .padding(contentPadding)
    ) {
        /** Building item's grid manually */
        val itemsCount = items.size

        /** Calculate how many rows we are gonna have */
        val rowCount = ceil((itemsCount / gridSpan.toDouble())).toInt()
        /** Building our grid's rows */
        for (currentRow in 0 until rowCount) {
            val startIndex = currentRow * gridSpan
            var lastIndex = startIndex + gridSpan
            while (lastIndex > itemsCount) {
                /** We must ensure that this #lastIndex in our list bounds to avoid #ArrayOutOfBoundException */
                lastIndex--
            }
            val rowItems = items.slice(startIndex until lastIndex)
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth()
            ) {
                /** Getting screen's width */
                val screenWidth = maxWidth
                Row(
                    modifier = Modifier
                        .padding(bottom = Dimension.pagePadding)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        if (gridSpan > 1) Dimension.pagePadding.div(
                            2
                        ) else Dimension.zero
                    )
                ) {
                    rowItems.forEach { item ->
                        content(
                            (screenWidth - (Dimension.pagePadding.div(2) * (gridSpan - 1))) / gridSpan,
                            item
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SecondaryTobBar(onBackPressed: () -> Unit) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .padding(horizontal = Dimension.pagePadding, vertical = Dimension.hoverEffectPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .clickable { onBackPressed() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "left arrow",
                tint = MaterialTheme.colors.primary,
                modifier = Modifier
                    .mirror()
                    .size(Dimension.smIconSize),
            )
            Spacer(modifier = Modifier.width(Dimension.xs))
            Text(
                text = stringResource(R.string.back),
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.primary,
            )
        }
    }
}

@Composable
fun TransactionCardLayout(
    trxModifier: Modifier = Modifier,
    date: String,
    time: String,
    amount: String,
    payments: List<TransactionPayment>,
    selected: Boolean = false,
    onClick: () -> Unit,
    title: String,
) {
    var modifier = trxModifier
        .padding(Dimension.surfaceElevation)
        .fillMaxWidth()

    if (selected) {
        modifier = modifier.border(
            width = Dimension.surfaceElevation,
            color = MaterialTheme.colors.secondary,
            shape = MaterialTheme.shapes.medium
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.background)
    ){
        Card(
            modifier = modifier
                .clip(MaterialTheme.shapes.medium)
                .clickable { onClick() },
            backgroundColor = MaterialTheme.colors.surface,
            shape = MaterialTheme.shapes.medium,
            elevation = Dimension.surfaceElevation
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(Dimension.pagePadding),
            ) {
                /** receipt or transaction number.... Text*/
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    text = title,
                    style = MaterialTheme.typography.h3.copy(
                        color = MaterialTheme.colors.onSurface.copy(
                            alpha = 0.6f
                        )
                    ),
                    textAlign = TextAlign.Center,
                )
                /** Image Dots*/
                Image(
                    modifier = Modifier
                        .padding(vertical = Dimension.pagePadding)
                        .fillMaxWidth()
                        .height(2.dp),
                    painter = painterResource(id = R.drawable.dots),
                    contentDescription = "Dots Image"
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ColumnText(
                        header = stringResource(id = R.string.date),
                        headerStyle = MaterialTheme.typography.subtitle1.copy(color = MaterialTheme.colors.secondaryVariant),
                        body = date.getStructuredDate()
                    )
                    ColumnText(
                        header = stringResource(id = R.string.time),
                        headerStyle = MaterialTheme.typography.subtitle1.copy(color = MaterialTheme.colors.secondaryVariant),
                        body = time.take(5).get12SystemHour()
                    )
                }
                Spacer(modifier = Modifier.height(Dimension.pagePadding))
                ColumnText(
                    header = stringResource(id = R.string.amount),
                    headerStyle = MaterialTheme.typography.subtitle1
                        .copy(color = MaterialTheme.colors.secondaryVariant),
                    body = amount,
                    bodyStyle = MaterialTheme.typography.h3,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    payments.forEach {
                        Row(
                            modifier = Modifier.padding(end = Dimension.pagePadding),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val painter = when (it.paymentModeId) {
                                1 -> painterResource(id = R.drawable.ic_cash)
                                2 -> painterResource(id = R.drawable.ic_bank)
                                else -> painterResource(id = R.drawable.nfc_card)
                            }
                            val payment = when (it.paymentModeId) {
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
                    }
                }

            }
        }
        if(selected){
            Icon(painter = painterResource(id = R.drawable.ic_check_circle),
                contentDescription = null,
                tint = MaterialTheme.colors.secondary,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.surface)
                    .padding(Dimension.surfaceElevation)
                    .size(Dimension.mdIconSize)
            )
        }
    }
}

@Composable
fun ColumnText(
    header: String,
    body: String,
    headerStyle: TextStyle = MaterialTheme.typography.h3,
    bodyStyle: TextStyle = MaterialTheme.typography.body1,
) {
    Column {
        Text(
            text = header,
            style = headerStyle,
        )
        Text(
            text = body,
            style = bodyStyle,
        )
    }
}

@Composable
fun CustomInputField(
    modifier: Modifier = Modifier,
    value: String,
    placeholder: String,
    textColor: Color = MaterialTheme.colors.onBackground,
    backgroundColor: Color = lightGray.copy(alpha = 0.3f),
    requireSingleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    onValueChange: (string: String) -> Unit,
) {
    TextField(
        modifier = modifier
            .fillMaxWidth(),
        value = value,
        onValueChange = {
            /** when the value change , it pass it up to MainTopBar() **/
            onValueChange(it)
        },
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.body2,
                color = textColor.copy(alpha = 0.5f)
            )
        }, isError = isError,
        visualTransformation = visualTransformation,
        singleLine = requireSingleLine,
        shape = MaterialTheme.shapes.small,
        colors = TextFieldDefaults.textFieldColors(
            textColor = textColor,
            backgroundColor = backgroundColor,
            cursorColor = MaterialTheme.colors.secondary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        textStyle = MaterialTheme.typography.body2,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        )
    )
}

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    elevationEnabled: Boolean = true,
    buttonColor: Color,
    contentColor: Color,
    text: String,
    trailingIcon: @Composable () -> Unit = {},
    onButtonClicked: () -> Unit,
    textStyle: TextStyle = MaterialTheme.typography.button,
) {
    Button(
        modifier = modifier,
        onClick = {
            onButtonClicked()
        },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = buttonColor,
            contentColor = contentColor,
            disabledBackgroundColor = MaterialTheme.colors.background,
            disabledContentColor = MaterialTheme.colors.onBackground,
        ),
        enabled = enabled,
        contentPadding = PaddingValues(vertical = Dimension.sm),
        elevation = if (elevationEnabled) ButtonDefaults.elevation()
        else ButtonDefaults.elevation(
            defaultElevation = Dimension.zero,
            pressedElevation = Dimension.elevation
        )
    ) {
        Text(
            text = text,
            style = textStyle
        )
        /** Add trailing icon */
        trailingIcon()
    }
}

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (value: String) -> Unit,
    placeHolder: String,
    trailingIcon: ImageVector?,
) {
    Row(
        modifier = modifier
            .clip(shape = MaterialTheme.shapes.small)
            .background(lightGray)
            .padding(
                end = Dimension.xs
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        TextField(
            modifier = modifier.weight(1f),
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeHolder,
                    style = MaterialTheme.typography.body2
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType
            ),
            shape = MaterialTheme.shapes.small,
            colors = TextFieldDefaults.textFieldColors(
                textColor = MaterialTheme.colors.onBackground,
                backgroundColor = Color.Transparent,
                cursorColor = MaterialTheme.colors.secondary,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            textStyle = MaterialTheme.typography.body2,
        )
        if (trailingIcon != null) {
            Icon(
                modifier = Modifier
                    .background(Color.Transparent)
                    .mirror()
                    .size(Dimension.mdIconSize),
                imageVector = trailingIcon,
                contentDescription = "icon",
                tint = MaterialTheme.colors.secondaryVariant,
            )
        }
    }
}

@Composable
fun DatePicker(
    modifier: Modifier = Modifier,
    currentPicked: Long,
    icon: ImageVector = Icons.Rounded.DateRange,
    placeholder: String,
    background: Color = lightGray.copy(alpha = 0.5f),
    contentColor: Color = MaterialTheme.colors.secondaryVariant,
    onDateUpdated: (date: Long) -> Unit,
) {
    var dateDialogShown by remember { mutableStateOf(false) }
    if (dateDialogShown) {
        DateDialog(
            currentPicked = currentPicked,
            background = MaterialTheme.colors.surface,
            updatedDate = {
                dateDialogShown = false
                onDateUpdated(it)
            },
            onDismissed = {
                dateDialogShown = false
            },
            onCleared = {
                dateDialogShown = false
                onDateUpdated(0L)
            }
        )
    }
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(background)
            .clickable {
                dateDialogShown = true
            }
            .padding(Dimension.pagePadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        val calendar =
            Calendar.getInstance().also { if (currentPicked > 0L) it.timeInMillis = currentPicked }
        val datePicked = calendar.time.getFormattedDate("yyyy-MM-dd")
        Text(
            text = if (currentPicked > 0L) datePicked else placeholder,
            style = MaterialTheme.typography.body1,
            color = contentColor.copy(alpha = if (currentPicked > 0) 1f else 0.7f)
        )
        Spacer(modifier = Modifier.width(Dimension.pagePadding))
        Icon(
            modifier = Modifier.mirror(),
            imageVector = icon,
            contentDescription = null,
            tint = contentColor.copy(alpha = 0.7f),
        )
    }
}

@Composable
fun DateDialog(
    currentPicked: Long,
    background: Color,
    updatedDate: (Long) -> Unit,
    onDismissed: () -> Unit,
    onCleared: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissed) {
        Column(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(background)
        ) {
            AndroidView(
                factory = { context ->
                    Timber.d("Old Current picked is $currentPicked")
                    CalendarView(context).also {
                        it.maxDate = Calendar.getInstance().timeInMillis
                        it.date =
                            if (currentPicked == 0L) Calendar.getInstance().timeInMillis else currentPicked
                    }
                },
                modifier = Modifier
                    .wrapContentWidth(),
                update = { view ->
                    view.setOnDateChangeListener { calendarView, day, month, year ->
                        Timber.d("new Current picked is ${calendarView.date}")
                        Calendar.getInstance().run {
                            set(day, month, year)
                            updatedDate(timeInMillis)
                        }
                    }
                }
            )
            Text(
                modifier = Modifier
                    .padding(start = Dimension.pagePadding, bottom = Dimension.pagePadding)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { onCleared() }
                    .padding(Dimension.xs),
                text = stringResource(R.string.clear),
                style = MaterialTheme.typography.body1,
            )
        }
    }
}


@Composable
fun MenuWithTitles(
    expanded: Boolean,
    currentIndex: Int,
    titles: List<String>,
    backgroundColor: Color = MaterialTheme.colors.surface,
    contentColor: Color = MaterialTheme.colors.onSurface,
    onItemSelected: (index: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    DropdownMenu(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor),
        expanded = expanded,
        onDismissRequest = onDismiss,
        offset = DpOffset(x = Dimension.pagePadding, y = Dimension.pagePadding),
    ) {
        titles.forEachIndexed { index, title ->
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .clickable { onItemSelected(index) }
                    .padding(all = Dimension.pagePadding),
                text = title,
                color = if (currentIndex == index) MaterialTheme.colors.primary else contentColor,
                style = MaterialTheme.typography.body2
            )
        }
    }
}

/** This FUnction For Showing Dialog In User Screen */
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

@Composable
fun DynamicText(
    text: String,
    style: TextStyle = MaterialTheme.typography.body1,
) {
    var dynamicStyle by remember { mutableStateOf(style) }
    var readyToDraw by remember{ mutableStateOf(false) }

    Text(
        modifier = Modifier.drawWithContent {
            if(readyToDraw) drawContent()
        },
        text = text,
        onTextLayout = {
           if(it.didOverflowWidth){
               dynamicStyle = dynamicStyle.copy(fontSize = dynamicStyle.fontSize * 0.9f)
           } else {
               readyToDraw = true
           }
        },
        softWrap = false,
        maxLines = 1,
        style = dynamicStyle,
    )
}

@Composable
fun HoverIcon(
    modifier: Modifier = Modifier,
    tint: Color,
    onClicked: () -> Unit,
    painter: Painter,
    shape: Shape = CircleShape,
    background: Color = MaterialTheme.colors.surface,
    iconSize: Dp = Dimension.smIconSize,
) {
    Box(
        modifier = modifier
            .shadow(elevation = 2.dp, shape = CircleShape)
            .clip(shape)
            .background(background)
            .clickable { onClicked() }
            .padding(Dimension.hoverEffectPadding)
    ) {
        Icon(
            modifier = Modifier
                .clip(CircleShape)
                .size(iconSize)
                .align(Alignment.Center),
            painter = painter,
            contentDescription = "icon",
            tint = tint,
        )
    }
}