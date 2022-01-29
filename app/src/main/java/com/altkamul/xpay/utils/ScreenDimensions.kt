package com.altkamul.xpay.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import timber.log.Timber

sealed class ScreenDimensions{
    object Height: ScreenDimensions()
    object Width: ScreenDimensions()

    /** A sealed class that define the operations that we use to compare the dimensions */
    sealed class DimensionOperator{
        object LessThan: DimensionOperator()
        object GreaterThan: DimensionOperator()
        object EqualTo: DimensionOperator()
    }

    /** A class to validate the dimension */
    class DimensionValidator(
        val dimension: ScreenDimensions,
        val operator: DimensionOperator,
        val value: Dp
    ){
        fun compare(width: Dp, height: Dp) : Boolean{
            /** Check what we want to compare */
            return when(dimension){
                is Width -> {
                    /** Check what operator that we want to use */
                    when(operator){
                        is DimensionOperator.LessThan -> width < value
                        is DimensionOperator.GreaterThan -> width > value
                        is DimensionOperator.EqualTo -> width == value
                    }
                }
                is Height -> {
                    /** Check what operator that we want to use */
                    when(operator){
                        is DimensionOperator.LessThan -> height < value
                        is DimensionOperator.GreaterThan -> height > value
                        is DimensionOperator.EqualTo -> height == value
                    }
                }
            }
        }
    }
}

@Composable
infix fun ScreenDimensions.smallerThan(value: Dp) : Boolean {
    val screenSize = LocalContext.current.getScreenSize()
    return ScreenDimensions.DimensionValidator(
        dimension = this,
        operator = ScreenDimensions.DimensionOperator.LessThan,
        value = value
    ).compare(
        width = screenSize.width,
        height = screenSize.height,
    )
}

@Composable
infix fun ScreenDimensions.largerThan (value: Dp) : Boolean {
    val screenSize = LocalContext.current.getScreenSize()
    return ScreenDimensions.DimensionValidator(
        dimension = this,
        operator = ScreenDimensions.DimensionOperator.GreaterThan,
        value = value
    ).compare(
        width = screenSize.width,
        height = screenSize.height,
    )
}

@Composable
infix fun ScreenDimensions.equalTo(value: Dp) : Boolean {
    val screenSize = LocalContext.current.getScreenSize()
    return ScreenDimensions.DimensionValidator(
        dimension = this,
        operator = ScreenDimensions.DimensionOperator.EqualTo,
        value = value
    ).compare(
        width = screenSize.width,
        height = screenSize.height,
    )
}

@Composable
fun Context.getScreenSize() : Size {
    val currentScreenWidth = this.resources.displayMetrics.widthPixels.getDp()
    val currentScreenHeight = this.resources.displayMetrics.heightPixels.getDp()
    return Size(width = currentScreenWidth, height = currentScreenHeight)
}

data class Size(
    val width: Dp,
    val height: Dp,
)