package com.altkamul.xpay.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altkamul.xpay.model.Category
import com.altkamul.xpay.model.QuickItem
import com.altkamul.xpay.repositroy.ParentRepository
import com.altkamul.xpay.repositroy.QuickAccessRepository
import com.altkamul.xpay.utils.LoggedMerchantPref
import com.altkamul.xpay.utils.appendOrRemove
import com.altkamul.xpay.utils.getFormattedDate
import com.altkamul.xpay.utils.mapSubCategoriesWithItems
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class QuickAccessViewModel @Inject constructor(
    private val parentRepository: ParentRepository,
    private val quickAccessRepository: QuickAccessRepository,
): ViewModel() {


    /** Our structured data */
    private val _data = MutableLiveData<List<Category>>()
    val data: LiveData<List<Category>> = _data

    /** Selected products list */
    val selectedItems: MutableList<Int> = mutableStateListOf()

    /** Indicate whether or not the submit button should be clickable */
    var submitEnabled = mutableStateOf(false)

    init {
        Timber.d("Getting the structured data ... ")
        getBranchCategoriesWithData()
    }

    /** A function to update the list of the selected items - whether adding or removing */
    fun updateSelectedItems(itemId: Int){
        selectedItems.appendOrRemove(itemId)
        /** submit is enable only if the items selected count is larger than 0 */
        submitEnabled.value = selectedItems.size  > 0
    }

    /** Storing the selected items - invoked when clicking submit button */
    fun saveQuickAccessItems(onSaveCompleted: () -> Unit){
        val items = selectedItems.mapTo(destination = mutableListOf()) { itemId->
            QuickItem(
                itemId = itemId,
                branchId = LoggedMerchantPref.branch?.id ?: "Will/Should not happened !",
                date = Date().getFormattedDate("yyyy-MM-dd HH:mm")
            )
        }
        /** Now what, Fire ! */
        viewModelScope.launch {
            quickAccessRepository.saveQuickAccessItems(items = items)
            /** After it got finished , pass the completion event up */
            onSaveCompleted()
        }
    }
    private fun getBranchCategoriesWithData() {
        viewModelScope.launch {
            /** First we should get Categories */
            val categories = parentRepository.getAllCategories()
            /** Then we should get Subcategories */
            val subcategories = parentRepository.getAllSubcategories()
            /** Finally we should get items */
            val items = parentRepository.getAllItems()
            /** Now it's time to expose our structured categories */
            _data.value = categories.mapSubCategoriesWithItems(subcategories, items)
        }
    }

}