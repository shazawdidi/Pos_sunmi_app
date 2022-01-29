package com.altkamul.xpay.views.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.PopupProperties
import coil.compose.rememberImagePainter
import com.altkamul.xpay.R
import com.altkamul.xpay.model.MenuOptionItem
import com.altkamul.xpay.sealed.Screen
import com.altkamul.xpay.ui.theme.Dimension
import com.altkamul.xpay.ui.theme.lightGray
import com.altkamul.xpay.ui.theme.white
import com.altkamul.xpay.utils.LoggedMerchantPref
import com.altkamul.xpay.utils.decreaseBy


/** Login topBar that contain the Login title & the option menu button */

@Composable
fun LoginTopBar(
    expanded: Boolean = false,
    onMenuClicked: ()-> Unit = {},
    onOptionClicked: (option: MenuOptionItem) -> Unit,
    isSupportLoginChecked: Boolean,
) {
    /** Options that appear in login TopBar options menu */
    val options = listOf(
        MenuOptionItem(
            id = 1, icon = R.drawable.ic_change,
            title = stringResource(id =  R.string.change_branch),
            route = Screen.ChangeBranch.route),
        MenuOptionItem(
            id = 2, icon = R.drawable.ic_cash_sign,
            title = stringResource(id = R.string.balance)),
        MenuOptionItem(
            id = 3, icon = R.drawable.ic_lang,
            title = stringResource(id =  R.string.change_lang),
            route = Screen.Languages.route),
        /** This is only option that contains the switch , so will give it a switch's state */
        MenuOptionItem(
            id = 4, icon = R.drawable.ic_user,
            title = stringResource(id =  R.string.support_login),
            isChecked = isSupportLoginChecked),
        MenuOptionItem(
            id = 5, icon = R.drawable.ic_call,
            title = stringResource(id =  R.string.contact_us),
            route = Screen.ContactUs.route),
        MenuOptionItem(
            id = 5, icon = R.drawable.ic_test_center,
            title = stringResource(id =  R.string.test_center),
            route = Screen.TestCenter.route),
        MenuOptionItem(
            id = 6, icon = R.drawable.ic_exit,
            title = stringResource(id =  R.string.exit)
        )
    )
    /** TopAppBar UI */
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.login),
            style = MaterialTheme.typography.h1,
            color = MaterialTheme.colors.secondaryVariant,
        )
        /** A box contain the menu icon - which act its anchor - and the actual menu */
        Box{
            Icon(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(Dimension.mdIconSize)
                    .clickable { onMenuClicked() }
                    .padding(Dimension.hoverEffectPadding),
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = "more icon",
                tint = MaterialTheme.colors.secondaryVariant
            )
            /** Our dear options menu */
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onMenuClicked() },
                modifier = Modifier.fillMaxWidth(0.7f),
                properties = PopupProperties(focusable = true)
            ) {
                /** Menu header */
                DropdownMenuItem(
                    modifier = Modifier
                        .background(lightGray.copy(alpha = 0.3f)),
                    enabled = false,
                    onClick = {}
                ) {
                    Text(
                        text = stringResource(R.string.options),
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f),
                    )
                }
                /** iterate through the menu options */
                options.forEach { option->
                    MyOptionsMenuItem(
                        option = option,
                        onOptionClicked = {
                            /** First hiding the options menu */
                            onMenuClicked()
                            /** Passing the option that is clicked up to login screen so we can handled it */
                            onOptionClicked(option)
                        }
                    )
                }
            }
        }
    }
}

/** Options menu's item layout , contain the icon & the title , and switch if its support login */
@Composable
fun MyOptionsMenuItem(
    option: MenuOptionItem,
    onOptionClicked: () -> Unit,
) {
    DropdownMenuItem(
        modifier = Modifier.background(white),
        onClick = {
            /** Catch clicking on an option event */
            onOptionClicked()
        },
    ) {
        Icon(
            painter = painterResource(id = option.icon),
            contentDescription = option.title,
            tint = MaterialTheme.colors.primary,
            modifier = Modifier
                .padding(end = Dimension.md)
                .size(Dimension.md decreaseBy 0.5f)
        )
        Text(
            modifier = Modifier.weight(1f),
            text = option.title,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onBackground.copy(alpha = 0.5f),
        )
        /** A switch that appear only when the option is support login */
        if (option.title.equals(other = stringResource(id = R.string.support_login),ignoreCase = true)){
            Switch(
                modifier = Modifier.padding(start = Dimension.sm),
                checked = option.isChecked,
                onCheckedChange = { onOptionClicked() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colors.primary,
                    checkedTrackColor = MaterialTheme.colors.primary.copy(alpha = 0.5f),
                    uncheckedThumbColor = MaterialTheme.colors.secondaryVariant,
                    uncheckedTrackColor = MaterialTheme.colors.secondaryVariant.copy(alpha = 0.5f),
                )

            )
        }

    }
}

/** Merchant data section , it contains merchant's image,name and also the branch */

@Composable
fun LoginMerchantDataSection(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = rememberImagePainter(
                /** data is url of the image to be loaded */
                data = LoggedMerchantPref.merchant?.branches?.first()?.images?.defaultlogo ?: "Default merchant profile",
            ),
            contentDescription = "image",
            modifier = Modifier
                .padding(Dimension.hoverEffectPadding)
                .fillMaxSize(0.5f)
                .aspectRatio(1f)
                .clip(CircleShape)
                .background(MaterialTheme.colors.primary)
        )
        Spacer(modifier = Modifier.height(Dimension.pagePadding * 2))
        /** Merchant name */
        Text(
            text = LoggedMerchantPref.merchant?.merchantName ?: "Merchant Name",
            style = MaterialTheme.typography.h3,
            color = MaterialTheme.colors.secondaryVariant.copy(alpha = 0.8f),
        )
        Spacer(modifier = Modifier.height(Dimension.xs.div(2)))
        /** And here goes the branch's name */
        Text(
            text = LoggedMerchantPref.branch?.name ?: "Branch Name",
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.primary.copy(alpha = 0.8f)
        )
    }
}