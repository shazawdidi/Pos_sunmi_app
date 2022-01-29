package com.altkamul.xpay.views.screens

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.altkamul.printer.TkamulPrinterBase
import com.altkamul.printer.core.Config
import com.altkamul.printer.models.config.PrinterType
import com.altkamul.xpay.R
import com.altkamul.xpay.sealed.Screen
import com.altkamul.xpay.ui.theme.Dimension
import com.altkamul.xpay.ui.theme.FontSize
import com.altkamul.xpay.ui.theme.gray
import com.altkamul.xpay.utils.Common
import com.altkamul.xpay.utils.Constants.largeDevicesRange
import com.altkamul.xpay.utils.ScreenDimensions
import com.altkamul.xpay.utils.largerThan
import com.altkamul.xpay.viewmodel.ParentViewModel
import com.altkamul.xpay.views.components.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TestCenterScreen(
    navController: NavHostController,
) {
    val context = LocalContext.current
    val parentViewModel: ParentViewModel = viewModel(context as ComponentActivity)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .padding(Dimension.pagePadding)
        ) {
            Column {
                Text(
                    text = stringResource(R.string.test_center),
                    style = MaterialTheme.typography.h2,
                    color = MaterialTheme.colors.secondaryVariant,
                )
                Spacer(modifier = Modifier.height(Dimension.xs))
                Text(
                    text = stringResource(R.string.test_center_slogan),
                    style = MaterialTheme.typography.subtitle2,
                    fontSize = FontSize.md,
                    color = gray
                )
            }
            Spacer(modifier = Modifier.height(Dimension.pagePadding))
            Column {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimension.pagePadding)
                ) {
                    /** This button will only be displayed when the device has a printer */
                    val deviceModel = Build.MODEL
                    val checkIfDeviceHadPrinter = Config.NameOfPrinterDevice.contains(deviceModel)
                    if (checkIfDeviceHadPrinter) {
                        Printer(
                            modifier = Modifier.weight(1f),
                            onClicked = {
                                /** Just print something */
                                parentViewModel.callPrinter2()
                            }
                        )
                    }
                    NfcTester(
                        modifier = Modifier.weight(1f),
                        onClicked = {
                            Common.createToast(
                                context = context,
                                message = "Working on this , sorry !"
                            )
//                            navController.navigate(Screen.NFCTest.route)
                        }
                    )
                }
                Spacer(modifier = Modifier.height(Dimension.pagePadding))
                if (
                    (ScreenDimensions.Width largerThan largeDevicesRange.first.dp)
                ) {
                    /** Only show customer screen when it's sunmi with the large screen */
                    CustomerScreen(
                        modifier = Modifier.fillMaxWidth(),
                        onClicked = {
                            /** What should we do to test customer screen ? */
                            Common.createToast(
                                context = context,
                                message = "Working on this , sorry !"
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(Dimension.pagePadding))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(Dimension.pagePadding)) {
                    QrMakerTester(
                        modifier = Modifier.weight(1f),
                        onClicked = {
                            navController.navigate(Screen.QrMakerTest.route)
                        }
                    )
                    QrReaderTester(
                        modifier = Modifier.weight(1f),
                        onClicked = {
                            navController.navigate(Screen.QrReaderTest.route)
                        }
                    )
                }
                Spacer(modifier = Modifier.height(Dimension.pagePadding))
                InternetTester(
                    onClicked = {
                        navController.navigate(Screen.InternetTest.route)
                    }
                )
            }
        }
    }
}