package com.altkamul.xpay.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.altkamul.xpay.model.LocalDataVersions
import com.altkamul.xpay.model.Transaction
import com.altkamul.xpay.repositroy.ParentRepository
import com.altkamul.xpay.sealed.NetworkStatus
import com.altkamul.xpay.utils.*
import com.altkamul.xpay.worker.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ParentViewModel @Inject constructor(
    private val parentRepository: ParentRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    val currentLanguage = context.dataStore.data.map {
        it[APP_LANGUAGE] ?: "en"
    }
    val homeSearchValue = mutableStateOf("")
    private val _network =
        MutableLiveData<NetworkStatus>().also { it.value = NetworkStatus.Disconnected }
    val network: LiveData<NetworkStatus> = _network
    private val _deviceSerialNumber = MutableLiveData("")
    val deviceSerialNumber: LiveData<String> = _deviceSerialNumber

    init {
//        getDeviceSerialNumber(context)
    }

    /** Updating network status whenever it got change */
    fun updateNetworkStatus(status: NetworkStatus) {
        this._network.postValue(status)
    }

    /** Getting Serial Device Number */
    private fun getDeviceSerialNumber(context: Context) {
        viewModelScope.launch {
            val serviceUtil = ServiceUtil(context)
            if (Build.VERSION.SDK_INT >= 29) {
                serviceUtil.getQInfo {
                    _deviceSerialNumber.value = it.deviceInfo.serial_number
                }
            } else {
                serviceUtil.getGoInfo {
                    _deviceSerialNumber.value = it.deviceInfo.serial_number
                }
            }
        }
    }


    /** A function that check if there are updates on the server, it's called from a specific checkpoints */
    fun checkForDataUpdates() {
        viewModelScope.launch {
            val version = parentRepository.getLocalDataVersions() ?: LocalDataVersions(1, 0, 0, 0)
            checkLatestDataVersion(currentVersion = version)
        }
    }

    /**GETTING PRINTER LAYOUT*/
    fun callPrinter(transaction: Transaction) {
        viewModelScope.launch {
            val printerUtil = PrinterUtil(context)
            Timber.d("Gonna reprint now !")
            val invoiceLayout = parentRepository.getLocalInvoiceData()
            invoiceLayout.let { printerUtil.printing(it, transaction) }

        }
    }

    fun callPrinter2() {
        val printerUtil = PrinterUtil(context)
        printerUtil.printImage()
    }

    private fun checkLatestDataVersion(currentVersion: LocalDataVersions) {
        viewModelScope.launch {
            val response = parentRepository.getLatestVersion()
            val responseBody = response.data ?: return@launch
            val latestVersion = LocalDataVersions(
                items = responseBody.getVersionNumber("Items"),
                categories = responseBody.getVersionNumber("Category"),
                subCategories = responseBody.getVersionNumber("Subcategory"),
            )
            Timber.d("Latest version is $latestVersion")
            if (latestVersion.categories != currentVersion.categories) {
                /** Categories had some changes , we should update it */
                Timber.d("syncing categories ... ")
                syncCategories(
                    currentVersion = currentVersion.categories,
                    lastVersion = latestVersion.categories
                )
            }
            if (latestVersion.subCategories != currentVersion.subCategories) {
                /** subCategories had some changes , we should update it */
                Timber.d("syncing subcategories ... ")
                syncSubcategories(
                    currentVersion = currentVersion.subCategories,
                    lastVersion = latestVersion.subCategories
                )
            }
            if (latestVersion.items != currentVersion.items) {
                /** items had some changes , we should update it */
                Timber.d("syncing items ... ")
                syncItems(
                    currentVersion = currentVersion.items,
                    lastVersion = latestVersion.items
                )
            }
            /** When reach here , everything that need to be updated should be start updating ! */
            Timber.d("Data that need to be updated is enqueued , just wait! ")
        }
    }

    /**
     * A function that is used to sync latest updated version of categories using a worker .
     * It takes current version that is sent to server , and take the last version to be stored locally later when the worker is completed its work
     */
    private fun syncCategories(
        currentVersion: Int,
        lastVersion: Int,
    ) {
        viewModelScope.launch {
            /** We should only get the data which had a version larger than #currentVersion */
            val categorySyncWorker = OneTimeWorkRequestBuilder<CategorySyncWorker>()
                .setInputData(
                    workDataOf(
                        "currentVersion" to currentVersion,
                        "lastVersion" to lastVersion
                    )
                )
                .addTag("SyncData")
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                "SyncCategories",
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                categorySyncWorker
            )
        }
    }

    /**
     * A function that is used to sync latest updated version of subcategories using a worker .
     * It takes current version that is sent to server , and take the last version to be stored locally later when the worker is completed its work
     */
    private fun syncSubcategories(
        currentVersion: Int,
        lastVersion: Int,
    ) {
        viewModelScope.launch {
            /** We should only get the data which had a version larger than #currentVersion */
            val subcategorySyncWorker = OneTimeWorkRequestBuilder<SubcategorySyncWorker>()
                .setInputData(
                    workDataOf(
                        "currentVersion" to currentVersion,
                        "lastVersion" to lastVersion
                    )
                )
                .addTag("SyncData")
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                "SyncSubCategories",
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                subcategorySyncWorker
            )
        }
    }

    /**
     * A function that is used to sync latest updated version of items using a worker .
     * It takes current version that is sent to server , and take the last version to be stored locally later when the worker is completed its work
     */
    private fun syncItems(
        currentVersion: Int,
        lastVersion: Int,
    ) {
        viewModelScope.launch {
            /** We should only get the data which had a version larger than #currentVersion */
            val itemsSyncWorker = OneTimeWorkRequestBuilder<ItemsSyncWorker>()
                .setInputData(
                    workDataOf(
                        "currentVersion" to currentVersion,
                        "lastVersion" to lastVersion
                    )
                )
                .addTag("SyncData")
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                "SyncItems",
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                itemsSyncWorker
            )
        }
    }

    /** This Function Will Begin Background Task And leave it stand alone*/
    fun lunchBackgroundTask() {
        viewModelScope.launch {
            val lunchBackgroundTask =
                OneTimeWorkRequestBuilder<BackgroundTasks>().addTag("LunchBackgroundTask")
                    .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                "BackgroundTask",
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                lunchBackgroundTask
            )

        }
    }

    /** FUNCTION TO DOWNLOAD IMAGES & FILES IN BACKGROUND*/
    fun downloadBranchImages(
    ) {
        viewModelScope.launch {
            val downloadImagesWorker = OneTimeWorkRequestBuilder<DownloadFilesWorker>()
                .addTag("DownloadFilesWorker")
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                "DownloadFilesWorker",
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                downloadImagesWorker
            )
        }
    }

//    fun logoutUser() {
//        viewModelScope.launch {
//            parentRepository.logoutCurrentUser().let{
//
//            }
//        }
//    }

}