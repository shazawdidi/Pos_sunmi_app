package com.altkamul.xpay.views.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.altkamul.xpay.R
import com.altkamul.xpay.ui.theme.Dimension
import com.altkamul.xpay.viewmodel.AccountsViewModel
import com.altkamul.xpay.views.components.AddNewAccountPopupScreen
import com.altkamul.xpay.views.components.ListOfAccount

@Composable
fun AccountSettingsScreen(accountsViewModel: AccountsViewModel = hiltViewModel()) {

    /** This Variable For Showing Add New Account Popup Screen*/
    var showingAddNewAccountPopupScreen by remember {
        mutableStateOf(false)
    }
    var isEditClicked by remember {
        mutableStateOf(false)
    }

    /** This Variable For Remember Operation Button Content*/
    val addAccountButtonText =
        if (showingAddNewAccountPopupScreen) "Save Account" else "Add Account"

    /** Parent Layout*/
    Column(
        Modifier
            .fillMaxSize()
            .padding(
                top = Dimension.smLineMargin,
            ),
    ) {
        /** These Variable For Add Account*/
        val accountNameValue = remember {
            mutableStateOf("")
        }
        val accountTypeValue = remember {
            mutableStateOf("")
        }
        val passwordValue = remember {
            mutableStateOf("")
        }
        val confirmPasswordValue = remember {
            mutableStateOf("")
        }
        /** Case showingAddNewAccountPopupScreen == true Showing AddNewAccountPopupScreen()*/
        if (showingAddNewAccountPopupScreen)
            AddNewAccountPopupScreen(
                accountsViewModel,
                accountNameValue = accountNameValue,
                accountTypeValue = accountTypeValue,
                passwordValue = passwordValue,
                confirmPasswordValue = confirmPasswordValue
            ) {
                showingAddNewAccountPopupScreen = false
            }
        else
            ListOfAccount(accountsViewModel) { showingAddButton ->
                isEditClicked = !showingAddButton
            }

        Box(
            modifier = Modifier
                .weight(0.15f)
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
                .padding(
                    end = Dimension.lgLineMargin,
                    start = Dimension.lgLineMargin,
                ),
            contentAlignment = Alignment.Center
        ) {
            /** Disable The Add Button If User Trying To Edit Another User*/
            if (!isEditClicked)
            /** This Button To Confirm Operation*/
                Button(
                    onClick = {
                        if (addAccountButtonText == "Add Account")
                            showingAddNewAccountPopupScreen = true

                        if (addAccountButtonText == "Save Account")
                            accountsViewModel.addNewUser(
                                name = accountNameValue.value,
                                type = accountTypeValue.value,
                                password = passwordValue.value,
                                confirmPassword = confirmPasswordValue.value
                            ) {
                                showingAddNewAccountPopupScreen = false
                                confirmPasswordValue.value = ""
                                accountNameValue.value = ""
                                accountTypeValue.value = ""
                                passwordValue.value = ""
                            }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(TextFieldDefaults.MinHeight),
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)
                ) {
                    Text(
                        modifier = Modifier,
                        text = addAccountButtonText,
                        style = MaterialTheme.typography.h5, color = MaterialTheme.colors.surface
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add_account),
                        contentDescription = "Add Account",
                        modifier = Modifier.size(Dimension.md)
                    )

                }
        }
    }
}
