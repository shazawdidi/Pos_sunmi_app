package com.altkamul.printer

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.mobiiot.androidqapi.api.CsPrinter
import com.mobiiot.androidqapi.api.Utils.PrinterServiceUtil
import com.sagereal.printer.PrinterInterface
import com.altkamul.printer.core.Logger
import com.altkamul.printer.models.config.DevicePrinterStatus
import com.altkamul.printer.models.config.LinePrintingStatus
import com.altkamul.printer.models.data.TkamulPrinterImageModel
import com.altkamul.printer.models.data.TkamulPrinterTextModel
import com.altkamul.printer.models.textFormat.PrintTextAlign
import com.altkamul.printer.models.textFormat.PrintTextDirction
import com.altkamul.printer.models.textFormat.PrinterTextScale



class CSTkamulPrinter(val context: Context) : TkamulPrinterBase() {

    private var setup: Boolean = false

    /**0000
     *  invoke onReady() when  PrinterServiceUtil launched and ready to use
     *  invoke onReady() when  PrinterServiceUtil have leak
     */
    override fun setup(onReady:()->Unit , onError:()->Unit) {
        if (!setup){
            setup = true
            val serviceConnection: ServiceConnection = object : ServiceConnection {
                override fun onServiceDisconnected(name: ComponentName) {
                    Log.e("PrinterServiceUtil", "aidl connect fail")
                    PrinterServiceUtil.atService = null
                    setup = false
                    onError()
                }

                override fun onServiceConnected(name: ComponentName, service: IBinder) {
                    Log.e("PrinterServiceUtil", "aidl connect success")
                    PrinterServiceUtil.atService = PrinterInterface.Stub.asInterface(service)
                    setup = true
                    onReady()
                }
            }
            context.bindService(PrinterServiceUtil.getPrintIntent(), serviceConnection,  Context.BIND_AUTO_CREATE)
        }else {
            onReady()
        }
    }

    /**
     * CsPrinter.getPrinterStatus() : Int
     * 0   : paper[KO]	Temperature[OK]
     * 1   : paper[OK]	Temperature[OK]
     * 16  : paper[KO]	Temperature[KO]
     * 17  : paper[OK]	Temperature[KO]
     *
     */
    override fun getPrinterStatus(): DevicePrinterStatus {
        val printerStatus = CsPrinter.getPrinterStatus()
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


    /**
     *  {@inheritDoc}
     */
    override fun getMaxCharCountInLine(scale: PrinterTextScale): Int {
        return when(scale){
            PrinterTextScale.normal ->   MAX_CHAR_COUNT_SCALE_NORMAL
            PrinterTextScale.medium ->   MAX_CHAR_COUNT_SCALE_MEDIUM
            PrinterTextScale.large ->   MAX_CHAR_COUNT_SCALE_LARGE
        }
    }


     fun getTextSize(scale: PrinterTextScale): Int {
        return when(scale){
            PrinterTextScale.large -> LARGE_TEXT
            PrinterTextScale.medium -> MED_TEXT
            PrinterTextScale.normal -> NORMAL_TEXT
        }
    }

    /**
     * - String (text to print)
     * int(size of text)[1-5]
     * int(direction of text)[1-3]
     * int(font of text)[1-5]
     * int(alignement of text)[1-3]
     * boolean(true for bold text, else false)
     * boolean(true for underline text, else false)
     * TODO("make keys for text font  , underline)
     */
    override fun PrintTextOnPaper(tkamulPrinterTextModel: TkamulPrinterTextModel) : LinePrintingStatus {
         CsPrinter.printText_FullParam(
                 tkamulPrinterTextModel.text,
                 getTextSize(tkamulPrinterTextModel.scale),
                 getTextDiriction(tkamulPrinterTextModel.dirction),
                 1, getTexAlign(tkamulPrinterTextModel.align), tkamulPrinterTextModel.isBold, false
         )
        return getLinePrintingStatus()
    }



    /**
     * int(alignement of text)[1-3]
     * ALIGN_LEFT= 1
     * ALIGN_RIGHT= 3
     * ALIGN_CENTER= 2
     */
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
            PrintTextDirction.LTR -> return LTR
            PrintTextDirction.RTL -> return RTL
        }
    }


    override fun PrintImageOnPaper(tkamulPrinterImageModel: TkamulPrinterImageModel) : LinePrintingStatus {
        if (tkamulPrinterImageModel.path != null)
             CsPrinter.printBitmap(tkamulPrinterImageModel.path)
        else if (tkamulPrinterImageModel.bitmap != null)
             CsPrinter.printBitmap(tkamulPrinterImageModel.bitmap)
        return getLinePrintingStatus()
    }

    override fun endingPrinterChild() {
        CsPrinter.printEndLine()
    }

    companion object {
        internal const val LARGE_TEXT = 2
        internal const val MED_TEXT = 1
        internal const val NORMAL_TEXT = 0
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
        public fun getLinePrintingStatus(lastError: Int = CsPrinter.getLastError()) : LinePrintingStatus {
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

    }
}