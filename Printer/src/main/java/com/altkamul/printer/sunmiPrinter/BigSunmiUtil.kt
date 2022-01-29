package com.altkamul.printer.sunmiPrinter

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.graphics.Bitmap
import android.os.Build
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.altkamul.printer.CSTkamulPrinter
import com.altkamul.printer.TkamulPrinterBase
import com.altkamul.printer.core.Logger
import com.altkamul.printer.models.config.DevicePrinterStatus
import com.altkamul.printer.models.config.LinePrintingStatus
import com.altkamul.printer.models.data.TkamulPrinterImageModel
import com.altkamul.printer.models.data.TkamulPrinterTextModel
import com.altkamul.printer.models.textFormat.PrintTextAlign
import com.altkamul.printer.models.textFormat.PrintTextDirction
import com.altkamul.printer.models.textFormat.PrinterTextScale
import com.mobiiot.androidqapi.api.CsPrinter
import com.mobiiot.androidqapi.api.Utils.PrinterServiceUtil
import com.sagereal.printer.PrinterInterface
import timber.log.Timber
import woyou.aidlservice.jiuiv5.ICallback
import woyou.aidlservice.jiuiv5.IWoyouService

class BigSunmiUtil(val context: Context) : TkamulPrinterBase() {
     private val SERVICE_PACKAGE:String = "woyou.aidlservice.jiuiv5"
    private val SERVICE_ACTION :String = "woyou.aidlservice.jiuiv5.IWoyouService"
    var woyouService: IWoyouService? = null
     private val TAG: String? = "PrinterServiceUtil"
    var atService: PrinterInterface? = null
private var setup: Boolean = false
    /**0000
     *  invoke onReady() when  PrinterServiceUtil launched and ready to use
     *  invoke onReady() when  PrinterServiceUtil have leak
     */
    override fun setup(onReady: () -> Unit, onError: () -> Unit) {
        if (!setup){
            setup = true
            val serviceConnection: ServiceConnection = object : ServiceConnection {
                override fun onServiceDisconnected(name: ComponentName) {
                    Timber.e("aidl connect fail")
                    woyouService = null
                    setup = false
                    onError()
                }

                override fun onServiceConnected(name: ComponentName, service: IBinder) {
                    Timber.e( "aidl connect success")
                    woyouService = IWoyouService.Stub.asInterface(service)
                    setup = true
                    onReady()
                }
            }
            context.bindService(getPrintIntent(), serviceConnection,  Context.BIND_AUTO_CREATE)
        }else {
            onReady()
        }

    }
    private fun getPrintIntent(): Intent {
        val aidlIntent = Intent()
        aidlIntent.setPackage(SERVICE_PACKAGE)
        aidlIntent.action=SERVICE_ACTION
        context.applicationContext.startService(aidlIntent)
        context.applicationContext.bindService(aidlIntent, connService, Context.BIND_AUTO_CREATE)
        return aidlIntent
    }
    override fun getPrinterStatus(): DevicePrinterStatus {
        val printerStatus = 1
        Logger.logd("printerStatus $printerStatus")
        if (printerStatus == 1){
            return DevicePrinterStatus("Ready", true)
        } else if (printerStatus ==0){
            return DevicePrinterStatus("Pinter is out of Papers, Please insert new Paper Roll", false)
        } else if (printerStatus ==16){
            return DevicePrinterStatus("Printer is  out of Paper and The device need to shutdown to cool down the printer ", false)
        } else if (printerStatus ==17){
            return DevicePrinterStatus("The device need to shutdown to cool down the printer", false)
        } else {
            return DevicePrinterStatus("The printer status is unknown, please contact support", false)
        }
    }

    override fun getMaxCharCountInLine(textScale: PrinterTextScale): Int {
        return when(textScale){
            PrinterTextScale.normal -> CSTkamulPrinter.MAX_CHAR_COUNT_SCALE_NORMAL
            PrinterTextScale.medium -> CSTkamulPrinter.MAX_CHAR_COUNT_SCALE_MEDIUM
            PrinterTextScale.large -> CSTkamulPrinter.MAX_CHAR_COUNT_SCALE_LARGE
        }
    }

    override fun PrintTextOnPaper(tkamulPrinterTextModel: TkamulPrinterTextModel): LinePrintingStatus {
        printText(tkamulPrinterTextModel.text,getTextSize(tkamulPrinterTextModel.scale),tkamulPrinterTextModel.isBold,false,false)
        return getLinePrintingStatus()
    }

    override fun PrintImageOnPaper(tkamulPrinterImageModel: TkamulPrinterImageModel): LinePrintingStatus {
        printBitmap(tkamulPrinterImageModel.path)
        return getLinePrintingStatus()
    }

