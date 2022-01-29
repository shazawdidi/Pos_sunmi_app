package com.altkamul.xpay.views.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.altkamul.xpay.R
import com.altkamul.xpay.ui.theme.Dimension
import com.altkamul.xpay.ui.theme.XPayAndroidTheme
import com.altkamul.xpay.ui.theme.lightGray
import com.altkamul.xpay.utils.decreaseBy
import com.altkamul.xpay.utils.mirror

@Composable
fun TransactionItemLayout(
    modifier: Modifier = Modifier,
    title: String = "Receipt No. 34343843",
    itemName: String = "Item Name",
    qty: Int = 10,
    discount: Double = 10.0,
    totalPrice: Double = 1500.0
) {
    XPayAndroidTheme {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium),
            shape = MaterialTheme.shapes.medium,
            elevation = Dimension.surfaceElevation
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(Dimension.md),
            ) {

                /** receipt or transaction number.... Text*/
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    text = title,
                    style = MaterialTheme.typography.h3.copy(color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)),
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
                Text(
                    text = itemName,
                    style = MaterialTheme.typography.h3,
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ColumnText(
                        header = stringResource(id = R.string.quantity),
                        headerStyle = MaterialTheme.typography.subtitle1
                            .copy(color = MaterialTheme.colors.secondaryVariant),
                        body = "$qty"
                    )
                    ColumnText(
                        header = stringResource(id = R.string.discount),
                        headerStyle = MaterialTheme.typography.subtitle1
                            .copy(color = MaterialTheme.colors.secondaryVariant), body = "$discount"
                    )
                }
                Spacer(modifier = Modifier.height(Dimension.pagePadding))
                ColumnText(
                    header = stringResource(id = R.string.total),
                    headerStyle = MaterialTheme.typography.subtitle1
                        .copy(color = MaterialTheme.colors.secondaryVariant),
                    body = "$totalPrice"
                )
            }
        }
    }
}

@Composable
fun CashiersSelector(
    value: String = "Cashier name",
    items: List<String>,
    background: Color = lightGray.copy(alpha = 0.5f),
    contentColor: Color = MaterialTheme.colors.secondaryVariant,
    onValueChanged: (index: Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val icon = if(expanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown

    Box(modifier = Modifier.fillMaxWidth()){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(background)
                .clickable {
                    expanded = true
                }
                .padding(Dimension.pagePadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.body1,
                color = contentColor
            )
            Icon(
                modifier = Modifier.mirror(),
                imageVector = icon,
                contentDescription = null,
                tint = contentColor.copy(alpha = 0.7f),
            )
        }
        MenuWithTitles(
            expanded = expanded,
            currentIndex = items.indexOf(value),
            titles = items,
            backgroundColor = MaterialTheme.colors.surface,
            contentColor = MaterialTheme.colors.onSurface,
            onItemSelected = {
                onValueChanged(it)
                expanded = false
            },
            onDismiss = {
                expanded = !expanded
            }
        )
    }
}