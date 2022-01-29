package com.altkamul.xpay.views.screens

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.altkamul.xpay.sealed.NetworkStatus
import com.altkamul.xpay.sealed.Screen
import com.altkamul.xpay.ui.theme.Dimension
import com.altkamul.xpay.utils.LoggedMerchantPref
import com.altkamul.xpay.utils.NetworkHelper
import com.altkamul.xpay.viewmodel.ParentViewModel
import com.altkamul.xpay.views.components.CustomStatusBar
import com.altkamul.xpay.views.components.DrawerLayout
import com.altkamul.xpay.views.components.MainTopBar
import com.altkamul.xpay.views.components.SecondaryTobBar
import kotlinx.coroutines.launch

/** a list contain all the main destination that the TopBar & the drawer appear in **/
val mainDestinations = listOf(
    Screen.Home,
    Screen.Settings,
    Screen.Reports,
    Screen.TransactionHistory,
    Screen.Reprint,
    Screen.Claim,
)

/** a list contain all the Secondary destination that the Secondary TopBar Contains  **/
val secondaryDestinations = listOf(
    Screen.ContactUs,
    Screen.ChangePassword,
    Screen.ChangeBranch,
    Screen.Cart,
    Screen.Languages,
    Screen.TestCenter,
    Screen.Accounts,
    Screen.NFCTest,
    Screen.QrReaderTest,
    Screen.QrMakerTest,
    Screen.InternetTest,
)

/** Graph's start destination , its the first screen that appear when open the app */
val startDestination = Screen.Home

