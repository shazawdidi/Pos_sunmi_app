package com.altkamul.xpay.views.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.altkamul.xpay.R
import com.altkamul.xpay.model.SubCategory
import com.altkamul.xpay.ui.theme.Dimension
import com.altkamul.xpay.ui.theme.FontSize
import com.altkamul.xpay.ui.theme.lightShadowOfGray
import com.altkamul.xpay.utils.Constants
import com.altkamul.xpay.utils.ScreenDimensions
import com.altkamul.xpay.utils.smallerThan
import com.altkamul.xpay.viewmodel.HomeScreenViewModel
import timber.log.Timber

/** This Function Draw Category And SubCategory And Items Layout*/
@Composable
fun CategoryAndSubCategoryAndItemsLayout(
    homeScreenViewModel: HomeScreenViewModel,
    navigateToQuickAccess: () -> Unit
) {
    /** Parent Layout Its Column To Divided The Screen Into Category Tab And SubCategory Card*/
    Column(Modifier.fillMaxSize()) {
        /** For Navigation Between Sub And Item*/
        val navController = rememberNavController()
        /** First Divided To Category Tab Layout*/
        CategoriesTapsLayout(homeScreenViewModel, navController)
        /** Second Divided Sub Category And Item Layout With Navigation*/
        SubCategoryAndItemNavigation(navController, homeScreenViewModel) {
            navigateToQuickAccess()
        }
    }

}

/** This Function Will Draw Category Tabs Layout*/
@Composable
fun CategoriesTapsLayout(
    homeScreenViewModel: HomeScreenViewModel,
    navController: NavHostController
) {
    Column(
        Modifier
            .fillMaxHeight(0.1f)
    ) {

        /** We Must have List Of Category */
        val category by homeScreenViewModel.category.observeAsState()

        /** We Must Have List Of Quick Access*/
        val quickAccessItems by homeScreenViewModel.quickAccessItems.observeAsState()

        /** This Variable to Determined The Current Selected Category*/
        var currentSelectedTab by remember { homeScreenViewModel.currentSelectedCategoryTab }

        /** This Variable For Determined is Select Quick Access Tab*/
        var isSelectQuickAccessTabShip by remember { mutableStateOf(false) }
        LazyRow(modifier = Modifier.fillMaxWidth()) {
            /** This Shape For Quick Access Tab Layout*/
            item {
                CategoryTap(
                    isSelected = isSelectQuickAccessTabShip,
                    categoryName = "Quick Access",
                    lastItem = false
                ) {
                    homeScreenViewModel.getQuickAccessItems()
                    isSelectQuickAccessTabShip = true
                    currentSelectedTab = -1
                    quickAccessItems?.let {
                        homeScreenViewModel.assigningCurrentSubCategory(emptyList())
                        homeScreenViewModel.assigningCurrentItems(it)
                    }
                    navController.popBackStack()
                    navController.navigate("Item")
                }
            }
            category?.let {
                /** Iteration in List Of Category To Draw Shapes*/
                it.forEachIndexed { index, item ->
                    item {
                        /** The Shape Of Category Tab*/
                        CategoryTap(
                            isSelected = index == currentSelectedTab,
                            categoryName = item.categoryNameEN ?: "No Name",
                            lastItem = index == category?.lastIndex ?: 0,
                        ) {
                            homeScreenViewModel.searchBySubCategory.value = true
                            /** On Click Navigate To SubCategory Nested Page*/
                            isSelectQuickAccessTabShip = false
                            currentSelectedTab = index
                            homeScreenViewModel.assigningCurrentSubCategory(
                                item.subcategories ?: emptyList()
                            )
                            navController.popBackStack()
                            navController.navigate("SubCategory")
                        }
                    }
                }
            }
        }
        /** This Spacer Draw Gray Line Under Tab Layout*/
        Spacer(
            modifier = Modifier
                .height(Dimension.xs.div(4))
                .fillMaxWidth()
                .background(lightShadowOfGray)
        )
    }
}

/** This Function Will Draw Category Tab Ship Shape*/
@Composable
fun CategoryTap(
    isSelected: Boolean,
    categoryName: String,
    lastItem: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(end = if (lastItem) Dimension.zero else Dimension.xxl)
            .clickable {
                onClick()
            }
    ) {
        Text(
            modifier = Modifier.padding(bottom = Dimension.xs.div(2)),
            text = categoryName,
            color = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.secondaryVariant,
            style = MaterialTheme.typography.h6
        )

        /** Wile This Condition == true showing underLine = orange*/
        if (isSelected)
            Spacer(
                modifier = Modifier
                    .width(Dimension.xxxl)
                    .height(Dimension.xs.div(2))
                    .background(MaterialTheme.colors.primary)
                    .clip(MaterialTheme.shapes.small)
            )
    }
}

