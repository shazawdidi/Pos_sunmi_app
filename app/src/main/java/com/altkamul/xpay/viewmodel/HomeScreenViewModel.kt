package com.altkamul.xpay.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altkamul.xpay.model.CartItem
import com.altkamul.xpay.model.Category
import com.altkamul.xpay.model.Item
import com.altkamul.xpay.model.SubCategory
import com.altkamul.xpay.repositroy.HomeScreenRepository
import com.altkamul.xpay.sealed.DiscountType
import com.altkamul.xpay.utils.LoggedMerchantPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(private val homeScreenRepository: HomeScreenRepository) :
    ViewModel() {


    /** All Local Category List*/
    private var _category = MutableLiveData<List<Category>>()
    val category: LiveData<List<Category>> = _category

    /** First List Of SubCategory Depend On Selected Category*/
    private var _currentSubCategorySelected = MutableLiveData<List<SubCategory>>()
    val currentSubCategorySelected: LiveData<List<SubCategory>> = _currentSubCategorySelected

    /** This List For Temporary Used Saving SubCategory List Before Filtering The List*/
    private var temporarySubCategoryList = listOf<SubCategory>()

    /** First List Of Items Depend On Selected SubCategory*/
    private var _currentItemsSelected = MutableLiveData<List<Item>>()
    val currentItemsSelected: LiveData<List<Item>> = _currentItemsSelected

    /** This List For Temporary Used Saving Items List Before Filtering The List*/
    private var temporaryItemList = listOf<Item>()

    /** Quick Access Items */
    private var _quickAccessItems = MutableLiveData<List<Item>>()
    var quickAccessItems: LiveData<List<Item>> = _quickAccessItems

    /** Current SubCategory ID*/
    var currentSubCategoryID = mutableStateOf(0)

    /** The Selected Item For Card*/
    var selectedCartItem = mutableListOf<CartItem>()
    var selectedItem = mutableListOf<Item>()


    /** Total Price To Showing It In UI*/
    val totalPrice = mutableStateOf(0.0)

    /** Also The Quantity To Showing It In UI*/
    val quantity = mutableStateOf(0)

    /** To Allow Search Bar Searching In SubCategory List*/
    val searchBySubCategory = mutableStateOf(true)

    /** This Variable Determined The Current Selected SubCategory Tab*/
    val currentSelectedCategoryTab = mutableStateOf(0)

    /** This Function Will Return Category And SubCategory And Items*/
    fun getCategoryAndSubCategoryAndItem() {
        viewModelScope.launch {
            val category = homeScreenRepository.getAllCategory()
            val subCategory = homeScreenRepository.getAllSubCategory()
            val items = homeScreenRepository.getAllItems()
            val data = mapCategoryWithSubCategoryAndItem(
                category = category,
                subCategory = subCategory,
                item = items
            )
            _category.value = data
            data.first().subcategories?.let {
                assigningCurrentSubCategory(it)
            }
            val quickAccessItemsWithoutData = homeScreenRepository.getAllQuickAccessItems()
            _quickAccessItems.value = items.filter { item ->
                quickAccessItemsWithoutData.any { quickItem ->
                    quickItem.itemId == item.itemId
                }
            }
        }

    }

    /** This Function For Getting Quick Access Locally*/
    fun getQuickAccessItems() {
        viewModelScope.launch {
            val quickAccessItemsWithoutData = homeScreenRepository.getAllQuickAccessItems()
            val items = homeScreenRepository.getAllItems()
            _quickAccessItems.value = items.filter { item ->
                quickAccessItemsWithoutData.any { quickItem ->
                    quickItem.itemId == item.itemId
                }
            }
        }
    }

    /** This Function For Filtering Base On Search Bar*/
    fun filteringBaseOnSearchField(filterBY: String) {
        if (searchBySubCategory.value) {
            _currentSubCategorySelected.value = temporarySubCategoryList
            _currentSubCategorySelected.value = _currentSubCategorySelected.value?.filter {
                it.subCategoryNameEN?.startsWith(filterBY) == true
            }

            if (filterBY.isEmpty()) {
                _currentSubCategorySelected.value = temporarySubCategoryList
            }
        } else {
            _currentItemsSelected.value = temporaryItemList
            _currentItemsSelected.value = _currentItemsSelected.value?.filter {
                it.itemNameEN?.startsWith(filterBY) == true
            }
            if (filterBY.isEmpty())
                _currentItemsSelected.value = temporaryItemList
        }

    }

    /** This Function Will Map One Category With There Specific SubCategory And Item BY ID*/
    private fun mapCategoryWithSubCategoryAndItem(
        category: List<Category>,
        subCategory: List<SubCategory>,
        item: List<Item>,
    ): List<Category> {
        category.forEach { categoryNested1 ->
            categoryNested1.subcategories = subCategory.filter { subCategoryNested1 ->
                categoryNested1.categoryId == subCategoryNested1.categoryId
            }.onEach { subCategoryNested2 ->
                subCategoryNested2.items = item.filter { item ->
                    item.subCategoryId == subCategoryNested2.subCategoryId
                }
            }
        }
        return category
    }

    /** This Function For Assigning Selected Sub Category To There Live Data Object*/
    fun assigningCurrentSubCategory(subCategory: List<SubCategory>) {
        _currentSubCategorySelected.value = subCategory
        temporarySubCategoryList = subCategory
    }

    /** This Function For Assigning Selected Items To There Live Data Object*/
    fun assigningCurrentItems(item: List<Item>) {
        _currentItemsSelected.value = item
        temporaryItemList = item
    }

    /** Adding Items To Cart*/
    fun addItemToCart(item: Item) {
        if (selectedCartItem.any { it.itemId == item.itemId }) {
            selectedCartItem.removeAll {
                it.itemId == item.itemId
            }
            totalPrice.value -= item.facePrice ?: 0
            selectedItem.remove(item)
        } else {
            val configurations = LoggedMerchantPref.configuration
            val merchantDiscountType: DiscountType =
                configurations?.discountType ?: DiscountType.Both
            val cartItem = CartItem(
                itemId = item.itemId ?: 0,
                qty = 1,
                realPrice = item.facePrice?.toDouble() ?: 0.0,
                totalPrice = item.facePrice?.toDouble() ?: 0.0,
                maxDiscount = item.discount ?: 0.0,
                discountType = if (merchantDiscountType == DiscountType.Both) DiscountType.ByValue() else merchantDiscountType,
                discountValue = 0.0
            )
            selectedCartItem.add(cartItem)
            totalPrice.value += item.facePrice ?: 0
            selectedItem.add(item)
        }

        quantity.value = selectedCartItem.size
    }

    /** Resetting All Variable After Going To Cart*/
    fun resetVariables() {
        totalPrice.value = 0.0
        quantity.value = 0
        selectedCartItem.clear()
        selectedItem.clear()
        _category.value = emptyList()
        _quickAccessItems.value = emptyList()
        _currentItemsSelected.value = emptyList()
        _currentSubCategorySelected.value = emptyList()
    }

    fun assigningCartListToHomeList(cartItem: MutableList<CartItem>, item: MutableList<Item>) {
        cartItem.forEach {
            totalPrice.value += it.totalPrice
        }
        quantity.value = cartItem.size
        selectedCartItem.addAll(cartItem)
        selectedItem.addAll(item)
    }
}