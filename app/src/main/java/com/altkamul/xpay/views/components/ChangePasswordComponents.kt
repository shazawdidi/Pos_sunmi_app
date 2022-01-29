package com.altkamul.xpay.views.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.altkamul.xpay.R
import com.altkamul.xpay.ui.theme.*
import com.altkamul.xpay.viewmodel.ChangePasswordViewModel

@Composable
fun PasswordsFieldsSection(
    oldPassword: String,
    newPassword: String,
    confirmPassword: String,
    changePasswordViewModel: ChangePasswordViewModel,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimension.xsLineMargin)
    ) {
        Spacer(modifier = Modifier.height(Dimension.xs))
        Text(
            text = stringResource(R.string.old_password),
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f)
        )
        /** Old Password Field */
        CustomInputField(
            value = oldPassword,
            placeholder = stringResource(R.string.enter_old_password),
            visualTransformation = PasswordVisualTransformation(),
            keyboardType = KeyboardType.Number,
            onValueChange = {
                /** Updating the Old Password */
                changePasswordViewModel.oldPassword.value = it
            }
        )
        Spacer(modifier = Modifier.height(Dimension.xs))
        Text(
            text = stringResource(R.string.new_password),
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f)
        )
        /** New Password Field */
        CustomInputField(
            value = newPassword,
            placeholder = stringResource(R.string.enter_new_password),
            visualTransformation = PasswordVisualTransformation(),
            keyboardType = KeyboardType.Number,
            onValueChange = {
                /** Updating the New Password */
                changePasswordViewModel.newPassword.value = it

            }
        )
        Spacer(modifier = Modifier.height(Dimension.xs))
        Text(
            text = stringResource(R.string.confirm_password),
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f)
        )
        /** Confirm Password Field */
        CustomInputField(
            value =confirmPassword,
            placeholder = stringResource(R.string.confirm_new_password),
            visualTransformation = PasswordVisualTransformation(),
            keyboardType = KeyboardType.Number,
            onValueChange = {
                /** Updating the Confirm Password */
                changePasswordViewModel.confirmPassword.value = it
            }
        )
    }

}