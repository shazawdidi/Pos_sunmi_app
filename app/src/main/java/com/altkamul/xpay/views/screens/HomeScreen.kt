package com.altkamul.xpay.views.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.altkamul.xpay.R
import com.altkamul.xpay.model.CartItem
import com.altkamul.xpay.model.Item
import com.altkamul.xpay.sealed.Screen
import com.altkamul.xpay.ui.theme.Dimension
import com.altkamul.xpay.ui.theme.FontSize
import com.altkamul.xpay.utils.Common
import com.altkamul.xpay.viewmodel.HomeScreenViewModel
import com.altkamul.xpay.views.components.CategoryAndSubCategoryAndItemsLayout

@Composable
fun HomeScreen(
    navController: NavController,
    searchState: String,
    homeScreenViewModel: HomeScreenViewModel =
        hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        val cartItem =
            navController.currentBackStackEntry?.savedStateHandle?.get<MutableList<CartItem>>("cart")
        val items =
            navController.currentBackStackEntry?.savedStateHandle?.get<MutableList<Item>>("item")
        if (cartItem != null && items != null && items.isNotEmpty()) {
            homeScreenViewModel.assigningCartListToHomeList(cartItem, items)
            homeScreenViewModel.getCategoryAndSubCategoryAndItem()
            navController.currentBackStackEntry?.savedStateHandle?.get<MutableList<CartItem>>("cart")
                ?.clear()
            navController.currentBackStackEntry?.savedStateHandle?.get<MutableList<Item>>("item")
                ?.clear()
        } else {
            homeScreenViewModel.getCategoryAndSubCategoryAndItem()
        }
    }
    LaunchedEffect(key1 = searchState) {
        homeScreenViewModel.filteringBaseOnSearchField(filterBY = searchState)
    }
    Column(
        Modifier
            .fillMaxSize()
    ) {
        /** This Function Will Draw Home Content*/
        HomeContent(homeScreenViewModel) {
            navController.navigate(Screen.QuickAccess.route) {
                this.popUpTo(Screen.Home.route) {
                    this.saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
        /** This Function Will Draw Home Bottom Bar*/
        HomeBottomBar(homeScreenViewModel, navController)
    }
}

/** Begin Of Home Content like Category And Sub Category Shape*/
@Composable
fun ColumnScope.HomeContent(
    homeScreenViewModel: HomeScreenViewModel,
    navigateToQuickAccess: () -> Unit
) {
    Column(
        Modifier
            .weight(0.85f)
            .fillMaxSize()
            .padding(
                top = Dimension.pagePadding,
                start = Dimension.pagePadding,
                end = Dimension.pagePadding
            )
            .background(MaterialTheme.colors.background)
    ) {

        /** Draw Custom Tab Layout For Category and Quick Access Tab*/
        CategoryAndSubCategoryAndItemsLayout(homeScreenViewModel) {
            navigateToQuickAccess()
        }
    }
}

/** This FUnction Will Draw Bottom Bar Contain Shopping Cart Icon And Total Price*/
@Composable
fun ColumnScope.HomeBottomBar(
    homeScreenViewModel: HomeScreenViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    Card(
        Modifier
            .weight(0.15f)
            .fillMaxSize(),
        shape = RoundedCornerShape(Dimension.zero),
        backgroundColor = MaterialTheme.colors.surface,
    ) {
        /** This Variable For Total Price*/
        val totalPrice by remember {
            homeScreenViewModel.totalPrice
        }

        /** This variable For Quantity*/
        val quantity by remember {
            homeScreenViewModel.quantity
        }

        /***/
        Row(
            Modifier
                .shadow(elevation = Dimension.surfaceElevation)
                .background(MaterialTheme.colors.surface)
                .padding(
                    horizontal = Dimension.pagePadding,
                    vertical = Dimension.xs.div(2)
                )
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                Modifier
                    .weight(0.8f)
            ) {
                Text(
                    text = "Total",
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.h6
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colors.secondaryVariant,
                                fontSize = FontSize.x
                            )
                        ) {
                            append("$totalPrice")
                        }
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colors.secondaryVariant,
                                fontSize = FontSize.sm
                            )
                        ) {
                            append("  AED")
                        }
                    }
                )
            }

            Box(
                Modifier
                    .weight(0.2f).fillMaxHeight()
                    .clickable {
                        if (homeScreenViewModel.selectedCartItem.isEmpty())
                            Common.createToast(context, "Pleas Select Items")
                        else {
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                "cartItem",
                                homeScreenViewModel.selectedCartItem.toList()
                            )
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                "items",
                                homeScreenViewModel.selectedItem.toList()
                            )
                            homeScreenViewModel.resetVariables()
                            navController.navigate(Screen.Cart.route)
                        }
                    }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.shopping_cart),
                    contentDescription = "Shopping Cart",
                    tint = MaterialTheme.colors.secondaryVariant,
                    modifier = Modifier
                        .padding(Dimension.xs)
                        .size(Dimension.lg)
                        .align(Alignment.BottomEnd)
                )
                Box(
                    modifier = Modifier
                        .clip(shape = CircleShape)
                        .background(MaterialTheme.colors.secondary)
                        .padding(Dimension.xs.div(4))
                        .size(Dimension.md)
                        .align(Alignment.CenterEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$quantity",
                        color = MaterialTheme.colors.surface,
                        style = MaterialTheme.typography.body2,
                    )
                }
            }
        }
    }
}