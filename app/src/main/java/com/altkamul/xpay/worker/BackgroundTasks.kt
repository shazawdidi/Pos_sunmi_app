package com.altkamul.xpay.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.altkamul.xpay.model.*
import com.altkamul.xpay.model.response.print.Data
import com.altkamul.xpay.repositroy.BackgroundTasksRepository
import com.altkamul.xpay.sealed.ServerResponse
import com.altkamul.xpay.utils.LoggedMerchantPref
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class BackgroundTasks @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val backgroundTaskRepository: BackgroundTasksRepository
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        val isGetMainConfigurationSuccess = getMainConfiguration()
        val isGetInvoiceLayoutSuccess = getInvoiceLayoutFromAPI()
        val isGetCashiersSuccess = getAllCashiersFromAPI()
        val isGetRolesSuccess = getAllRolesFromAPI()
        val isGetContactUsDataSuccess = getContactUsDataFromAPI()
        return if (isGetInvoiceLayoutSuccess && isGetCashiersSuccess
            && isGetRolesSuccess && isGetMainConfigurationSuccess && isGetContactUsDataSuccess
        )
            Result.success()
        else
            Result.retry()
    }

    private suspend fun getInvoiceLayoutFromAPI(): Boolean {
        return when (val response = backgroundTaskRepository.getInvoiceLayout()) {
            is ServerResponse.Success -> {
                /** fetched the data from the server*/
                val data = response.data
                /** inserted the data in local*/
                data?.layoutText?.list?.let { insertInvoiceLayoutLocally(it) }
                true
            }
            is ServerResponse.Error -> {
                false
            }
        }
    }

    private suspend fun getAllCashiersFromAPI(): Boolean {
        return when (
            val response = backgroundTaskRepository.getAllCashiers()) {
            is ServerResponse.Success -> {
                val data = response.data
                data?.cashiers?.let {
                    insertCashiersLocally(it)
                }
                true
            }
            is ServerResponse.Error -> {
                false
            }
        }
    }

    private suspend fun getAllRolesFromAPI(): Boolean {
        return when (val response = backgroundTaskRepository.getAllRoles()) {
            is ServerResponse.Success -> {
                val data = response.data
                data?.posRoles?.let { insertRolesLocally(it) }
                true
            }
            is ServerResponse.Error -> {
                false
            }
        }
    }

    private suspend fun getMainConfiguration(): Boolean {
        return when (val response = backgroundTaskRepository.getMainConfiguration()) {
            is ServerResponse.Success -> {
                /** fetched the data from the server*/
                val data = response.data
                if (data != null) {
                    /** inserted the data in local*/
                    val localConfig = convertRemoteMainConfigObjectToLocal(data)
                    insertMainConfigurationLocally(localConfig)
                    /** Setting the configuration pref */
                    LoggedMerchantPref.configuration = localConfig
                }
                true
            }
            is ServerResponse.Error -> {
                false
            }
        }
    }

    private suspend fun getContactUsDataFromAPI(): Boolean {
        return when (val response = backgroundTaskRepository.getContactDataRemotely()) {
            is ServerResponse.Success -> {
                val data = response.data
                data?.let { insertContactUsDataLocally(it) }
                true
            }
            is ServerResponse.Error -> {
                false
            }
        }
    }

    /** This Function Will Convert Remote Main Config Object To Local Object */
    private fun convertRemoteMainConfigObjectToLocal(data: MainConfiguration): LocalMainConfiguration {
        return LocalMainConfiguration(
            data.branchID,
            data.businessDayAllowed,
            data.businessShiftAllowed,
            data.claimAllowed,
            data.customerAllowed,
            data.decimalPoint,
            data.discountAllowed,
            data.footerMessage,
            data.isSucceeded,
            data.merchantCopy,
            data.nfcProductSearch,
            data.payment,
            data.printAllowed,
            data.queueAllowed,
            data.rePrint,
            data.smsAllowed,
            data.statusCode,
            data.taxAllowed,
            data.trn,
            taxName = data.tax?.name,
            taxID = data.tax?.id,
            taxValue = data.tax?.value,
            taxTypeID = data.taxType?.id,
            taxTypeName = data.taxType?.name
        )
    }

    private suspend fun insertCashiersLocally(list: List<Cashiers>) =
        backgroundTaskRepository.insertAllCashiers(list)

    private suspend fun insertRolesLocally(roles: List<PosRole>) =
        backgroundTaskRepository.insertRoles(roles)

    private suspend fun insertMainConfigurationLocally(localMainConfiguration: LocalMainConfiguration) =
        backgroundTaskRepository.insertMainConfig(localMainConfiguration)

    private suspend fun insertInvoiceLayoutLocally(data: List<Data>) =
        backgroundTaskRepository.insertInvoiceData(data)

    private suspend fun insertContactUsDataLocally(contactUs: ContactUs) =
        backgroundTaskRepository.insertContactDataLocally(contactUs)
}