/** This Function Will Create Nav Host Between SubCategory And Item */
@Composable
fun SubCategoryAndItemNavigation(
    navController: NavHostController,
    homeScreenViewModel: HomeScreenViewModel,
    navigateToQuickAccess: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        NavHost(navController = navController, startDestination = "SubCategory") {

            composable("SubCategory") {
                SubCategoryGridLayout(homeScreenViewModel) {
                    homeScreenViewModel.searchBySubCategory.value = false
                    navController.popBackStack()
                    navController.navigate("Item")
                }
            }
            composable("Item") {
                ItemsGridLayout(homeScreenViewModel) {
                    navigateToQuickAccess()
                }
            }
        }
    }
}


/** This Function Will Draw Grid Layout For SubCategory*/
@Composable
fun SubCategoryGridLayout(
    homeScreenViewModel: HomeScreenViewModel,
    onClick: () -> Unit
) {
    val selectedSubCategoryList by homeScreenViewModel.currentSubCategorySelected.observeAsState()

    selectedSubCategoryList?.let { list ->

        val span =
            if (ScreenDimensions.Width.smallerThan(value = Constants.largeDevicesRange.first.dp)) 2
            else 4
        /** Checking if The List Was Empty Or Not*/
        if (list.isNotEmpty())
            LazyGridLayout(
                list = list,
                gridSpan = span,
            ) { item ->
                SubCategoryCard(
                    subCategoryName = item.subCategoryNameEN ?: "No Data",
                    imageUrl = item.imageUrl ?: "No Data"
                ) {
                    homeScreenViewModel.assigningCurrentItems(
                        item.items ?: emptyList()
                    )
                    homeScreenViewModel.currentSubCategoryID.value =
                        item.subCategoryId ?: 0
                    onClick()
                }
            }
        else
            EmptyContent(empty = "Empty Category")
    }
}

/** The SubCategory Card */
@Composable
fun RowScope.SubCategoryCard(
    subCategoryName: String = "text",
    imageUrl: String = "url",
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable {
                onClick()
            },
        backgroundColor = MaterialTheme.colors.surface,
        elevation = Dimension.elevation,
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /** The Sub Category Image Should Showing here*/
            val painter =
                rememberImagePainter(data = imageUrl) {
                    placeholder(R.drawable.placeholder_images)
                    error(R.drawable.placeholder_images)
                }
            Image(
                modifier = Modifier
                    .weight(0.7f)
                    .padding(start = Dimension.sm, end = Dimension.sm, top = Dimension.xs),
                painter = painter,
                contentDescription = "Sub Category Image",
                contentScale = ContentScale.Crop
            )

            /** The SUb Category Title Should Showing here*/
            /** This Text Represent Product Name*/
            Text(
                modifier = Modifier
                    .weight(0.3f),
                text = subCategoryName,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.secondaryVariant,
                maxLines = 2,
            )
        }
    }
}

/** This Will Draw Items List In Grid Layout*/
@Composable
fun ItemsGridLayout(homeScreenViewModel: HomeScreenViewModel, navigateToQuickAccess: () -> Unit) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(Dimension.xs)
    ) {
        /** This Variable For Saving Selected Items State*/
        Box(
            modifier = Modifier
                .fillMaxHeight(0.1f)
                .fillMaxWidth()
        ) {
            /** This Variable For Selected SubCategory State*/
            val subCategorySelectedID = remember {
                homeScreenViewModel.currentSubCategoryID
            }
            val selectedSubCategory by homeScreenViewModel.currentSubCategorySelected.observeAsState()
            selectedSubCategory?.let { list ->
                SubCategoryTabLayout(
                    list = list,
                    subCategorySelectedID = subCategorySelectedID,
                    navigateToQuickAccess = { navigateToQuickAccess() }, onClick = {
                        subCategorySelectedID.value = it.subCategoryId ?: 0
                        homeScreenViewModel.assigningCurrentItems(it.items ?: emptyList())
                    })

            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val selectedItems by homeScreenViewModel.currentItemsSelected.observeAsState()
            selectedItems?.let { list ->
                val span =
                    if (ScreenDimensions.Width.smallerThan(value = Constants.largeDevicesRange.first.dp)) 2
                    else 4
                if (list.isNotEmpty())
                    LazyGridLayout(list = list, gridSpan = span) { item ->
                        var isSelected by remember {
                            mutableStateOf(homeScreenViewModel.selectedCartItem.any {
                                it.itemId == item.itemId
                            })
                        }
                        ItemCard(
                            isSelected = isSelected,
                            itemPrice = (item.facePrice ?: 0).toString(),
                            itemName = item.itemNameEN ?: "No data",
                            itemImageUrl = item.imageUrl ?: "No data"
                        ) {
                            homeScreenViewModel.addItemToCart(item)
                            isSelected = homeScreenViewModel.selectedCartItem.any {
                                it.itemId == item.itemId
                            }
                        }
                    }
                else
                    EmptyContent(empty = "Empty Sub Category")
            }
        }

    }
}

