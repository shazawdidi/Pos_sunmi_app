package com.altkamul.xpay.views.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.altkamul.xpay.R
import com.altkamul.xpay.ui.theme.Dimension
import com.altkamul.xpay.ui.theme.red
import com.altkamul.xpay.ui.theme.transGray

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Printer(
    modifier: Modifier = Modifier,
    onClicked: () -> Unit,
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(transGray)
            .border(
                width = 2.dp,
                shape = MaterialTheme.shapes.small,
                color = Color.Transparent
            )
            .clickable {
                onClicked()
            }
            .padding(Dimension.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Print,
            contentDescription = "Print Test Icon",
            tint = red,
            modifier = Modifier.size(Dimension.lgLineMargin)
        )
        Spacer(modifier = Modifier.width(Dimension.xs))
        DynamicText(
            text = stringResource(R.string.print_test),
            style = MaterialTheme.typography.body2
        )


    }
}

@Composable
fun NfcTester(
    modifier: Modifier = Modifier,
    onClicked: ()-> Unit,
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(transGray)
            .border(
                width = 2.dp,
                shape = MaterialTheme.shapes.small,
                color = Color.Transparent
            )
            .clickable {
                onClicked()
            }
            .padding(Dimension.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Nfc,
            contentDescription = "Print Test  Icon",
            tint = red,
            modifier = Modifier.size(Dimension.lgLineMargin)
        )
        Spacer(modifier = Modifier.width(Dimension.xs))
        DynamicText(
            text = stringResource(R.string.nfc_test),
            style = MaterialTheme.typography.body2
        )


    }
}

@Composable
fun CustomerScreen(
    modifier: Modifier = Modifier,
    onClicked: ()-> Unit,
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(transGray)
            .fillMaxWidth()
            .border(
                width = 2.dp,
                shape = MaterialTheme.shapes.small,
                color = Color.Transparent
            )
            .clickable {
                onClicked()
            }
            .padding(Dimension.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Dashboard,
            contentDescription = "Print Test  Icon",
            tint = red,
            modifier = Modifier.size(Dimension.lgLineMargin)
        )
        Spacer(modifier = Modifier.width(Dimension.xs))
        DynamicText(
            text = stringResource(R.string.customer_screen),
            style = MaterialTheme.typography.body2
        )


    }
}

@Composable
fun QrMakerTester(
    modifier: Modifier = Modifier,
    onClicked: ()-> Unit,
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(transGray)
            .border(
                width = 2.dp,
                shape = MaterialTheme.shapes.small,
                color = Color.Transparent
            )
            .clickable {
                onClicked()
            }
            .padding(Dimension.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.QrCodeScanner,
            contentDescription = "Print Test  Icon",
            tint = red,
            modifier = Modifier.size(Dimension.lgLineMargin)
        )
        Spacer(modifier = Modifier.width(Dimension.smLineMargin))
        DynamicText(
            text = stringResource(R.string.qr_maker),
            style = MaterialTheme.typography.body2
        )
    }
}

@Composable
fun QrReaderTester(
    modifier: Modifier = Modifier,
    onClicked: ()-> Unit,
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(transGray)
            .border(
                width = 2.dp,
                shape = MaterialTheme.shapes.small,
                color = Color.Transparent
            )
            .clickable {
                onClicked()
            }
            .padding(Dimension.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.QrCode,
            contentDescription = "Print Test  Icon",
            tint = red,
            modifier = Modifier.size(Dimension.lgLineMargin)
        )
        Spacer(modifier = Modifier.width(Dimension.smLineMargin))
        DynamicText(
            text = stringResource(R.string.qr_reader),
            style = MaterialTheme.typography.body2
        )

    }


}

@Composable
fun InternetTester(
    modifier: Modifier = Modifier,
    onClicked: ()-> Unit,
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(transGray)
            .fillMaxWidth()
            .border(
                width = 2.dp,
                shape = MaterialTheme.shapes.small,
                color = Color.Transparent
            )
            /** TODO("Till Check Network Speed In Future)*/
            .clickable {
                onClicked()
            }
            .padding(Dimension.sm),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Speed,
            contentDescription = "Print Test  Icon",
            tint = red,
            modifier = Modifier.size(Dimension.lgLineMargin)
        )
        Spacer(modifier = Modifier.width(Dimension.xs))
        DynamicText(
            text = stringResource(R.string.internet_test),
            style = MaterialTheme.typography.body2
        )


    }
}


