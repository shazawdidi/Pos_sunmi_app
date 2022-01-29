package com.altkamul.xpay.utils

import android.util.LayoutDirection
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.core.text.layoutDirection
import com.altkamul.xpay.R
import com.altkamul.xpay.model.*
import com.altkamul.xpay.model.response.local.TransactionWithItems
import com.altkamul.xpay.sealed.DiscountType
import com.altkamul.xpay.sealed.ServerResponse
import com.altkamul.xpay.ui.theme.Dimension
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.statement.*
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

/** An extension function that is used to convert the API response to a JSONObject & return the field message from it */
suspend fun HttpResponse.getMessage(): String {
    /** The json string */
    val responseAsString = this.receive<String>()
    /** convert the json string to a JSONObject that we can extract the message from it */
    return try {
        val jsonObj = JSONObject(responseAsString)
        jsonObj.getString("message") ?: "No message provided !"
    } catch (exception: JSONException) {
        "Server error, please call the support!"
    }

}

/**
 * An extension function that is used to handle the exception that occur when fetching from server
 * it send the report log - later - to server and return a valid response
 */
suspend fun <T> Throwable.handleResponseException(): ServerResponse<T> {
    return when (this) {
        is RedirectResponseException -> {
            ServerResponse.Error(message = this.response.getMessage())
        }
        is ClientRequestException -> {
            ServerResponse.Error(message = this.response.getMessage())
        }
        is ServerResponseException -> {
            ServerResponse.Error(message = this.response.getMessage())
        }
        else -> {
            ServerResponse.Error(message = "Unknown error ! ${this.message}")
        }
    }
}

/** An extension function that extend a list of data versions and return the version of the table taken as parameter */
fun List<DataVersion>.getVersionNumber(tableName: String) =
    find { it.table == tableName }?.version ?: -1


/** An extension function that is used to mirror the compose icons when using rtl languages like arabic and urdu */
fun Modifier.mirror(): Modifier {
    return when (Locale.getDefault().layoutDirection) {
        /** If app layout direction is rtl , then flip our icon horizontally (as a mirror) */
        LayoutDirection.RTL -> this.scale(scaleX = -1f, scaleY = 1f)
        /** If is ltr , just forget about this amigo ! */
        else -> this
    }
}

/** An extension function to map subcategories and items to a category instance */
fun List<Category>.mapSubCategoriesWithItems(
    subcategories: List<SubCategory>,
    items: List<Item>
): List<Category> {
    /** Then structuring all this data into one instance so we can use it straightforwardly from our screen */
    this.forEach { category ->
        /** Iterate over each category we had and get the corresponding subcategories */
        subcategories.filter { subCategory ->
            subCategory.categoryId == category.categoryId
        }.also { categorySubs ->
            if (categorySubs.isEmpty()) {
                /** If this category's subcategories still empty, we should forget about it's subcategories items too */
                Timber.d("Category with name ${category.categoryNameEN} had no subcategories !")
            } else {
                /**
                 * We are sure that this category had a subcategories
                 * We should re-structure each category's subcategories so that each subcategory had a list of items
                 */
                category.subcategories = categorySubs
                categorySubs.forEach { subCategory ->
                    /** Iterate over each subcategory and fill it with corresponding items */
                    items.filter { item ->
                        item.subCategoryId == subCategory.subCategoryId
                    }.also { subcategoryItems ->
                        if (subcategoryItems.isEmpty()) {
                            /** Unfortunately,this subcategory had no items */
                            Timber.d("subcategory ${subCategory.subCategoryNameEN} had no items !")
                        } else {
                            /**
                             * We are sure that this subcategory had items now
                             * We should push all this items to the current subcategory
                             */
                            subCategory.items = subcategoryItems
                        }
                    }
                }
            }
        }
    }
    return this
}

/**
 *  An extension function that is used to append an element to a list - or remove it in case it already exist.
 * Return the element if added or null if removed
 */
fun <T> MutableList<T>.appendOrRemove(element: T): T? {
    remove(element).also { removed ->
        return if (removed) {
            /** Removed successfully */
            null
        } else {
            /** Not exist, we should add it */
            this.add(element = element)
            element
        }
    }
}

/** An extension function on Date's object that is used to get a formatted date & time.
 * It takes the pattern that you want.
 * Shortcuts: yyyy: year , MM: month , dd: day , HH: hour , mm: minutes.
 */
fun Date.getFormattedDate(pattern: String): String {
    val simpleDateFormat = SimpleDateFormat(pattern, Locale.ENGLISH)
    return simpleDateFormat.format(this)
}

