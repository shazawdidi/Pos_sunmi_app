package com.altkamul.xpay.views.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.altkamul.xpay.R
import com.altkamul.xpay.sealed.ExpandableType
import com.altkamul.xpay.sealed.Screen
import com.altkamul.xpay.ui.theme.Dimension
import com.altkamul.xpay.utils.Common
import com.altkamul.xpay.utils.Constants
import com.altkamul.xpay.utils.ScreenDimensions
import com.altkamul.xpay.utils.largerThan
import com.altkamul.xpay.viewmodel.QuickAccessViewModel
import com.altkamul.xpay.views.components.CategoryContent
import com.altkamul.xpay.views.components.CustomButton
import com.altkamul.xpay.views.components.CustomLazyGrid
import com.altkamul.xpay.views.components.QuickExpandableItem

@Composable
fun QuickAccessScreen(
    navController : NavHostController,
    quickAccessViewModel: QuickAccessViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val quickItems = quickAccessViewModel.selectedItems
        val submitEnable by remember { quickAccessViewModel.submitEnabled }
        Column(
            modifier = Modifier
                .weight(1f)
                .background(MaterialTheme.colors.background)
                .padding(horizontal = Dimension.pagePadding, ),
        ) {
            val categories by quickAccessViewModel.data.observeAsState()
            /** Page title */
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.quick_acess),
                style = MaterialTheme.typography.h1
            )
            Spacer(modifier = Modifier.height(Dimension.xs))
            /** Title's slug */
            Text(
                text = stringResource(R.string.quick_access_slug),
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.secondaryVariant.copy(alpha = 0.5f),
            )
            Spacer(modifier = Modifier.height(Dimension.md))
            Text(
                modifier = Modifier.fillMaxWidth(0.7f),
                text = stringResource(id = R.string.select_items),
                style = MaterialTheme.typography.h3.copy(color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f))
            )
            Spacer(modifier = Modifier.height(Dimension.pagePadding))
            /** Building the categories, subcategories , and items expanded layout */
            /** Check screen size to determine how many cells we should have */
            val categoriesSpan =
                if(ScreenDimensions.Width largerThan Constants.largeDevicesRange.first.dp) 2
                else 1

            CustomLazyGrid(
                items = categories ?: listOf(),
                modifier = Modifier
                    .weight(1f)
                    .background(Color.Transparent),
                gridSpan = categoriesSpan,
                contentPadding = PaddingValues(Dimension.zero)
            ){width, category ->
                /** Remember category's expanding state */
                var categoryExpanded by rememberSaveable { mutableStateOf(false) }
                QuickExpandableItem(
                    modifier = Modifier.width(width = width),
                    title = category.categoryNameEN ?: "No name" ,
                    type = ExpandableType.Category,
                    expanded = categoryExpanded,
                    onExpandChange = {
                        /** Toggle expanding state */
                        categoryExpanded = !categoryExpanded
                    },
                    content ={
                        val context = LocalContext.current
                        /** The expandable content */
                        CategoryContent(
                            category,
                            quickItems ?: mutableListOf(),
                            onItemSelected = { itemId->
                                /** Catching the event of selecting an item */
                                quickAccessViewModel.updateSelectedItems(itemId = itemId)
                                Common.createToast(context = context, message = "${quickItems.size}" + "Items selected")
                            }
                        )
                    }
                )
            }
        }
        /** Skip and submit buttons section */
        Row(
            modifier = Modifier
                .background(color = MaterialTheme.colors.surface)
                .padding(Dimension.pagePadding)
                .fillMaxWidth()
        ) {
            /** Skip button */
            CustomButton(
                modifier = Modifier
                    .weight(1f)
                    .clip(MaterialTheme.shapes.small),
                elevationEnabled = false,
                buttonColor = Color.Transparent,
                contentColor = MaterialTheme.colors.secondaryVariant,
                text = stringResource(id = R.string.skip),
                onButtonClicked = {
                    /** On Skipping Page , just forget about this and navigate to home man ! */
                    navController.popBackStack()
                    navController.navigate(Screen.Home.route)
                }
            )
            Spacer(modifier = Modifier.width(Dimension.sm))
            /** Submit button */
            CustomButton(
                modifier = Modifier
                    .weight(1f)
                    .clip(MaterialTheme.shapes.small),
                enabled = submitEnable,
                buttonColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary,
                text = stringResource(id = R.string.submit),
                onButtonClicked = {
                    /** On submitting , save selected items to local storage and then go to home */
                    quickAccessViewModel.saveQuickAccessItems(
                        onSaveCompleted = {
                            /** Navigating to home directly */
                            navController.popBackStack()
                            navController.navigate(Screen.Home.route)
                        }
                    )
                }
            )
        }
    }
}
