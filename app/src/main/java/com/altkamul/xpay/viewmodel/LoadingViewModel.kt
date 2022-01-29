package com.altkamul.xpay.viewmodel


import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altkamul.xpay.R
import com.altkamul.xpay.model.*
import com.altkamul.xpay.repositroy.LoadingRepository
import com.altkamul.xpay.sealed.ServerResponse
import com.altkamul.xpay.utils.Common
import com.altkamul.xpay.utils.getVersionNumber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class LoadingViewModel @Inject constructor(
    private val loadingRepository: LoadingRepository,
    private val context: Context,
) : ViewModel() {

    /** This List Contain Screen Items Status and Text*/
    val downloadProgressText = listOf(
        context.getString(R.string.loading_dataVersion),
        context.getString(R.string.loading_categories),
        context.getString(R.string.loading_sub_categories),
        context.getString(R.string.loading_products),
        context.getString(R.string.complete)
    )
    val downloadProgressIsComplete = mutableStateListOf(false, false, false, false, false)
    var loadingProgressIndicator = mutableStateOf(0.0f)

    init {
        completeRecall()
    }

    /** This Function Will Behave Like Complete Recall In Old System*/
    private fun completeRecall() {
        viewModelScope.launch {
            getDataVersion()
        }
    }

    /** This Function Will GET DATA VERSION From SERVER */
    private suspend fun getDataVersion() {
        when (val response = loadingRepository.getDataVersion()) {
            is ServerResponse.Success -> {
                val data = response.data
                data?.let { insertDataVersionIntoDataBase(convertDataVersionToLocalDataVersion(it)) }
                downloadProgressIsComplete[0] = true
                loadingProgressIndicator.value = 0.2f
                getCategories()
            }
            is ServerResponse.Error -> {

                Common.createToast(context, "Error Data Version ${response.message}")
            }
        }
    }

    /** This Function Will Convert Remotely Data Version To Local*/
    private fun convertDataVersionToLocalDataVersion(data: List<DataVersion>): LocalDataVersions {
        return LocalDataVersions(
            items = data.getVersionNumber("items"),
            categories = data.getVersionNumber("categories"),
            subCategories = data.getVersionNumber("subcategories"),
        )
    }

    /** This Function Will GET ALL CATEGORIES From SERVER */
    private suspend fun getCategories() {
        when (val response = loadingRepository.getAllCategories()) {
            is ServerResponse.Success -> {
                val data = response.data
                data?.categories?.let {
                    insertCategoriesIntoDatabase(it)
                }
                downloadProgressIsComplete[1] = true
                loadingProgressIndicator.value = 0.4f
                getSubCategories()
            }
            is ServerResponse.Error -> {
                Common.createToast(context, "Error Category ${response.message}")
            }
        }
    }

    /** This Function Will GET ALL SUBCATEGORIES From SERVER */
    private suspend fun getSubCategories() {
        when (val response = loadingRepository.getAllSubCategories()) {
            is ServerResponse.Success -> {
                val data = response.data
                data?.subCategories?.let {
                    insertSubCategoryIntoDatabase(it)
                }
                downloadProgressIsComplete[2] = true
                loadingProgressIndicator.value = 0.6f
                getItems()
            }
            is ServerResponse.Error -> {
                Common.createToast(context, "Error Sub-Category ${response.message}")
            }
        }
    }

    /** This Function Will GET ALL ITEMS From SERVER */
    private suspend fun getItems() {
        when (val response = loadingRepository.getAllItem()) {
            is ServerResponse.Success -> {
                val data = response.data
                data?.items?.let {
                    insertItemsIntoDatabase(it)
                }
                downloadProgressIsComplete[3] = true
                loadingProgressIndicator.value = 0.8f
                complete()
            }
            is ServerResponse.Error -> {
                Common.createToast(context, "Error Products ${response.message}")
            }
        }
    }


    /** This Function Doesn't Do any Thing Except Delay The Screen 0.5 Second*/
    private suspend fun complete() {
        delay(500)
        downloadProgressIsComplete[4] = true
        loadingProgressIndicator.value = 1.0f
    }

    /** This Function Will Insert Data Version Locally*/
    private suspend fun insertDataVersionIntoDataBase(dataVersion: LocalDataVersions) {
        loadingRepository.insertDataVersion(dataVersion)
    }

    /** This Function Will Insert All Categories Locally */
    private suspend fun insertCategoriesIntoDatabase(category: List<Category>) {
        loadingRepository.insertCategoriesList(category)
    }

    /** This Function Will Insert All Sub Categories Locally */
    private suspend fun insertSubCategoryIntoDatabase(subCategory: List<SubCategory>) {
        loadingRepository.insertSubCategoriesList(subCategory)
    }

    /** This Function Will Insert All Items Locally */
    private suspend fun insertItemsIntoDatabase(items: List<Item>) {
        loadingRepository.insertAllItemsList(items)
    }
}