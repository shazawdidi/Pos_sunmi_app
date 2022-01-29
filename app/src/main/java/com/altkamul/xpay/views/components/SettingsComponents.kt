package com.altkamul.xpay.views.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.altkamul.xpay.model.DrawerItem
import com.altkamul.xpay.sealed.Screen
import com.altkamul.xpay.ui.theme.Dimension
import com.altkamul.xpay.ui.theme.FontSize
import com.altkamul.xpay.ui.theme.lightGray
import com.altkamul.xpay.utils.Constants
import com.altkamul.xpay.utils.mirror

@Composable
fun DrawItems(
    contentText: String,
    navController: NavHostController,
    currentRoute: String,
    destinations: List<DrawerItem>,
    onSettingItemClicked: (route: String) -> Unit = {},
) {

    Column(verticalArrangement = Arrangement.spacedBy(Dimension.sm))
    {
        Text(
            text = contentText,
            style = MaterialTheme.typography.body2.copy(color = lightGray, fontSize = FontSize.lg)
        )
        destinations.forEach { destination ->
            Item(
                item = destination,
                onItemClicked = {
                    if (currentRoute != destination.screen.route) {
                        onSettingItemClicked(destination.screen.route)
                        navController.navigate(destination.screen.route)
                    }
                }
            )
        }
    }
}

@Composable
fun Item(
    item: DrawerItem = DrawerItem(1, Screen.Accounts, title = "Accounts"),
    onItemClicked: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimension.xl)
            .clip(shape = MaterialTheme.shapes.small)
            .clickable {
                onItemClicked()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimension.sm)
    ) {
        /** The icon of the Common Setting item **/
        Image(
            painter = painterResource(id = item.screen.icon),
            contentDescription = "icon",
            modifier = Modifier.mirror()
        )
        Text(
            modifier = Modifier.weight(1f),
            text = item.title,
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.secondaryVariant,
            maxLines = 1
        )
        Text(
            text = "Details",
            style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Normal),
            color = lightGray
        )
        Icon(
            Icons.Filled.ArrowForwardIos,
            "contentDescription",
            tint = lightGray
        )
    }
}

@Composable
fun ColumnScope.AppVersion() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .weight(0.1f),
        contentAlignment = Alignment.BottomCenter
    ) {
        Text(
            text = Constants.APP_VERSION,
            style = MaterialTheme.typography.h6,
            color = lightGray
        )

    }
}