/** This Function Will Draw Item Card*/
@Composable
fun RowScope.ItemCard(
    isSelected: Boolean,
    itemName: String,
    itemImageUrl: String,
    itemPrice: String,
    onClick: () -> Unit

) {
    Card(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable {
                onClick()
            }
            .border(
                width = 2.dp,
                if (isSelected) MaterialTheme.colors.primary else Color.Transparent,
                shape = MaterialTheme.shapes.small
            ),
        backgroundColor = MaterialTheme.colors.surface,
        elevation = Dimension.elevation,
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /** Getting Item Image Remotely From The Server*/
            val painter =
                rememberImagePainter(data = itemImageUrl) {
                    placeholder(R.drawable.placeholder_images)
                    error(R.drawable.placeholder_images)
                }
            /** Item Image Should Showing here*/
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f)
                    .padding(start = Dimension.sm, end = Dimension.sm, top = Dimension.xs),
                painter = painter,
                contentDescription = "item Image",
                contentScale = ContentScale.Crop
            )
            /** The Item Name Should Showing here*/
            Text(
                modifier = Modifier
                    .weight(0.3f),
                text = itemName,
                style = MaterialTheme.typography.h5,
                color = MaterialTheme.colors.secondaryVariant,
                maxLines = 2,
            )

            /** The Item Price Should Showing here*/
            Text(
                modifier = Modifier
                    .weight(0.2f),
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontSize = FontSize.x,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colors.secondaryVariant,
                            fontFamily = FontFamily(
                                Font(
                                    R.font.montserrat_normal,
                                    FontWeight.ExtraBold
                                )
                            )
                        )
                    ) {
                        append(itemPrice)
                    }
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colors.secondary,
                            fontSize = FontSize.sm
                        )
                    ) {
                        append("  AED")
                    }
                }
            )
        }
    }

}

/** This Function Will Draw SubCategory Ships Layout*/
@Composable
fun SubCategoryTabLayout(
    list: List<SubCategory>,
    onClick: (subCategory: SubCategory) -> Unit,
    navigateToQuickAccess: () -> Unit,
    subCategorySelectedID: MutableState<Int>
) {
    if (list.isNotEmpty())
        LazyRow(
            Modifier
                .fillMaxSize()
        ) {
            items(list) {
                SubCategoryShip(
                    name = it.subCategoryNameEN ?: "No Data",
                    isSelected = it.subCategoryId == subCategorySelectedID.value
                ) {
                    onClick(it)
                }
            }
        }
    else
        QuickAccessAddButton {
            navigateToQuickAccess()
        }
}

/** This Function Will Draw SubCategory Tab Chip Shape*/
@Composable
fun SubCategoryShip(
    name: String = "Chip",
    isSelected: Boolean = false,
    onSelectionChanged: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .padding(end = Dimension.xs),
        elevation = Dimension.elevation,
        shape = CircleShape,
        color = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.surface
    ) {
        Row(modifier = Modifier
            .toggleable(
                value = isSelected,
                onValueChange = {
                    onSelectionChanged()
                }
            )
            .padding(start = Dimension.xxl, end = Dimension.xxl),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.h6,
                color = if (isSelected) MaterialTheme.colors.surface else MaterialTheme.colors.secondaryVariant,
            )
        }
    }
}

/** For Empty Category OR SubCategory*/
@Composable
fun EmptyContent(empty: String) {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Text(
            text = empty,
            style = MaterialTheme.typography.h4,
            color = MaterialTheme.colors.secondaryVariant,
            maxLines = 2,
            fontFamily = FontFamily(Font(R.font.montserrat_normal, FontWeight.Bold))
        )
    }
}

/** This Function Will Draw Lazy Vertical Grid Layout*/
@Composable
fun <T> LazyGridLayout(
    list: List<T>,
    gridSpan: Int,
    content: @Composable RowScope.(item: T) -> Unit
) {
    Timber.d("size ${list.size}")
    var listDropped = list
    LazyColumn(
        Modifier.fillMaxSize()
    ) {
        for (i in 0..listDropped.lastIndex step gridSpan)
            item {
                Row(
                    modifier = Modifier
                        .padding(bottom = Dimension.xs.div(2))
                        .fillMaxWidth(
                            if (gridSpan > listDropped.size)
                                if (listDropped.size == 1 && gridSpan == 4)
                                    0.25f
                                else
                                    0.5f
                            else 1f
                        )
                        .height(200.dp),
                    horizontalArrangement = Arrangement.spacedBy(Dimension.xs)
                ) {
                    listDropped.take(gridSpan).forEach { content(it) }
                    listDropped = listDropped.drop(gridSpan)
                }
            }
    }
}


@Composable
fun QuickAccessAddButton(navigateToQuickAccess: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colors.secondary)
            .clickable {
                navigateToQuickAccess()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Icon",
            modifier = Modifier
                .padding(Dimension.xs)
                .size(Dimension.md),
            tint = MaterialTheme.colors.onSecondary,
        )
    }
}