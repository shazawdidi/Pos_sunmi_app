package com.altkamul.xpay.views.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.altkamul.xpay.R
import com.altkamul.xpay.model.Branch
import com.altkamul.xpay.sealed.NetworkStatus
import com.altkamul.xpay.sealed.Screen
import com.altkamul.xpay.ui.theme.Dimension
import com.altkamul.xpay.ui.theme.lightGray
import com.altkamul.xpay.ui.theme.lightShadowOfGray
import com.altkamul.xpay.utils.Common
import com.altkamul.xpay.viewmodel.ChangeBranchViewModel
import com.altkamul.xpay.viewmodel.ParentViewModel
import com.altkamul.xpay.views.components.ShowDialog

@Composable
fun ChangeBranch(
    changeBranchViewModel: ChangeBranchViewModel = hiltViewModel(),
    navController: NavController, parentViewModel: ParentViewModel =
        hiltViewModel(LocalContext.current as ComponentActivity)
) {

    /** Getting Network Status IS Connected Or Dis*/
    val status = parentViewModel.network.observeAsState()

    /** Context Variable For Toasting User UI*/
    val context = LocalContext.current

    /** Parent Layout*/
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(Dimension.pagePadding)
    ) {
        /** This Variable For Saving Current Selected Text*/
        val choseBranch = stringResource(id = R.string.change_branch)
        val selectedText = remember { mutableStateOf(choseBranch) }

        /** This Variable For Observing Branches*/
        val branches by changeBranchViewModel.branches.observeAsState()

        val isLoading by remember {
            changeBranchViewModel.isLoading
        }
        if (isLoading)
            ShowDialog()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.8f)
        ) {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(Dimension.xs)
            ) {
                /** Primary Text*/
                Text(
                    text = stringResource(id = R.string.change_branch),
                    style = MaterialTheme.typography.h2.copy(color = MaterialTheme.colors.secondaryVariant)
                )
                /** Secondary Text*/
                Text(
                    text = stringResource(id = R.string.select_branch),
                    style = MaterialTheme.typography.h6.copy(color = lightGray)
                )
                /** This For Displaying Drop Down List Of Branches*/
                branches?.let { DroppedDownMenu(it, selectedText) }
            }
        }
        /** And This Box For Displaying Button For Operation Change Branch*/
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.2f), contentAlignment = Alignment.Center
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(TextFieldDefaults.MinHeight),
                onClick = {
                    if (status.value == NetworkStatus.Connected) {
                        if (selectedText.value != choseBranch)
                        /** Start Syncing Data And Reset Database*/
                            changeBranchViewModel.completeSyncingAndResetDatabase(
                                selectedBranch = selectedText.value
                            ) {
                                /** Navigate To Login Screen To Complete Change Process*/
                                navController.popBackStack()
                                navController.navigate(Screen.Login.route)
                            }
                    } else
                        Common.createToast(context, "Please Check Your Internet Connection")
                },

                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
            ) {
                Text(
                    text = stringResource(id = R.string.change_branch),
                    style = MaterialTheme.typography.h6.copy(color = MaterialTheme.colors.surface)
                )
            }
        }
    }
}

/** This Function For Draw Drop Down List*/
@Composable
fun DroppedDownMenu(branches: List<Branch>, selectedText: MutableState<String>) {

    /** This Variable For Expanded Drop Down List*/
    var expanded by remember { mutableStateOf(false) }

    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown
    var rowSize by remember { mutableStateOf(Size.Zero) }

    Column(
        Modifier
            .padding(top = Dimension.sm)
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
            verticalAlignment = CenterVertically
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
                branches.forEach { branch ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (selectedText.value == branch.name) MaterialTheme.colors.background
                                else MaterialTheme.colors.surface
                            )
                            .height(TextFieldDefaults.MinHeight)
                            .clickable {
                                selectedText.value = branch.name ?: ""
                                expanded = false
                            }, verticalAlignment = CenterVertically
                    ) {
                        if (selectedText.value == branch.name) {
                            Spacer(
                                modifier = Modifier
                                    .background(MaterialTheme.colors.primary)
                                    .width(Dimension.xs.div(2))
                                    .height(TextFieldDefaults.MinHeight)
                            )
                        }
                        Text(
                            modifier = Modifier.padding(start = Dimension.xs),
                            text = branch.name ?: "",
                            style = MaterialTheme.typography.h6.copy(color = MaterialTheme.colors.secondaryVariant)
                        )
                    }
                }
            }
        }
    }
}