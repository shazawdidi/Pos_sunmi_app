package com.altkamul.xpay.views.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.SignalCellularAlt
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.WorkInfo
import androidx.work.WorkManager
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.altkamul.xpay.R
import com.altkamul.xpay.model.DrawerItem
import com.altkamul.xpay.sealed.NetworkStatus
import com.altkamul.xpay.sealed.Screen
import com.altkamul.xpay.ui.theme.Dimension
import com.altkamul.xpay.ui.theme.imageBackground
import com.altkamul.xpay.ui.theme.lightGray
import com.altkamul.xpay.ui.theme.white
import com.altkamul.xpay.utils.LoggedMerchantPref
import com.altkamul.xpay.utils.decreaseBy
import com.altkamul.xpay.utils.mirror
import com.altkamul.xpay.viewmodel.ParentViewModel
import timber.log.Timber


/** The Custom status bar , contain network state & whether or not a loading operation is running , for now ! */
@Composable
fun CustomStatusBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.background)
            .padding(horizontal = Dimension.pagePadding, vertical = Dimension.hoverEffectPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){

        /** Our shared parent view model */
        val parentViewModel: ParentViewModel = viewModel(LocalContext.current as ComponentActivity)
        /** Network state check */
        val isConnected = parentViewModel.network.observeAsState()
        /** Loading data check , it should read the value of the Boolean flag */
        var isLoadingData by remember { mutableStateOf(false) }


        /**
         * Getting the state of all our syncing workers
         */
        val syncStates = WorkManager
            .getInstance(LocalContext.current)
            .getWorkInfosByTagLiveData("SyncData").observeAsState()

        if(syncStates.value?.any{ it.state == WorkInfo.State.RUNNING} == true){
            /** If one of them is running , update the loading indicator to true */
            isLoadingData = true
            Timber.d("some syncing is running now ... ")
        } else {
            /** else, update the loading indicator to false */
            isLoadingData = false
            Timber.d("No syncing is running , lets celebrate :-D ... ")
        }
        /** The actual UI */
        Icon(
            imageVector = Icons.Rounded.SignalCellularAlt,
            contentDescription = "network",
            modifier = Modifier
                .size(Dimension.smIconSize),
            tint = if (isConnected.value == NetworkStatus.Connected) MaterialTheme.colors.secondary else Color.Red
        )

        if (isLoadingData) {
            CircularProgressIndicator(
                modifier = Modifier.size(Dimension.smIconSize),
                strokeWidth = Dimension.hoverEffectPadding,
                color = MaterialTheme.colors.secondary,
                progress = 0.2f
            )
        }
    }
}

/**
 * The main AppTopBar that appear in the main destinations in the Navigation Drawer like home,settings, etc
 */

@Composable
fun MainTopBar(
    onlyDrawer: Boolean = false,
    showFilterButton: Boolean = false,
    searchState: String = "",
    onDrawerClicked: () -> Unit,
    onSearchQueryChanged: (String) -> Unit = {},
    onProfileClicked: () -> Unit = {},
    onFilterClicked: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimension.pagePadding, vertical = Dimension.hoverEffectPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier
                .clip(CircleShape)
                .padding(Dimension.hoverEffectPadding)
                .size(Dimension.mdIconSize)
                .clickable { onDrawerClicked() },
            imageVector = Icons.Rounded.Menu,
            contentDescription = "drawer icon",
            tint = MaterialTheme.colors.secondaryVariant
        )
        /**
         * The search input field & profile image is only visible in one destination which is home
         * but sometimes the only thing required is the drawer icon
         */
        if (!onlyDrawer) {
            /** The search input */
            SearchField(
                modifier = Modifier.padding(horizontal = Dimension.xs).weight(1f),
                state = searchState,
                // whenever the search query change , just pass it up to the screen holder
                onValueChanged = {
                    onSearchQueryChanged(it)
                }
            )
            /** Merchant's profile image */
            Image(
                painter = rememberImagePainter(
                    data = "LoggedMerchantPref.merchant?.profile ?: this could/should not happen !",
                    builder = {
                        transformations(CircleCropTransformation())
                    }
                ),
                contentDescription = "cover",
                modifier = Modifier
                    .padding(Dimension.hoverEffectPadding)
                    .clip(CircleShape)
                    .size(Dimension.topBarProfileSize)
                    .background(MaterialTheme.colors.surface)
                    .clickable { onProfileClicked() }
            )
        } else if(showFilterButton){
            /** Filter option that is only appear in transactions history */
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .clickable { onFilterClicked() }
                    .padding(horizontal = Dimension.xs),
                verticalAlignment = Alignment.CenterVertically,
            ){
                Text(
                    text = "Filter",
                    style = MaterialTheme.typography.subtitle2,
                    color = MaterialTheme.colors.onBackground,
                )
                Icon(
                    modifier = Modifier
                        .size(Dimension.mdIconSize)
                        .clip(CircleShape),
                    imageVector = Icons.Outlined.FilterAlt,
                    contentDescription = "filter icon",
                    tint = MaterialTheme.colors.onBackground.copy(alpha = 0.8f),
                )
            }
        }
    }
}

