package com.altkamul.xpay.views.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.altkamul.xpay.R
import com.altkamul.xpay.model.Cashiers
import com.altkamul.xpay.sealed.NetworkStatus
import com.altkamul.xpay.ui.theme.*
import com.altkamul.xpay.utils.Common
import com.altkamul.xpay.viewmodel.AccountsViewModel
import com.altkamul.xpay.viewmodel.ParentViewModel


@Composable
fun ColumnScope.ListOfAccount(
    accountsViewModel: AccountsViewModel,
    isEditClicked: (showAddButton: Boolean) -> Unit
) {
    val cashiers by accountsViewModel.cashiers.observeAsState()
    LazyColumn(
        modifier = Modifier
            .weight(0.85f)
            .padding(
                end = Dimension.lgLineMargin,
                start = Dimension.lgLineMargin,
            )
            .fillMaxWidth()
    ) {

        item {
            Text(
                text = stringResource(R.string.account),
                style = MaterialTheme.typography.h2.copy(color = MaterialTheme.colors.secondaryVariant)
            )
            Text(
                modifier = Modifier.padding(bottom = Dimension.sm.plus(Dimension.xs.div(4))),
                text = stringResource(R.string.accounts_slogan),
                style = MaterialTheme.typography.h6.copy(color = lightGray)
            )
        }
        cashiers?.let {
            it.forEachIndexed { index, cashier ->
                item {
                    var showingAccount by remember {
                        mutableStateOf(true)
                    }
                    if (showingAccount)
                        AccountListExpand(
                            cashiers = cashier,
                            onExpandChange = {
                                showingAccount = false
                                isEditClicked(showingAccount)
                            },
                            position = index + 1
                        )
                    if (!showingAccount)
                        AccountDetails(cashier, accountsViewModel) {
                            showingAccount = true
                            isEditClicked(showingAccount)
                        }
                }
            }
        }
    }
}

/** This Function Will Draw Add New Account Popup Screen*/
@Composable
fun ColumnScope.AddNewAccountPopupScreen(
    accountsViewModel: AccountsViewModel,
    accountNameValue: MutableState<String>,
    accountTypeValue: MutableState<String>,
    passwordValue: MutableState<String>,
    confirmPasswordValue: MutableState<String>,
    onCloseAddAccountPopup: () -> Unit
) {
    /** Parent Layout*/
    Surface(
        modifier = Modifier
            .weight(0.8f)
            .fillMaxWidth(),
        color = MaterialTheme.colors.surface,
        shape = RoundedCornerShape(topStart = Dimension.sm, topEnd = Dimension.sm)
    ) {
        /** Sup Layout*/
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(Dimension.pagePadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimension.sm)
        ) {
            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = Dimension.xs),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .weight(0.25f), contentAlignment = Alignment.CenterStart
                    ) {
                        /** Text With Clickable Function If Clicked Will Disable Popup Screen*/
                        Text(
                            modifier = Modifier
                                .clickable {
                                    onCloseAddAccountPopup()
                                },
                            text = "Cancel",
                            style = MaterialTheme.typography.h6.copy(color = MaterialTheme.colors.primary)
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(0.5f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(
                            modifier = Modifier
                                .width(Dimension.xxl)
                                .height(Dimension.xs.div(2))
                                .clip(MaterialTheme.shapes.medium)
                                .background(color = lightGray)
                        )
                        Text(
                            text = "Add Account",
                            style = MaterialTheme.typography.h5.copy(color = MaterialTheme.colors.secondaryVariant)
                        )
                    }
                    Spacer(
                        modifier = Modifier
                            .weight(0.25f)
                    )
                }
            }
            item {

                TextWithTextField(
                    textValue = "Account Name",
                    textFieldValue = accountNameValue,
                    placeHolder = "Enter Account Name"
                )
            }
            item {

                val listOfRoles by accountsViewModel.roles.observeAsState()
                val rolesTitle = mutableListOf<String>()
                listOfRoles?.let {
                    it.forEach { posRole ->
                        rolesTitle.add(posRole.posRoleName ?: "No Data")
                    }
                }
                TextWithDropDownList(
                    textValue = "Account Type",
                    accountsType = rolesTitle
                ) {
                    accountTypeValue.value = it
                }
            }
            item {

                TextWithTextField(
                    textValue = "Password",
                    textFieldValue = passwordValue,
                    placeHolder = "Enter Account Password"
                )
            }
            item {

                val isError = remember {
                    mutableStateOf(passwordValue.value != confirmPasswordValue.value)
                }
                TextWithTextField(
                    textValue = "Confirm Password",
                    textFieldValue = confirmPasswordValue,
                    isError = isError, confirmPassword = passwordValue,
                    placeHolder = "Confirm Account Password"
                )
            }
        }
    }
}