/** An extension function that is used to convert the px values to a valid Dp */
@Composable
fun Int.getDp() : Dp{
    val px = this
    with(LocalDensity.current){
        Timber.d("density is ${this.density}")
        return px.toDp()
    }
}

/** An extension function that return the price after applying the discount */
fun Double.getPriceAfterDiscount(
    discountType: DiscountType,
    discountValue: Double,
    qty: Int,
) : Double{
    return when(discountType){
        is DiscountType.ByPercent -> {
            qty * (this * (1 - (discountValue / 100.0) ))

        }
        is DiscountType.ByValue -> {
            qty * (this - discountValue)
        }
        else ->{
            /** When discount's type is not percent nor value , return the normal price */
            qty * this
        }
    }
}

fun Item.getName(): String {
    return when (LoggedMerchantPref.lang) {
        "en" -> this.itemNameEN ?: "English name"
        "ar" -> this.itemNameAR ?: "arabic name"
        "tr" -> this.itemNameTR ?: "turkish name"
        "ur" -> this.itemNameEN ?: "urdu name"
        "fr" -> this.itemNameFR ?: "french name"
        else -> "No lang"
    }
}

fun Item.getSubcategoryName(): String {
    return when (LoggedMerchantPref.lang) {
        "en" -> this.subCategoryNameEN ?: "English name"
        "ar" -> this.subCategoryNameAR ?: "arabic name"
        "tr" -> this.subCategoryNameTR ?: "turkish name"
        "ur" -> this.subCategoryNameEN ?: "urdu name"
        "fr" -> this.subCategoryNameFR ?: "french name"
        else -> "No lang"
    }
}

fun Category.getName(): String {
    return when (LoggedMerchantPref.lang) {
        "en" -> this.categoryNameEN ?: "English name"
        "ar" -> this.categoryNameAR ?: "arabic name"
        "tr" -> this.categoryNameTR ?: "turkish name"
        "ur" -> this.categoryNameEN ?: "urdu name"
        "fr" -> this.categoryNameFR ?: "french name"
        else -> "No lang"
    }
}

fun SubCategory.getName(): String {
    return when (LoggedMerchantPref.lang) {
        "en" -> this.subCategoryNameEN ?: "English name"
        "ar" -> this.subCategoryNameAR ?: "arabic name"
        "tr" -> this.subCategoryNameTR ?: "turkish name"
        "ur" -> this.subCategoryNameEN ?: "urdu name"
        "fr" -> this.subCategoryNameFR ?: "french name"
        else -> "No lang"
    }
}

/** A function that is used to decrease a dp by a rate ( from 0 to 1) from the 8 grid
 * For example : 24 decreased by 0.5 is equal to 24 - 4 = 20.dp
 */
infix fun Dp.decreaseBy(value: Float) : Dp{
    return this - (Dimension.xs * value)
}

/** A function that is used to increase a dp by a rate ( from 0 to 1) from the 8 grid
 * For example : 24 increased by 0.5 is equal to 24 + 4 = 28.dp
 */
infix fun Dp.increaseBy(value: Float) : Dp{
    return this + (Dimension.xs * value)
}

fun Double.roundToTwoDecimal(): Double {
    val number3digits: Double = (this * 1000.0).roundToInt() / 1000.0
    return (number3digits * 100.0).roundToInt() / 100.0
}

fun Double.roundToOneDecimal(): Double {
    val number2digits: Double = (this * 100.0).roundToInt() / 100.0
    return (number2digits * 10.0).roundToInt() / 10.0
}

/** Get a structured transactions */
fun TransactionWithItems.getStructureTransaction() : Transaction{
    return this.transaction.also {
        it.transactionDetail = this.items
        it.transactionPayment = this.payments
    }
}

@Composable
fun String.getStructuredDate() : String{
    /**
     * converting string month to int month
     * date had the format yyyy-mm-dd , so month will be at the second position when splatted
     */
    val date = this.split("-")
    val day = date[2].toInt()
    val month = date[1].toInt()
    val year = date[0].toInt()
    /** Making sure that month passed is valid month */
    if(month > 12) return "$day $month, $year"
    val months = stringArrayResource(id = R.array.months)
    return "$day ${months[month - 1]}, $year"
}

@Composable
fun String.get12SystemHour(): String {
    /** converting string hour to int hour */
    val hour = this.split(":").first().toInt()
    val minutes = this.split(":").last().toInt()
    return when(hour){
        0 -> stringResource(id = R.string.x_am,"${hour + 12}:$minutes")
        in 1..11 -> stringResource(id = R.string.x_am, "$hour:$minutes")
        else -> stringResource(id = R.string.x_pm, "${hour - 12}:$minutes")
    }
}