/**
 * The search input field that only appear in the home screen , it take the modifier and also the state that he hold currently
 * It pass the event of changing the search query up to the main TopAppBar in screen holder
 * so that the screen holder can pass it again to home screen
 */
@Composable
fun SearchField(
    modifier: Modifier = Modifier,
    state: String = "",
    onValueChanged: (searchQuery: String) -> Unit = {},
) {

    BasicTextField(
        cursorBrush = SolidColor(MaterialTheme.colors.secondary),
        modifier = modifier
            .shadow(elevation = Dimension.surfaceElevation, shape = MaterialTheme.shapes.small)
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface)
            .padding(horizontal = Dimension.pagePadding, vertical = Dimension.pagePadding.decreaseBy(0.5f)),
        value = state,
        onValueChange = {
            /** when the value change , it pass it up to MainTopBar() **/
            onValueChanged(it)
        },
        singleLine = true,
        textStyle = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.onBackground),
        decorationBox = {container->
            Box(
                modifier = modifier,
                contentAlignment = Alignment.CenterStart
            ){
                if(state.isEmpty()){
                    Text(
                        text = stringResource(id = R.string.search),
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.secondaryVariant,
                    )
                }
                container()
            }
        }
    )

//    TextField(
//        modifier = modifier
//            .padding(horizontal = Dimension.xs)
//            .shadow(elevation = Dimension.surfaceElevation, shape = MaterialTheme.shapes.small)
//            .fillMaxWidth(),
//        value = state,
//        onValueChange = {
//            /** when the value change , it pass it up to MainTopBar() **/
//            onValueChanged(it)
//        },
//        placeholder = {
//            Text(
//                text = stringResource(id = R.string.search),
//                style = MaterialTheme.typography.body2
//            )
//        },
//        singleLine = true,
//        shape = MaterialTheme.shapes.small,
//        colors = TextFieldDefaults.textFieldColors(
//            textColor = MaterialTheme.colors.onBackground,
//            backgroundColor = white,
//            cursorColor = MaterialTheme.colors.secondary,
//            focusedIndicatorColor = Color.Transparent,
//            unfocusedIndicatorColor = Color.Transparent,
//        ),
//        textStyle = MaterialTheme.typography.body2
//    )
}

/**
 * The drawer layout , it contains all that elements that appear when you open the drawer
 */