@Composable
fun ColumnScope.TextWithDropDownList(
    textValue: String,
    accountsType: List<String>,
    onSelectedAccountType: (selectedAccountType: String) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .weight(1f),
        verticalArrangement = Arrangement.spacedBy(Dimension.sm.div(4))
    ) {
        Text(
            text = textValue,
            style = MaterialTheme.typography.h5.copy(color = MaterialTheme.colors.secondaryVariant)
        )
        val selectedAccountType = remember {
            mutableStateOf("Select Account Type")
        }
        DroppedDownList(list = accountsType, selectedText = selectedAccountType)
        onSelectedAccountType(selectedAccountType.value)

    }

}

@Composable
fun ColumnScope.TextWithTextField(
    textValue: String,
    textFieldValue: MutableState<String>,
    isError: MutableState<Boolean> = mutableStateOf(false),
    confirmPassword: MutableState<String> = mutableStateOf(""),
    placeHolder: String
) {
    Column(
        Modifier
            .fillMaxWidth()
            .weight(1f),
        verticalArrangement = Arrangement.spacedBy(Dimension.sm.div(4))
    ) {

        Text(
            text = textValue,
            style = MaterialTheme.typography.h5.copy(color = MaterialTheme.colors.secondaryVariant)
        )
        CustomInputField(
            value = textFieldValue.value,
            placeholder = placeHolder,
            isError = isError.value,
            onValueChange = {
                textFieldValue.value = it
                if (confirmPassword.value.isNotBlank())
                    isError.value = it != confirmPassword.value
            })
    }
}

@Composable
fun AccountListExpand(
    cashiers: Cashiers,
    onExpandChange: () -> Unit,
    position: Int,
) {
    val context = LocalContext.current
    val parentViewModel: ParentViewModel =
        hiltViewModel(context as ComponentActivity)
    val status = parentViewModel.network.observeAsState()
    val backGroundColor =
        if (status.value == NetworkStatus.Connected) Color.Transparent else MaterialTheme.colors.surface.copy(
            alpha = 0.62f
        )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimension.xxxl.times(2))
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = Dimension.sm.plus(Dimension.xs.div(4))),
            backgroundColor = MaterialTheme.colors.surface,
            elevation = Dimension.sm,
            shape = MaterialTheme.shapes.small
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = Dimension.sm, top = Dimension.sm, end = Dimension.sm
                    ),
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Account $position",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.primary
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = cashiers.userType ?: "No Data",
                        style = MaterialTheme.typography.h5.copy(color = darkGray)
                    )
                }

                Column(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.End,
                ) {
                    Text(
                        text = cashiers.userName ?: "No Data",
                        style = MaterialTheme.typography.body2.copy(color = darkGray)
                    )
                    Button(
                        onClick = {
                            onExpandChange()
                        },
                        modifier = Modifier
                            .height(Dimension.xxl)
                            .fillMaxWidth(0.5f),
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                        elevation = ButtonDefaults.elevation(
                            defaultElevation = Dimension.md,
                            pressedElevation = Dimension.md,
                            disabledElevation = 0.dp
                        )
                    ) {
                        Text(
                            text = "Edit",
                            style = MaterialTheme.typography.h6.copy(
                                color = MaterialTheme.colors.surface
                            )
                        )
                    }
                }


            }
        }
        if (status.value == NetworkStatus.Disconnected)
            Box(modifier = Modifier
                .padding(bottom = Dimension.sm.plus(Dimension.xs.div(4)))
                .fillMaxSize()
                .clip(MaterialTheme.shapes.small)
                .background(backGroundColor)
                .clickable {

                    Common.createToast(
                        context,
                        "You Are Offline Please Mack Suer Connecting To The Internet"
                    )
                })
    }

}


