package com.altkamul.xpay.views.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.altkamul.xpay.R
import com.altkamul.xpay.ui.theme.Dimension
import com.altkamul.xpay.ui.theme.FontSize
import com.altkamul.xpay.utils.Common
import com.altkamul.xpay.viewmodel.ChangePasswordViewModel
import com.altkamul.xpay.views.components.CustomButton
import com.altkamul.xpay.views.components.PasswordsFieldsSection

@Composable
fun ChangePasswordScreen(
    changePasswordViewModel: ChangePasswordViewModel = hiltViewModel(),
//    navController: NavController
) {
    val isLoading by remember { changePasswordViewModel.isLoading }
    val newPassword = remember {
        changePasswordViewModel.newPassword
    }
    val oldPassword = remember {
        changePasswordViewModel.oldPassword
    }
    val confirmPassword = remember {
        changePasswordViewModel.confirmPassword
    }

    val context = LocalContext.current
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

    /** Screen content */
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(start = Dimension.pagePadding, end = Dimension.pagePadding, bottom = Dimension.pagePadding)
    ) {
        Text(
            text = stringResource(R.string.change_password),
            style = MaterialTheme.typography.h2,
            color = MaterialTheme.colors.secondaryVariant,
        )
        Text(
            text = stringResource(R.string.change_password_slogan),
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.secondaryVariant.copy(alpha = 0.7f),
        )
        Spacer(modifier = Modifier.height(Dimension.md))
        PasswordsFieldsSection(
            oldPassword = oldPassword.value,
            newPassword = newPassword.value,
            confirmPassword = confirmPassword.value,
            changePasswordViewModel = changePasswordViewModel
        )
        Spacer(modifier = Modifier.weight(1f))
        /** Updating password button */
        CustomButton(
            modifier = Modifier.fillMaxWidth(),
            buttonColor = MaterialTheme.colors.secondary,
            contentColor = MaterialTheme.colors.onSecondary,
            text = stringResource(R.string.change_password),
            onButtonClicked = {
                changePasswordViewModel.updatePassword(
                    onValidateFailed = {
                        Common.createToast(context = context, message = it)
                    },
                    onPasswordUpdated = {
                        /** What shall we do when password is update ? */
                    },
                    onPasswordUpdateFailed = {
                        Common.createToast(context = context, message = it)
                    }
                )
            }
        )
    }

}