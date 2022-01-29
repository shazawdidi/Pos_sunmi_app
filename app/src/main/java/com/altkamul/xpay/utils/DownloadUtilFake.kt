package com.altkamul.xpay.utils

import android.content.Context
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.ContextCompat
import java.util.*
import kotlin.concurrent.schedule
import kotlin.properties.Delegates

class DownloadUtilsFake(
    private val context: Context,
    private val URL: String,
    private val internal : Boolean
) {
    companion object{
        /**
         * static function to excite download call
         * notify subscriber when download finish
         * deliver download status every 1 sec
         * @param packageName must be a package name like com.myapplication
         * @param internal default false : file will be  downloaded inside app directory
         * @param internal default true : file will be  downloaded inside public download directory
         */
        fun enqueue(context: Context, url: String, internal : Boolean = false, downloadListener: DownloadListener): Long {
            DownloadUtilsFake(context, url , internal)
                .run {
                    val downloadRef = downloadMedia()
                    trackUpdates(downloadListener)
                    registerCompleteReceiver(downloadListener)
                    return downloadRef
                }
        }
    }
    private var downloadReference by Delegates.notNull<Long>()

    /**
     * make request
     * @return download reference id
     */
    private fun downloadMedia(): Long {
        val Download_Uri = Uri.parse(URL)
        val request = DownloadManager.Request(Download_Uri)
        //Restrict the types of networks over which this download may proceed.
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        //Set whether this download may proceed over a roaming connection.
//        request.setAllowedOverRoaming(false)
        //Set the title of this download, to be displayed in notifications (if enabled).
        request.setTitle("logo")
        //Set a description of this download, to be displayed in notifications (if enabled)
        request.setDescription("Downloading logo...")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        //Set the local destination for the downloaded file to a path within the application's external files directory
        if (internal)
            request.setDestinationInExternalFilesDir(context , Environment.DIRECTORY_DOWNLOADS , System.currentTimeMillis().toString())
        else
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,System.currentTimeMillis().toString())
        //Enqueue a new download and same the referenceId
        downloadReference = ContextCompat.getSystemService(context, DownloadManager::class.java)!!
            .enqueue(request)
        Log.i("downloadReference", "" + downloadReference)
        return downloadReference
    }

    /**
     * get status for download reference id
     */
    private fun getStatus(): DownloadStatus {
        val myDownloadQuery = DownloadManager.Query()
        //set the query filter to our previously Enqueued download
        myDownloadQuery.setFilterById(downloadReference)
        //Query the download manager about downloads that have been requested.
        val cursor = ContextCompat.getSystemService(context, DownloadManager::class.java)!!.query(myDownloadQuery)
        if (cursor.moveToFirst()) {
            //checkStatus(cursor);

            //column for status
            val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            val status = cursor.getInt(columnIndex)
            //column for reason code if the download failed or paused
            val columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
            val reason = cursor.getInt(columnReason)
            //get the download filename
            var internalFilename :String? = null;
            val fileUriIdx = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
            val fileUri = cursor.getString(fileUriIdx);
            if (fileUri != null) {
                internalFilename = Uri.parse(fileUri).path
            }
            var statusText = ""
            var reasonText = ""
            when (status) {
                DownloadManager.STATUS_FAILED -> {
                    statusText = "STATUS_FAILED"
                    when (reason) {
                        DownloadManager.ERROR_CANNOT_RESUME -> reasonText = "ERROR_CANNOT_RESUME"
                        DownloadManager.ERROR_DEVICE_NOT_FOUND -> reasonText = "ERROR_DEVICE_NOT_FOUND"
                        DownloadManager.ERROR_FILE_ALREADY_EXISTS -> reasonText = "ERROR_FILE_ALREADY_EXISTS"
                        DownloadManager.ERROR_FILE_ERROR -> reasonText = "ERROR_FILE_ERROR"
                        DownloadManager.ERROR_HTTP_DATA_ERROR -> reasonText = "ERROR_HTTP_DATA_ERROR"
                        DownloadManager.ERROR_INSUFFICIENT_SPACE -> reasonText = "ERROR_INSUFFICIENT_SPACE"
                        DownloadManager.ERROR_TOO_MANY_REDIRECTS -> reasonText = "ERROR_TOO_MANY_REDIRECTS"
                        DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> reasonText = "ERROR_UNHANDLED_HTTP_CODE"
                        DownloadManager.ERROR_UNKNOWN -> reasonText = "ERROR_UNKNOWN"
                    }
                }
                DownloadManager.STATUS_PAUSED -> {
                    statusText = "STATUS_PAUSED"
                    when (reason) {
                        DownloadManager.PAUSED_QUEUED_FOR_WIFI -> reasonText = "PAUSED_QUEUED_FOR_WIFI"
                        DownloadManager.PAUSED_UNKNOWN -> reasonText = "PAUSED_UNKNOWN"
                        DownloadManager.PAUSED_WAITING_FOR_NETWORK -> reasonText = "PAUSED_WAITING_FOR_NETWORK"
                        DownloadManager.PAUSED_WAITING_TO_RETRY -> reasonText = "PAUSED_WAITING_TO_RETRY"
                    }
                }
                DownloadManager.STATUS_PENDING -> statusText = "STATUS_PENDING"
                DownloadManager.STATUS_RUNNING -> statusText = "STATUS_RUNNING"
                DownloadManager.STATUS_SUCCESSFUL -> {
                    statusText = "STATUS_SUCCESSFUL"
                    reasonText = "Filename:\n$internalFilename"
                }
            }
            return DownloadStatus(statusText, reasonText, fileUri = internalFilename, referenceId = downloadReference)
        }
        return DownloadStatus("UNKONWN", "UNKONWN", fileUri = "UNKONWN", referenceId = downloadReference)
    }

    private val timer: Timer = Timer()
    val  statusList = mutableListOf<DownloadStatus>()
    /**
     * deliver download status every 1 sec
     */
    private fun  trackUpdates(downloadListener: DownloadListener){
        timer.schedule(0, 1000) {
            val status = getStatus()
            if (!statusList.contains(status)){
                statusList.add(status)
                downloadListener.deliverStatus(status)
            }
        }
    }

    /**
     * register broadcast receiver with download complete action
     */
    private fun registerCompleteReceiver(onDownloadFinished: DownloadListener) {
        val downloadReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                //check if the broadcast message is for our Enqueued download
                val referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (downloadReference == referenceId) {
                    onDownloadFinished.onFinish(referenceId, getStatus())
                    timer.cancel()
                }
            }
        }
        val downloadReceiverFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        context.registerReceiver(downloadReceiver, downloadReceiverFilter)
    }


    interface DownloadListener {
        fun onFinish(referenceId: Long, filename: DownloadStatus)
        fun deliverStatus(downloadStatus: DownloadStatus)
    }
    data class DownloadStatus(val statusText: String?, val reasonText: String?, val referenceId: Long, val fileUri: String?)
}