/** This Function Will Draw Account Details And Information*/
@Composable
fun AccountDetails(
    cashiers: Cashiers,
    accountsViewModel: AccountsViewModel, onCloseExpanding: () -> Unit
) {
    val context = LocalContext.current
    val parentViewModel: ParentViewModel =
        hiltViewModel(context as ComponentActivity)
    val status = parentViewModel.network.observeAsState()
    if (status.value == NetworkStatus.Disconnected)
        onCloseExpanding()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = Dimension.sm.plus(Dimension.xs.div(4))),
        backgroundColor = MaterialTheme.colors.surface,
        elevation = Dimension.sm,
        shape = MaterialTheme.shapes.small
    ) {

        val cashiersName = remember {
            mutableStateOf("")
        }
        val cashiersPassword = remember {
            mutableStateOf("")
        }
        val cashiersType = remember {
            mutableStateOf(cashiers.userType ?: "No data")
        }
        val showDeleteAccountPopupDialog = remember {
            mutableStateOf(false)
        }
        if (showDeleteAccountPopupDialog.value)
            DeleteAccountPopupDialog(showDeleteAccountPopupDialog) {
                accountsViewModel.deleteUser(it, cashiers)
                onCloseExpanding()
            }

        Column(
            modifier = Modifier
                .padding(Dimension.sm),
            verticalArrangement = Arrangement.spacedBy(Dimension.sm)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimension.sm)
            ) {
                cashiers.userName?.let {
                    CustomInputField(
                        modifier = Modifier.weight(0.6f),
                        value = cashiersName.value,
                        placeholder = it,
                        onValueChange = { newValue ->
                            cashiersName.value = newValue
                        }
                    )

                }
                CashierTypes(accountsViewModel, cashiersType)
            }
            cashiers.userPassword?.let {
                CustomInputField(
                    value = cashiersPassword.value,
                    placeholder = it,
                    onValueChange = { newValue ->
                        cashiersPassword.value = newValue
                    }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimension.sm)
            ) {
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .height(TextFieldDefaults.MinHeight),
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.buttonColors(backgroundColor = red),
                    onClick = {
                        showDeleteAccountPopupDialog.value = true
                    }
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        style = MaterialTheme.typography.button,
                        color = MaterialTheme.colors.surface
                    )
                }
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .height(TextFieldDefaults.MinHeight),
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary),
                    onClick = {
                        onCloseExpanding()
                        accountsViewModel.editUser(
                            name = cashiersName.value,
                            type = cashiersType.value,
                            password = cashiersPassword.value,
                            userID = cashiers.userId ?: "No data"
                        )
                    }
                ) {
                    Text(
                        text = stringResource(R.string.save),
                        style = MaterialTheme.typography.button,
                        color = MaterialTheme.colors.surface
                    )
                }
            }

        }
    }
}


