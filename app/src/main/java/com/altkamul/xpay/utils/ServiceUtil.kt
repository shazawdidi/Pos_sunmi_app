package com.altkamul.xpay.utils

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import com.mediatek.settings.service.CSAndoridGo
import com.mediatek.settings.service.CsApiAndroidQ


/**
 * CLASS FOR MEDIA TECH INFORMATION FOR DEVICES
 */
class ServiceUtil(private val context: Context) {

    private val serviceUtilIntent = Intent("com.mediatek.settings.MyService.action")

    @SuppressLint("HardwareIds")
    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(RuntimeException::class)
    fun getGoInfo(info: (Info) -> Unit = {}) {
        getGoInterface {
            info(
                Info(
                    deviceInfo = it.deviceInformation
                ).apply {
                    val telephonyMgr =
                        context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                    if (!telephonyMgr.deviceId.isEmpty()) {
                        deviceInfo.serial_number = telephonyMgr.deviceId
                    } else if (!it.deviceInformation.sim1_imei.isEmpty()) {
                        deviceInfo.serial_number = it.deviceInformation.sim1_imei
                    } else if (!it.deviceInformation.sim2_imei.isEmpty()) {
                        deviceInfo.serial_number = it.deviceInformation.sim2_imei
                    }
                }
            )

        }

    }

    @SuppressLint("HardwareIds")
    @RequiresApi(Build.VERSION_CODES.Q)
    @Throws(RuntimeException::class)
    fun getQInfo(info: (Info) -> Unit = {}) {
        getQInterface {
            info(
                Info(
                    deviceInfo = it.deviceInformation
                ).apply {
                    if (!it.deviceInformation.sim1_imei.isEmpty()) {
                        deviceInfo.serial_number = it.deviceInformation.sim1_imei
                    } else if (!it.deviceInformation.sim2_imei.isEmpty()) {
                        deviceInfo.serial_number = it.deviceInformation.sim2_imei
                    }
                }
            )

        }

    }


    @Throws(java.lang.RuntimeException::class)
    fun getGoInterface(onServiceConnected: (CSAndoridGo) -> Unit) {
        bindRemoteService({
            onServiceConnected(CSAndoridGo.Stub.asInterface(it))
        }, {
        })
    }

    @Throws(java.lang.RuntimeException::class)
    fun getQInterface(onServiceConnected: (CsApiAndroidQ) -> Unit) {
        bindRemoteService({
            onServiceConnected(CsApiAndroidQ.Stub.asInterface(it))
        }, {
        })
    }

    //GET ANDROIDQ & ANDROIDGO INTEFACES
    @Throws(java.lang.RuntimeException::class)
    fun bindRemoteService(
        onServiceConnected: (IBinder) -> Unit,
        onServiceDisconnected: () -> Unit = {}
    ) {
        val intent = Intent()
        intent.action = "com.mediatek.settings.MyService.action" //若修改了清单文件，一定要重启手机！
        val service = createExplicitFromImplicitIntent(context)
        val connection = geMediaTechConnectionListener({ serviceInterface: IBinder ->
            onServiceConnected(serviceInterface)
        }, {
            onServiceDisconnected()
        })
        context.bindService(service, connection, Context.BIND_AUTO_CREATE)
    }


    // create explicit intent for [mediatekRemoteServiceIntent}
    private fun createExplicitFromImplicitIntent(
        context: Context
    ): Intent {
        // Retrieve all services that can match the given intent
        val packageManager = context.packageManager
        val resolveInfo = packageManager.queryIntentServices(serviceUtilIntent, 0)
        resolveInfo.ifEmpty { null }?.run {
            // Get component info and create ComponentName
            val serviceInfo = resolveInfo[0]
            val packageName = serviceInfo.serviceInfo.packageName
            val className = serviceInfo.serviceInfo.name
            val component = ComponentName(packageName, className)
            // Set the component to be explicit
            return serviceUtilIntent.apply {
                this.component = component
            }
        }
        throw Exception("cant find service match [${serviceUtilIntent.action}]")
    }

    private fun geMediaTechConnectionListener(
        onServiceConnected: (serviceInterface: IBinder) -> Unit = {},
        onServiceDisconnected: () -> Unit = {}
    ): ServiceConnection {
        return object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, serviceInterface: IBinder) {
                onServiceConnected(serviceInterface)
            }

            override fun onServiceDisconnected(componentName: ComponentName) {
                onServiceDisconnected()
            }
        }
    }
}