    override fun endingPrinterChild() {
        print3Line()
    }



    val isConnect: Boolean
        get() = woyouService != null
    private val connService: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {
            woyouService = null
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            woyouService = IWoyouService.Stub.asInterface(service)
        }
    }

    fun generateCB(printerCallback: PrinterCallback?): ICallback {
        return object : ICallback.Stub() {
            @Throws(RemoteException::class)
            override fun onRunResult(isSuccess: Boolean) {
            }

            @Throws(RemoteException::class)
            override fun onReturnString(result: String) {
            }

            @Throws(RemoteException::class)
            override fun onRaiseException(code: Int, msg: String) {
            }

            @Throws(RemoteException::class)
            override fun onPrintResult(code: Int, msg: String) {
            }
        }
    }


    private val darkness = intArrayOf(0x0600, 0x0500, 0x0400, 0x0300, 0x0200, 0x0100, 0,
        0xffff, 0xfeff, 0xfdff, 0xfcff, 0xfbff, 0xfaff)//info.add(woyouService.getServiceVersion());


    @get:RequiresApi(api = Build.VERSION_CODES.P)
    val printerInfo: List<String>?
        get() {
            if (woyouService == null) {
                Toast.makeText(context, "The service has been disconnected!", Toast.LENGTH_LONG)
                    .show()
                return null
            }
            val info: MutableList<String> = ArrayList()
            try {
                info.add(woyouService!!.printerSerialNo)
                info.add(woyouService!!.printerModal)
                info.add(woyouService!!.printerVersion)
                info.add(woyouService!!.printedLength.toString() + "")
                info.add("")
                //info.add(woyouService.getServiceVersion());
                val packageManager = context!!.packageManager
                try {
                    val packageInfo: PackageInfo =
                        packageManager.getPackageInfo(SERVICE_PACKAGE, 0)
                    if (packageInfo != null) {
                        info.add(packageInfo.versionName)
                        info.add(packageInfo.longVersionCode.toString() + "")
                    } else {
                        info.add("")
                        info.add("")
                    }
                } catch (e: NameNotFoundException) {
                    e.printStackTrace()
                }
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
            return info
        }

    /**
     * initPrinter
     */
    fun initPrinter() {
        if (woyouService == null) {
            Toast.makeText(context, "The service has been disconnected!", Toast.LENGTH_LONG).show()
            return
        }
        try {
            woyouService!!.printerInit(null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * 打印二维码
     */
    fun printQr(data: String?, modulesize: Int, errorlevel: Int) {
        if (woyouService == null) {
            Toast.makeText(context, "The service has been disconnected!", Toast.LENGTH_LONG).show()
            return
        }
        try {
            woyouService!!.setAlignment(1, null)
            woyouService!!.printQRCode(data, modulesize, errorlevel, null)
            woyouService!!.lineWrap(2, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * 打印条形码
     */
    fun printBarCode(data: String?, symbology: Int, height: Int, width: Int, textposition: Int) {
        if (woyouService == null) {
            Toast.makeText(context, "The service has been disconnected!", Toast.LENGTH_LONG).show()
            return
        }
        try {
            woyouService!!.printBarCode(data, symbology, height, width, textposition, null)
            woyouService!!.lineWrap(3, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }
    private fun getTexAlign(align: PrintTextAlign): Int {
        return when(align){
            PrintTextAlign.LEFT -> ALIGN_LEFT
            PrintTextAlign.RIGHT -> ALIGN_RIGHT
            PrintTextAlign.CENTER -> ALIGN_CENTER
        }
    }

    /**
     * LTR -> 0
     * RTL ->1
     */
    private fun getTextDiriction(dirction: PrintTextDirction): Int {
        when(dirction){
            PrintTextDirction.LTR -> return CSTkamulPrinter.LTR
            PrintTextDirction.RTL -> return CSTkamulPrinter.RTL
        }
    }

    /**
     * 打印文字
     */
    open fun printText(
        content: String?,
        size: Float,
        isBold: Boolean,
        isUnderLine: Boolean,
        align: Boolean,
    ) {
        if (woyouService == null) {
            Toast.makeText(context, "The service has been disconnected!", Toast.LENGTH_LONG).show()
            return
        }
        try {
            if (align)  woyouService?.setAlignment(2,
                null) else woyouService?.setAlignment(0, null)
            if (isBold) {
               woyouService?.sendRAWData(ESCUtil.boldOn(), null)
            } else {
               woyouService?.sendRAWData(ESCUtil.boldOff(), null)
            }
            if (isUnderLine) {
                woyouService?.sendRAWData(ESCUtil.underlineWithOneDotWidthOn(), null)
            } else {
                woyouService?.sendRAWData(ESCUtil.underlineOff(), null)
            }
            woyouService?.printTextWithFont(content, null, size, null)
            woyouService?.lineWrap(1, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }


    /*
    *打印图片
     */
    fun printBitmap(bitmap: Bitmap?) {
        if (woyouService == null) {
            Toast.makeText(context, "The service has been disconnected!", Toast.LENGTH_LONG).show()
            return
        }
        try {
            woyouService!!.setAlignment(1, null)
            woyouService!!.printBitmap(bitmap, null)
            woyouService!!.lineWrap(3, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * 打印图片和文字按照指定排列顺序
     */
    fun printBitmap(bitmap: Bitmap?, orientation: Int) {
        if (woyouService == null) {
            Toast.makeText(context, "服务已断开！", Toast.LENGTH_LONG).show()
            return
        }
        try {
            woyouService!!.setAlignment(1, null)
            if (orientation == 0) {
                woyouService!!.printBitmap(bitmap, null)
                //                woyouService.printText("横向排列\n", null);
//                woyouService.printBitmap(bitmap, null);
//                woyouService.printText("横向排列\n", null);
            } else {
                woyouService!!.printBitmap(bitmap, null)
                //                woyouService.printText("\n纵向排列\n", null);
//                woyouService.printBitmap(bitmap, null);
//                woyouService.printText("\n纵向排列\n", null);
            }
            woyouService!!.lineWrap(1, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }
    //    /**
    //     * 打印表格
    //     */
    //    public void printTable(LinkedList<TableItem> list) {
    //        if (woyouService == null) {
    //            Toast.makeText(context,R.string.toast_2,Toast.LENGTH_LONG).show();
    //            return;
    //        }
    //
    //        try {
    //            for (TableItem tableItem : list) {
    //                Log.i("kaltin", "printTable: "+tableItem.getText()[0]+tableItem.getText()[1]+tableItem.getText()[2]);
    //                woyouService.printColumnsText(tableItem.getText(), tableItem.getWidth(), tableItem.getAlign(), null);
    //            }
    //            woyouService.lineWrap(3, null);
    //        } catch (RemoteException e) {
    //            e.printStackTrace();
    //        }
    //    }
    /*
    * 空打三行！
     */
    fun print3Line() {
        if (woyouService == null) {
            Toast.makeText(context, "The service has been disconnected!", Toast.LENGTH_LONG).show()
            return
        }
        try {
            woyouService!!.lineWrap(3, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun sendRawData(data: ByteArray?) {
        if (woyouService == null) {
            Toast.makeText(context, "The service has been disconnected!", Toast.LENGTH_LONG).show()
            return
        }
        try {
            woyouService!!.sendRAWData(data, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    //获取当前的打印模式
    val printMode: Int
        get() {
            if (woyouService == null) {
                Toast.makeText(context, "服务已断开！", Toast.LENGTH_LONG).show()
                return -1
            }
            val res: Int
            res = try {
                woyouService!!.printerMode
            } catch (e: RemoteException) {
                e.printStackTrace()
                -1
            }
            return res
        }


    fun getTextSize(scale: PrinterTextScale): Float {
        return when(scale){
            PrinterTextScale.large -> LARGE_TEXT
            PrinterTextScale.medium -> MED_TEXT
            PrinterTextScale.normal -> NORMAL_TEXT
        }
    }

    companion object {
        internal const val LARGE_TEXT = 2f
        internal const val MED_TEXT = 1f
        internal const val NORMAL_TEXT = 0f
        internal const val MAX_CHAR_COUNT_SCALE_LARGE: Int = 14
        internal const val MAX_CHAR_COUNT_SCALE_MEDIUM: Int = 21
        internal const val MAX_CHAR_COUNT_SCALE_NORMAL: Int = 42
        internal const val LTR: Int = 0
        internal const val RTL: Int = 1
        internal  const val ALIGN_LEFT = 0
        internal  const val ALIGN_RIGHT = 2
        internal  const val ALIGN_CENTER = 1

        /**
         *0  : OK
         * 1  : No Paper
         * 2  : Overheat
         * 3  : Invalid printing data
         * 4  : Printing queue full
         * 10 : Unknown error
         */
        public fun getLinePrintingStatus(lastError: Int =0) : LinePrintingStatus {
            return when(lastError){
                0 -> LinePrintingStatus(true, null)
                1 -> LinePrintingStatus(false, "No Paper")
                2 -> LinePrintingStatus(false, "Overheat")
                3 -> LinePrintingStatus(false, "Invalid printing data")
                4 -> LinePrintingStatus(false, "Printing queue full")
                10 -> LinePrintingStatus(false, "Unknown error")
                else -> LinePrintingStatus(false, "Unknown error")
            }
        }

    }}