/** This Function Will Draw Delete Account Popup Dialog*/
@Composable
fun DeleteAccountPopupDialog(
    showDeleteAccountPopupDialog: MutableState<Boolean>,
    onDeleteUser: (isDelete: Boolean) -> Unit
) {
    /** If onDismissAlertDialog == true Then We Will Show This Dialog*/
    Dialog(
        onDismissRequest = { showDeleteAccountPopupDialog.value = false },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        )
    ) {
        Surface(
            modifier = Modifier
                .width(342.dp)
                .height(264.dp),
            shape = RoundedCornerShape(10.dp),
            color = Color.White,
            elevation = 4.dp
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(Dimension.xs)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .weight(0.2f)
                ) {
                    /** Delete Image*/
                    Image(
                        modifier = Modifier.weight(1f),
                        painter = painterResource(id = R.drawable.delete_icon),
                        contentDescription = "Delete Icon"
                    )
                    /** Cancel Icon*/
                    Icon(
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                showDeleteAccountPopupDialog.value = false
                            },
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Cancel Icon",
                        tint = Color(0xFFd1d1d1)
                    )
                }
                /** This Parent Column For Displaying Tow Text*/
                Column(
                    Modifier
                        .padding(top = 8.dp, bottom = 8.dp)
                        .fillMaxWidth()
                        .weight(0.5f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Dimension.sm)

                ) {
                    Text(
                        text = "You are about to delete an account",
                        color = Color(0xFF363636),
                        style = MaterialTheme.typography.h5,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = "This will delete an account\n" +
                                "Are you sure ?",
                        textAlign = TextAlign.Center,
                        color = Color(0xFF5e5e5e),
                        style = MaterialTheme.typography.h6
                    )
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .weight(0.3f),
                    horizontalArrangement = Arrangement.spacedBy(Dimension.md)
                ) {
                    /** This Button For Canceling Deleting Account*/
                    Button(
                        onClick = {
                            onDeleteUser(false)
                            showDeleteAccountPopupDialog.value = false
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(TextFieldDefaults.MinHeight),
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface),
                        elevation = ButtonDefaults.elevation(Dimension.zero)
                    ) {
                        Text(
                            text = "Cancel",
                            style = MaterialTheme.typography.h6.copy(
                                color = Color(0xFF5e5e5e)
                            )
                        )
                    }
                    /** This Button For Deleting Account*/
                    Button(
                        onClick = {
                            onDeleteUser(true)
                            showDeleteAccountPopupDialog.value = false
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(TextFieldDefaults.MinHeight),
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors(backgroundColor = red)
                    ) {
                        Text(
                            text = "Delete",
                            style = MaterialTheme.typography.h6.copy(
                                color = MaterialTheme.colors.surface
                            )
                        )
                    }
                }

            }
        }
    }
}


@Composable
fun RowScope.CashierTypes(
    accountsViewModel: AccountsViewModel,
    cashiersType: MutableState<String>,
) {
    Box(Modifier.weight(0.4f)) {
        val listOfRoles by accountsViewModel.roles.observeAsState()
        val rolesTitle = mutableListOf<String>()
        listOfRoles?.let {
            it.forEach { posRole ->
                rolesTitle.add(posRole.posRoleName ?: "No Data")
            }
        }

        DroppedDownList(list = rolesTitle, selectedText = cashiersType)
    }
}


/** This Function For Draw Drop Down List*/
@Composable
fun DroppedDownList(list: List<String>, selectedText: MutableState<String>) {

    /** This Variable For Expanded Drop Down List*/
    var expanded by remember { mutableStateOf(false) }

    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown
    var rowSize by remember { mutableStateOf(Size.Zero) }

    Column(
        Modifier
            .fillMaxWidth()
    ) {
        Row(
            Modifier
                .padding(bottom = Dimension.xs.div(4))
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    //This value is used to assign to the DropDown the same width
                    rowSize = coordinates.size.toSize()
                }
                .height(TextFieldDefaults.MinHeight)
                .background(color = lightShadowOfGray, shape = MaterialTheme.shapes.small)
                .padding(horizontal = Dimension.xs)
                .clickable { expanded = !expanded },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = selectedText.value,
                style = MaterialTheme.typography.h6.copy(color = MaterialTheme.colors.secondaryVariant),
            )
            Icon(
                icon, "contentDescription", tint = MaterialTheme.colors.secondaryVariant
            )
        }
        MaterialTheme(shapes = MaterialTheme.shapes.copy(medium = RoundedCornerShape(2.dp))) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier

                    .width(with(LocalDensity.current) { rowSize.width.toDp() })
                    .background(color = MaterialTheme.colors.surface)
                    .shadow(elevation = Dimension.zero),
                properties = PopupProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            ) {
                list.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (selectedText.value == item) MaterialTheme.colors.background
                                else MaterialTheme.colors.surface
                            )
                            .height(TextFieldDefaults.MinHeight)
                            .clickable {
                                selectedText.value = item
                                expanded = false
                            }, verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (selectedText.value == item) {
                            Spacer(
                                modifier = Modifier
                                    .background(MaterialTheme.colors.primary)
                                    .width(Dimension.xs.div(2))
                                    .height(TextFieldDefaults.MinHeight)
                            )
                        }
                        Text(
                            modifier = Modifier.padding(start = Dimension.xs),
                            text = item,
                            style = MaterialTheme.typography.h6.copy(color = MaterialTheme.colors.secondaryVariant)
                        )
                    }
                }
            }
        }
    }
}