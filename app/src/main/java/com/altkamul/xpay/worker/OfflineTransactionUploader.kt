package com.altkamul.xpay.worker


import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.altkamul.xpay.api.ApiClientImp
import com.altkamul.xpay.db.DataAccessObject
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber


@HiltWorker
public class OfflineTransactionUploader @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    val api: ApiClientImp,
    val dao: DataAccessObject,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        /** First we should get the offline transactions from local */
        val offlineTransactions = dao.getLocalOfflineTransactions(isOffline = true)
        Timber.d("Number of offline transaction is ${offlineTransactions.size}")

        /** It's time to push it to the server */

        return Result.success()
    }


}