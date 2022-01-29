package com.altkamul.xpay.ui.theme

import androidx.compose.ui.unit.dp

object Dimension {
  /**
   * All this values are used somewhere in the app
   * Changing this values will mess up all the UI that are used in
   * If you need another values , just add it in the end of this file and give them a proper names
   */
  val zero = 0.dp

  /** The dimension's grid that we are using through out the app */
  val xs = 8.dp

  val sm = 16.dp
  val md = 24.dp
  val lg = 32.dp
  val xl = 40.dp
  val xxl = 48.dp
  val xxxl = 72.dp

  val pagePadding = sm
  val fullPadding = 20.dp

  val xsLineMargin = 4.dp
  val smLineMargin = xs
  val mdLineMargin = sm
  val lgLineMargin = md

    val smIconSize = md
    val mdIconSize = lg
    val lgIconSize = xl

  val topBarProfileSize = xxl
  val drawerProfileSize = xxxl
  val loginProfileSize = 240.dp
  val versionPadding = 130.dp
  val buttonWidth = 150.dp
  val versionPaddingSt = 70.dp

  val hoverEffectPadding = 4.dp
  val loadingPadding = 15.dp
  val elevation = 3.dp
  val surfaceElevation = 5.dp
  val alpha = 0.8f
}