@Composable
fun DrawerLayout(
    currentRoute: String,
    onDrawerScreenClicked: (route: String) -> Unit = {},
    onLogout: ()-> Unit
) {
    /**
     * A list that contains all the possible drawer items that a user - regardless of who the user are - can access to
     */
    val destinations = listOf(
        DrawerItem(id = 1, screen = Screen.Home, stringResource(id = R.string.home)),
        DrawerItem(id = 2, screen = Screen.Reprint, stringResource(id = R.string.reprint)),
        DrawerItem(id = 3, screen = Screen.Claim, stringResource(id = R.string.claim)),
        DrawerItem(id = 4, screen = Screen.TransactionHistory, stringResource(id = R.string.transaction_history)),
        DrawerItem(id = 5, screen = Screen.Reports, stringResource(id = R.string.reports)),
        DrawerItem(id = 6, screen = Screen.Settings, stringResource(id = R.string.settings)),
        DrawerItem(id = 7, screen = Screen.Logout, stringResource(id = R.string.logout)),
    )
    /** Hide it only for testing */
//        .filter {
//        /** Now we filter it out so that each user only see the items that he had the permission to access it **/
//        it.id in LoggedMerchantPref.user?.permissions?.let { permissions ->
//            permissions.map { permission ->
//                permission.posPermissionId
//            }
//        } ?: listOf(1, 2, 3, 4, 5, 6)
//    }
    /** Adding the Drawer header which contain merchant data and also the current logged in user info */
    DrawerHeader()
    Spacer(modifier = Modifier.height(Dimension.mdLineMargin))
    /** And now , it's time to build destinations menu */
    LazyColumn(
        contentPadding = PaddingValues(vertical = Dimension.xsLineMargin)
    ) {
        /** Iterate through the filtered drawer destinations */
        items(destinations) { destination ->
            DrawerItemLayout(
                item = destination,
                selected = currentRoute == destination.screen.route,
                onItemClicked = {
                    if(destination.screen.route == "logout"){
                        /** Log out button clicked */
                        onLogout()
                    } else {
                        /**
                         * check if the clicked item is the currently active or not
                         * if not , then send the route of this clicked item to navigate to
                         */
                        if (currentRoute != destination.screen.route) {
                            onDrawerScreenClicked(destination.screen.route)
                        }
                    }
                },
            )
        }
    }

}

@Composable
fun DrawerHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimension.pagePadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(Dimension.sm))
        /** The merchant's profile image */
        Image(
            painter = rememberImagePainter(
                data = "LoggedMerchantPref.merchant?.profile ?: This (could/should) not happen btw !",
                builder = {
                    transformations(CircleCropTransformation())
                }
            ),
            contentDescription = "profile",
            modifier = Modifier
                .padding(Dimension.hoverEffectPadding)
                .clip(CircleShape)
                .size(Dimension.drawerProfileSize)
                .background(lightGray)
        )
        Spacer(modifier = Modifier.height(Dimension.xsLineMargin))
        /** Merchant Name here */
        Text(
            text = LoggedMerchantPref.merchant?.merchantName ?: "Merchant Name",
            style = MaterialTheme.typography.h2,
            color = MaterialTheme.colors.onBackground.copy(alpha = 0.8f)
        )
        /** And here goes the branch's name */
        Text(
            text = LoggedMerchantPref.branch?.name ?: "Branch Name",
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.primary
        )
        /** Finally , the logged in user info */
        Text(
            text = buildAnnotatedString {
                /**
                 * Firstly adding the name of the user that is currently logged in :-D
                 */
                append("${LoggedMerchantPref.user?.name ?: "User Name"} - ")
                /**
                 * Then adding the role of that user , to ensure null safety we add the role cashier
                 */
                withStyle(TextStyle(color = MaterialTheme.colors.secondary).toSpanStyle()) {
                    append(LoggedMerchantPref.user?.userType ?: "Cashier")
                }
            },
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.secondaryVariant
        )
    }
}

@Composable
fun DrawerItemLayout(
    item: DrawerItem = DrawerItem(1, Screen.Home, title = "Home"),
    selected: Boolean = true,
    onItemClicked: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .padding(horizontal = Dimension.pagePadding, )
            .fillMaxWidth()
            .clip(shape = MaterialTheme.shapes.small)
            .background(
                if (selected) lightGray.copy(alpha = 0.3f) else Color.Transparent
            )
            .clickable {
                /** event of a drawer's item got clicked **/
                onItemClicked()
            }
            .padding(Dimension.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        /** The icon of the drawer item **/
        Image(
            painter = painterResource(id = item.screen.icon),
            contentDescription = "image",
            modifier = Modifier
                .mirror()
                .size(Dimension.md),
            colorFilter = ColorFilter.tint(MaterialTheme.colors.primary)
        )
        Spacer(modifier = Modifier.width(Dimension.md))
        /** The title of the drawer item **/
        Text(
            modifier = Modifier.weight(1f),
            text = item.title,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.secondaryVariant
        )
    }
}


