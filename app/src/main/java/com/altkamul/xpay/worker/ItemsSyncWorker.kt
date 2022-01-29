package com.altkamul.xpay.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.altkamul.xpay.api.ApiClientImp
import com.altkamul.xpay.db.DataAccessObject
import com.altkamul.xpay.sealed.ServerResponse
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
public class ItemsSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    val api: ApiClientImp,
    val dao: DataAccessObject,
) : CoroutineWorker(context,params){

    override suspend fun doWork(): Result {
        Timber.d("Getting the updated items using a worker :) ")
        /** Getting the current version that we had passed here */
        val currentVersion = inputData.getInt("currentVersion",-1).also {
            /** If we didn't get any version here , we should return failure state */
            if(it == -1) return Result.failure()
        }
        /** Getting the last version that we had passed here that we should store later */
        val lastVersion = inputData.getInt("lastVersion",-1).also {
            /** If we didn't get any version here , we should return failure state */
            if(it == -1) return Result.failure()
        }

        /** Getting the latest version items*/
        val response = api.syncItems(currentVersion = currentVersion)
        /** We had the response */
        Timber.d("We got a response !")
        when(response){
            is ServerResponse.Success -> {
                val items = response.data
                    ?: return Result.failure(workDataOf("message" to "Data suppose to be fetched from the server!"))

                /** We should HAPPILY replace this data with the old one in local room */
                dao.updateItems(items = items)
                /** After this , we should update our last version of items */
                dao.updateItemsVersion(currentVersion,lastVersion)
                /** return a success response */
                return Result.success()
            }
            is ServerResponse.Error -> {
                /** Shit , we had an error but trying again */
                Timber.d("Server error when getting updated categories")
                return Result.retry()
            }
        }
    }


}