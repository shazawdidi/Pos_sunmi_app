package com.altkamul.xpay.views.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.altkamul.xpay.R
import com.altkamul.xpay.model.TransactionItem
import com.altkamul.xpay.ui.theme.*
import com.altkamul.xpay.viewmodel.ClaimViewModel

/** This Function Will Draw Transaction Items List*/
@Composable
fun ShowTransactionItems(
    showTransactionItems: MutableState<Boolean>,
    claimViewModel: ClaimViewModel
) {
    /** List For Test*/
    val list = listOf<TransactionItem>()
    /** Parent Layout For Transparent Background With Some Shadow*/
    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xD1424242))
            .padding(Dimension.pagePadding)
    ) {
        /** Icon For Canceling Pop Up Transaction Screen*/
        Icon(
            imageVector = Icons.Filled.Cancel,
            contentDescription = "cancel icon",
            tint = Color.White,
            modifier = Modifier
                .clickable {
                    /** Clickable Icon For Canceling Pop Up Screen*/
                    showTransactionItems.value = !showTransactionItems.value
                }
                .padding(bottom = 20.dp)
        )

        /** Card For Items*/
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = Dimension.pagePadding, bottom = Dimension.pagePadding),
            elevation = 2.dp,
            shape = RoundedCornerShape(6.dp),
            backgroundColor = Color(0xFFededed)
        ) {
            /** Lazy Column For Scrolling Between Items*/
            LazyColumn(Modifier.fillMaxSize()) {
                items(list) { transactionItem ->
                    /** Transaction Item Layout*/
                    TransactionItem(
                        itemName = transactionItem.itemName,
                        quantity = transactionItem.qty.toString()
                    )
                }

            }
        }

    }
}

/** This Function Will Draw Transaction Item Layout it Tacks Transaction Item Object Instance */
@Composable
fun TransactionItem(itemName: String = "Orange", quantity: String = "1") {
    /** Variable for control Column Border is Selected Or Not*/
    var columnBorderColor by remember {
        mutableStateOf(false)
    }
    /** Column Parent Layout With Border*/
    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .background(shape = RoundedCornerShape(10.dp), color = Color(0xFFf7f7f7))
            .border(
                width = 3.dp,
                /** The Condition For Displaying Border If Is Selected*/
                color = if (columnBorderColor) orange else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .height(120.dp)
            .padding(top = 8.dp, bottom = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        /** Text For Displaying Item Name*/
        Text(
            text = "Item Name: $itemName",
            fontSize = FontSize.md,
            fontWeight = FontWeight.Bold,
            color = darkGray,
            fontFamily = FontFamily(
                Font(
                    R.font.montserrat_normal,
                    FontWeight.Bold,
                    FontStyle.Normal
                )
            )
        )
        /** Row For Displaying Tow Button For Selection And Determined Quantity*/
        Row(
            modifier = Modifier
                .padding(5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            /** Box For Determined Quantity*/
            Box(
                Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .background(shape = RoundedCornerShape(5.dp), color = Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    /** Text For Quantity -> Qy*/
                    Text(
                        modifier = Modifier
                            .weight(0.3f),
                        text = "Qy:",
                        color = gray
                    )
                    /** Text Field Value*/
                    val textFieldValue = remember {
                        mutableStateOf(quantity)
                    }
                    /** Text Field For Entering Quantity*/
                    CustomTextField(textFieldValue)
                }
            }

            /** Box For Claiming Process*/
            Box(
                Modifier
                    .height(TextFieldDefaults.MinHeight)
                    .weight(0.5f)
                    .background(shape = RoundedCornerShape(5.dp), color = orange)
                    .border(
                        width = 3.dp,
                        color = Color.Transparent,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .clickable {
                        columnBorderColor = !columnBorderColor
                    }, contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Claim",
                    color = Color.White
                )
            }
        }
    }
}

/** Custom Text Field*/
@Composable
fun RowScope.CustomTextField(textFieldValue: MutableState<String>) {
    /** Text Field For Enter Quantity*/
    TextField(
        modifier = Modifier
            .weight(0.7f),
        value = textFieldValue.value,
        onValueChange = {
            textFieldValue.value = it
        },
        textStyle = TextStyle(color = Color.Black, fontSize = FontSize.md),
        placeholder = {
            Text(
                modifier = Modifier
                    .fillMaxSize(), textAlign = TextAlign.Center,
                text = textFieldValue.value, fontWeight = FontWeight.Medium,
                fontSize = FontSize.md, color = Color(0xFFaaaaaa)
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color(0xFFededed),
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}