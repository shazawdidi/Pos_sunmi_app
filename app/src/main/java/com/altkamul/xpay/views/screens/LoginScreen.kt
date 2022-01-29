package com.altkamul.xpay.views.screens

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.altkamul.xpay.R
import com.altkamul.xpay.sealed.Screen
import com.altkamul.xpay.ui.theme.Dimension
import com.altkamul.xpay.ui.theme.FontSize
import com.altkamul.xpay.ui.theme.lightGray
import com.altkamul.xpay.utils.Common
import com.altkamul.xpay.utils.Constants
import com.altkamul.xpay.viewmodel.LoginViewModel
import com.altkamul.xpay.viewmodel.ParentViewModel
import com.altkamul.xpay.views.components.CustomButton
import com.altkamul.xpay.views.components.CustomInputField
import com.altkamul.xpay.views.components.LoginMerchantDataSection
import com.altkamul.xpay.views.components.LoginTopBar
import timber.log.Timber

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginScreen(
    navController: NavHostController,
    loginViewModel: LoginViewModel = hiltViewModel()

) {
    val context = LocalContext.current

    /** Our shared parent view model */
    val parentViewModel: ParentViewModel = viewModel(context as ComponentActivity)
    val isLoading by remember { loginViewModel.isLoading }

    /** Making two layers , bottom is for content , top is for loading state */
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(
                start = Dimension.pagePadding,
                end = Dimension.pagePadding,
                bottom = Dimension.pagePadding,
            )
            .focusable(enabled = !isLoading)
            .alpha(alpha = if (isLoading) 0.6f else 1f),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        /** Boolean that control the show/hide process of the options menu */
        val optionsMenuExpanded = remember {
            loginViewModel.isOptionsMenuExpanded
        }

        /** Boolean that hold support login switch's state  */
        val isSupportLoginChecked = remember {
            loginViewModel.isSupportLoginChecked
        }

        /** A Boolean state used to handle the case of closing the app */
        val shouldExitApp = remember {
            mutableStateOf(false)
        }.also {
            if (it.value) {
                /** Getting our activity and actually closing the app */
                val activity = context as ComponentActivity
                activity.finish()
            }
        }
        val noUserFoundMsg = stringResource(R.string.no_user_found)

        /** UI Section */
        Column(
            modifier = Modifier
                .weight(3f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween,
        ){
            /** TopLoginBar with slug */
            Column {
                /**
                 * Login top Bar , contain title and the icon that show/hide option menu
                 * Catch the events of clicking the option menu button and selecting an option menu
                 */
                LoginTopBar(
                    expanded = optionsMenuExpanded.value,
                    isSupportLoginChecked = isSupportLoginChecked.value,
                    onMenuClicked = {
                        /** Option menu button clicked, inverse the current state */
                        loginViewModel.isOptionsMenuExpanded.value = !optionsMenuExpanded.value
                    },
                    onOptionClicked = { option ->
                        /** Hide login options menu */
                        loginViewModel.isOptionsMenuExpanded.value = false
                        when (option.route) {
                            null -> {
                                /**
                                 *  Here we are sure that it's not a destination we can navigate to
                                 *  It could be Balance or Exit menu options
                                 */
                                when (option.id) {
                                    1 -> {
                                        Common.createToast(context = context, message = option.title)
                                        navController.navigate(option.route ?: "")
                                    }
                                    2 -> {
                                        /** its Balance check option , should only show a dialog with the remained balance */
                                        Common.createToast(
                                            context = context,
                                            message = "Checking Balance ..."
                                        )
                                    }
                                    4 -> {
                                        /**
                                         * Support login switch's state changed , we should inverse it now B-)
                                         */
                                        loginViewModel.isSupportLoginChecked.value =
                                            !isSupportLoginChecked.value
                                    }
                                    6 -> {
                                        /** Its Exit option , should exit the app now */
                                        shouldExitApp.value = true
                                    }
                                }
                            }
                            else -> {
                                /** This option had a route , we can just navigate to it */
                                navController.navigate(option.route)
                            }
                        }
                    },
                )
                Spacer(modifier = Modifier.height(Dimension.xs))
                Text(
                    text = stringResource(R.string.login_slug_txt),
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.secondaryVariant.copy(alpha = 0.5f),
                )
            }
            /** Logged Merchant data */
            LoginMerchantDataSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimension.md)
            )
        }
        /** Then the password section */
        Column(
            modifier = Modifier
                .weight(2f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            val password = remember {
                loginViewModel.password
            }
            /** Password label */
            Text(
                text = stringResource(R.string.password),
                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Normal),
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(Dimension.pagePadding))
            /** Password Field */
            CustomInputField(
                backgroundColor = lightGray.copy(alpha = 0.3f),
                value = password.value,
                placeholder = stringResource(R.string.enter_password),
                keyboardType = KeyboardType.Number,
                visualTransformation = PasswordVisualTransformation(),
                onValueChange = {
                    /** Updating the password */
                    loginViewModel.password.value = it
                }
            )
            Spacer(modifier = Modifier.height(Dimension.pagePadding * 2))
            /** Login Button */
            CustomButton(
                modifier = Modifier
                    .fillMaxWidth(),
                buttonColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary,
                text = stringResource(R.string.submit),
                onButtonClicked = {
                    /** Now validating the data that we have , merchant data with user data */
                    loginViewModel.validatePassword(
                        onValidationFailed = {
                            Timber.d("Validation error !")
                            /** Show password too short error message */
                            Common.createToast(
                                context = context,
                                message = "Password is too short , should be at least " + Constants.MIN_PASSWORD_LENGTH + " digits"
                            )
                        },
                        onLoggedCompleted = { currentVersion ->
                            /** We should define the routes that the user can take when logged depending on some scenarios */
                            if (currentVersion == null) {
                                Timber.d("logged , going to loading")
                                /** Should delete the login page from the backstack */
                                navController.popBackStack()
                                /** Go to loading screen amigo ! */
                                navController.navigate(Screen.Loading.route)
                            } else {
                                Timber.d("logged , going to home")
                                /** Should delete the login page from the backstack */
                                navController.popBackStack()
                                /** Amigo , tell parent view model to check for updates and go straight to home screen */
                                parentViewModel.checkForDataUpdates()
                                navController.navigate(Screen.Home.route){
                                    launchSingleTop = true
                                }
                            }
                        },
                        onLoggingFailed = { reason ->
                            Timber.d("logging failed because $reason")
                            /** Show authentication error message when its not empty */
                            val message =
                                if (reason.contains("Unable to locate")) noUserFoundMsg else reason
                            Common.createToast(context = context, message = message)
                        }
                    )
                }
            )
        }

    }
    /** Loading Dialog, only visible when its loading */
    if (isLoading) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
            )
        ) {
            /** Loading dialog contents */
            Row(
                modifier = Modifier
                    .padding(horizontal = Dimension.pagePadding)
                    .clip(shape = MaterialTheme.shapes.small)
                    .background(MaterialTheme.colors.background)
                    .padding(horizontal = Dimension.md, vertical = Dimension.lg),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(Dimension.md),
                    strokeWidth = Dimension.hoverEffectPadding
                )
                Spacer(modifier = Modifier.width(Dimension.md))
                Text(
                    text = stringResource(R.string.please_wait),
                    style = MaterialTheme.typography.body1.copy(fontSize = FontSize.sm),
                    color = MaterialTheme.colors.onBackground.copy(alpha = 0.8f),
                )
            }
        }
    }
}