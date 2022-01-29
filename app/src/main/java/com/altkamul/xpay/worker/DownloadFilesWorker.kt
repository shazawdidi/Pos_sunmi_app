package com.altkamul.xpay.worker

import android.annotation.SuppressLint
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.altkamul.xpay.api.ApiClientImp
import com.altkamul.xpay.db.DataAccessObject
import com.altkamul.xpay.utils.DownloadUtils
import com.altkamul.xpay.utils.LoggedMerchantPref
import com.altkamul.xpay.utils.LoggedMerchantPref.merchant
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class DownloadFilesWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    val api: ApiClientImp,
    val dao: DataAccessObject,
) : CoroutineWorker(context,params){
    override suspend fun doWork(): Result {
        Timber.d("Download Images & logos  using a worker :) ")
        LoggedMerchantPref.branch?.images?.defaultlogo?.let {
            DownloadUtils.enqueue(context = applicationContext, url = "http://smartepaystaging.altkamul.ae/Content/img/printing.bmp", downloadListener = object : DownloadUtils.DownloadListener {
                override fun onFinish(referenceId: Long, downloadStatus: DownloadUtils.DownloadStatus) {
                    Timber.d("onFinish  : $downloadStatus")
                }

                override fun deliverStatus(downloadStatus: DownloadUtils.DownloadStatus) {
                    Timber.d("deliverStatus  : $downloadStatus")
                }
            })
            return Result.success()
        }

        return Result.success()
    }
}

