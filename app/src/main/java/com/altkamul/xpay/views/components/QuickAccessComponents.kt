package com.altkamul.xpay.views.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.altkamul.xpay.R
import com.altkamul.xpay.model.Category
import com.altkamul.xpay.model.Item
import com.altkamul.xpay.sealed.ExpandableType
import com.altkamul.xpay.ui.theme.Dimension
import com.altkamul.xpay.utils.*
import timber.log.Timber

/** Expandable Category and Subcategory expandable single item layout */
@Composable
fun QuickExpandableItem(
    modifier: Modifier = Modifier,
    title: String,
    type: ExpandableType = ExpandableType.Category,
    expanded: Boolean = false,
    onExpandChange: () -> Unit,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(
                horizontal = if (type is ExpandableType.SubCategory) Dimension.pagePadding else Dimension.zero
            )
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(
                if (type is ExpandableType.Category) MaterialTheme.colors.surface
                else MaterialTheme.colors.background
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    /** Catching the event of clicking on an item and pass it up */
                    onExpandChange()
                }
                .padding(horizontal = Dimension.xs, vertical = Dimension.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.mirror(),
                imageVector = if (expanded) Icons.Rounded.KeyboardArrowDown else Icons.Rounded.KeyboardArrowRight,
                contentDescription = "expandIcon",
                tint = MaterialTheme.colors.primary
            )
            Spacer(modifier = Modifier.width(Dimension.xs.div(2)))
            Text(
                text = title,
                style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onBackground.copy(alpha = 0.5f))
            )
        }
        if (expanded) {
            /** If it's expanded , then show our content , which can be a subcategories or a products */
            content()
        }
    }
}

/** Category's expanded content */
@Composable
fun CategoryContent(
    category: Category,
    quickItems: MutableList<Int>,
    onItemSelected: (itemId: Int) -> Unit,
) {
    /** Check screen size to determine how many cells we should have */
    val subCategoriesSpan =
        if(ScreenDimensions.Width largerThan Constants.largeDevicesRange.first.dp) 2
        else 1

    Timber.d("Subcategory span is $subCategoriesSpan")

    CustomLazyGrid(
        items = category.subcategories ?: mutableListOf(),
        gridSpan = subCategoriesSpan,
        scrollable = false,
        contentPadding = PaddingValues(Dimension.zero),
    ) {width, subcategory ->
        /** Remember subcategory's expanding state */
        var subCategoryExpanded by rememberSaveable { mutableStateOf(false) }
        QuickExpandableItem(
            modifier = Modifier.width(width = width),
            title = subcategory.subCategoryNameEN ?: "No name",
            type = ExpandableType.SubCategory,
            expanded = subCategoryExpanded,
            onExpandChange = {
                /** Toggle expanding state */
                subCategoryExpanded = !subCategoryExpanded
            },
            content = {
                SubcategoryContent(
                    items = subcategory.items ?: return@QuickExpandableItem,
                    quickItems = quickItems,
                    onItemSelected = { itemId ->
                        /** Catching the event of adding an item to quick access list */
                        onItemSelected(itemId)
                    },
                )
            },
        )
    }
}

/** Subcategory's expanded content */
@Composable
fun SubcategoryContent(
    items: List<Item> = mutableListOf(),
    quickItems: MutableList<Int>,
    onItemSelected: (itemId: Int) -> Unit,
    gridSpan: Int = 3,
) {
    CustomLazyGrid(
        items = items,
        gridSpan = gridSpan,
        scrollable = false,
        contentPadding = PaddingValues(Dimension.zero),
    ){width, item ->
        /** Product's selection state */
        var selected by rememberSaveable { mutableStateOf(item.itemId in quickItems) }
        QuickProductItem(
            modifier = Modifier.width(width = width),
            productImage = item.imageUrl ?: "",
            productName = item.itemNameEN ?: "",
            productPrice = item.facePrice ?: 10,
            onItemSelected = {
                /** Update our selected products list */
                onItemSelected(item.itemId ?: 0)
                /** Then toggle selection state */
                selected = selected.not()
            },
            selected = selected,
        )
    }
}

/** Product item layout , which appear when you expand the subcategory item */
@Composable
fun QuickProductItem(
    modifier: Modifier = Modifier,
    productImage: String,
    productName: String,
    productPrice: Int,
    selected: Boolean = false,
    onItemSelected: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colors.background)
            .clickable {
                /** Catching the event of clicking on an item */
                onItemSelected()
            }
            .padding(Dimension.xs),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /** Box contain the image and the mask that appear when you select this item */
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .shadow(elevation = Dimension.elevation, shape = MaterialTheme.shapes.small)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colors.surface),
        ) {
            /** Product's image */
            val painter = rememberImagePainter(data = productImage)
            Image(
                modifier = modifier
                    .fillMaxSize(),
                painter = painter,
                contentDescription = "product cover",
            )
            /**
             * Currently the checking for loading state is Experimental.
             * We can't use progress bar as a place holder yet, but soon ...
             */
//            if(painter.state is ImagePainter.State.Loading){
//                CircularProgressIndicator(
//                    modifier = Modifier.align(Alignment.Center),
//                    color = MaterialTheme.colors.primary
//                )
//            }
            /** The mask on the item in case it got selected */
            if (selected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.primary.copy(alpha = 0.5f)),
                )
                Icon(
                    modifier = Modifier.align(Alignment.Center),
                    imageVector = Icons.Rounded.Check,
                    contentDescription = "check mark",
                    tint = MaterialTheme.colors.surface
                )
            }

        }
        Spacer(modifier = Modifier.height(Dimension.smLineMargin))
        /** Product's name */
        Text(
            text = productName,
            style = MaterialTheme.typography.subtitle1.copy(color = MaterialTheme.colors.secondaryVariant),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        /** Product's price */
        Text(
            text = "$productPrice ${stringResource(R.string.aed_currency)}",
            style = MaterialTheme.typography.subtitle2.copy(color = MaterialTheme.colors.secondaryVariant),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}