/**
 * Screen holder that act as container for all the destinations exist in the app
 * It contains the NavHost with the routes of the destinations
 */

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScreenHolder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {

        val parentViewModel: ParentViewModel =
            hiltViewModel(LocalContext.current as ComponentActivity)


        /**
         * The network's status , it got observed as state along with the app's lifecycle
         * Whenever the status is changed , it also got updated everywhere it got used using our parent view model
         */
        val status = NetworkHelper(LocalContext.current).observeAsState()
        parentViewModel.updateNetworkStatus(status = status.value ?: NetworkStatus.Disconnected)

        val navController = rememberNavController()
        val scaffoldState = rememberScaffoldState()
        val scope = rememberCoroutineScope()
        val currentRoute = getActiveRoute(navController = navController)
        val searchState = remember {
            parentViewModel.homeSearchValue
        }

        /** A boolean that indicate whether or not to show transaction's filter dialog */
        val shouldShowTrxFilterButton = currentRoute == Screen.TransactionHistory.route
        var shouldShowTrxFilters by remember { mutableStateOf(false) }

        /** a boolean that indicate whether to show the topBar with the drawer or not **/
        val shouldShowMainComponents by remember {
            mutableStateOf(true)
        }.also {
            /** its value depends on the current active route of the navigation **/
            it.value =
                currentRoute in mainDestinations.map { screen -> screen.route }
        }

        /** a boolean that indicate whether to show the Secondary topBar  or not **/
        val shouldShowSecondaryComponents by remember {
            mutableStateOf(true)
        }.also {
            /** its value depends on the current active route of the navigation **/
            it.value =
                currentRoute in secondaryDestinations.map { screen -> screen.route }
        }
        /** Firstly we add the custom status bar */
        CustomStatusBar()
        /** Now creating the Scaffold that hold the topBar & the drawer - and also the navigation host with its routes **/
        Scaffold(
            /** the main topAppBar , only appear when it's #shouldShowMainComponents is true **/
            topBar = {
                if (shouldShowMainComponents) {
                    MainTopBar(
                        onDrawerClicked = {
                            /** Catch the event of drawer button clicked , so we can open the drawer **/
                            scope.launch {
                                scaffoldState.drawerState.open()
                            }
                        },
                        onlyDrawer = currentRoute != Screen.Home.route,
                        searchState = searchState.value,
                        onSearchQueryChanged = {
                            searchState.value = it
                        },
                        onProfileClicked = {
                            /** catch the event of clicking the profile image in the TopAppBar */
                        },
                        showFilterButton = shouldShowTrxFilterButton,
                        onFilterClicked = {
                            /** Catch the event of show/hide the transaction's filter dialog */
                            shouldShowTrxFilters = shouldShowTrxFilters.not()
                        }
                    )
                } else if (shouldShowSecondaryComponents) {
                    SecondaryTobBar(
                        onBackPressed = {
                            navController.popBackStack()
                        }
                    )
                }
            },
            /** scaffold state the contain all the states of the drawer , navigation - if exists - etc ... **/
            scaffoldState = scaffoldState,
            /** the navigation drawer **/
            drawerContent = {
                DrawerLayout(
                    /** Passing the current active route */
                    currentRoute = currentRoute,
                    /**
                     * Catching the event of an item clicked on the drawer menu so we can close the drawer
                     * Also updating the current route with the clicked one
                     */
                    onDrawerScreenClicked = { routeClicked ->
                        scope.launch {
                            scaffoldState.drawerState.close()
                        }

                        navController.navigate(route = routeClicked) {
                            this.popUpTo(Screen.Home.route) {
                                this.saveState = true
                                inclusive = false
                            }

                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onLogout = {
//                        parentViewModel.logoutUser()
                        /** Hide the drawer */
                        scope.launch {
                            scaffoldState.drawerState.close()
                        }
                        LoggedMerchantPref.user = null
                        navController.navigate(Screen.Login.route){
                            popUpTo(Screen.Home.route){
                                inclusive = true
                            }
                        }
                    }
                )
            },
            /** Now customize our Drawer layout */
            drawerBackgroundColor = MaterialTheme.colors.background,
            drawerElevation = Dimension.zero,
            drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
            drawerShape = RoundedCornerShape(size = Dimension.zero),
        ) {
            /** here we will create our NavHost , should be updated whenever a new destination is created **/
            NavHost(navController = navController, startDestination = startDestination.route) {
                composable(Screen.Splash.route) {
                    SplashScreen(navController)
                }
                composable(Screen.InitialSetup.route) {
                    InitialSetupScreen(navController)
                }
                composable(Screen.Login.route) {
                    LoginScreen(navController = navController)
                }
                composable(Screen.Languages.route) {
                    ChangeLanguageScreen()
                }
                composable(Screen.ContactUs.route) {
                    ContactUsScreen()
                }
                composable(Screen.Loading.route) {
                    LoadingScreen(navController = navController)
                }
                composable(Screen.Home.route) {
                    HomeScreen(navController, searchState.value)
                }
                composable(Screen.QuickAccess.route) {
                    QuickAccessScreen(navController = navController)
                }

                composable(Screen.Cart.route) {
                    CartScreen(navController)
                }
                composable(route = Screen.Reprint.route) {
                    ReprintScreen()
                }
                composable(route = Screen.TransactionHistory.route) {
                    TransactionHistoryScreen(filterDialogShown = shouldShowTrxFilters)
                }
                composable(route = Screen.Reports.route) {
                    ReportsScreen(controller = navController)
                }

                composable(route = Screen.Settings.route) {
                    SettingsScreen(navController)
                }
                composable(route = Screen.Accounts.route) {
                    AccountSettingsScreen()
                }
                composable(route = Screen.TestCenter.route) {
                    TestCenterScreen(navController = navController)
                }
                composable(route = Screen.ChangePassword.route) {
                    ChangePasswordScreen()
                }
                composable(route = Screen.ChangeBranch.route) {
                    ChangeBranch(navController = navController)
                }
                composable(route = Screen.Claim.route) {
                    ClaimScreen()
                }
                composable(route = Screen.QrMakerTest.route) {
                    QRMakerTestScreen()
                }
                composable(route = Screen.QrReaderTest.route) {
                    QRReaderTestScreen()
                }
                composable(route = Screen.NFCTest.route) {
                    NFCTestScreen()
                }
                composable(route = Screen.InternetTest.route) {
                    InternetTestScreen()
                }

            }
        }
    }
}

/**
 * A function that is used to get the active route in our Navigation Graph , should return the splash route if it's null
 */
@Composable
fun getActiveRoute(navController: NavHostController): String {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route ?: "splash"
}