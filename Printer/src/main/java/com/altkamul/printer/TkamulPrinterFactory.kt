package com.altkamul.printer

import android.content.Context
import com.altkamul.printer.core.Logger
import com.altkamul.printer.models.config.PrinterType
import com.altkamul.printer.sunmiPrinter.BigSunmiUtil
import timber.log.Timber


/**
 * usage
 * TkamulPrinterFactory.getTkamulPrinter(context)
 *.addEmptyLine()
 * .addText(text)
 * .addEmptyLine()
 * .addEmptyLine()
 * .printOnPaper()  >> return LinePrintingStatus
 */
object TkamulPrinterFactory {

    @JvmStatic
    fun getTkamulPrinterOptions(context: Context): TkamulPrinterBase {
        return when (TkamulPrinterBase.getPrinterType()) {
            PrinterType.MOBIEWIRE -> {
                Logger.logd("OPTION1")
                MobiWireTkamulPrinter() // mp3
            }
            PrinterType.CSPRINTER -> {
                Logger.logd("OPTION2")
                CSTkamulPrinter(context) // mp3+ , mp4+ , mp4 ,D2mini .T2 ,T2mini
            }
            PrinterType.BIGSUNMI -> {
                Logger.logd("OPTION3")
                BigSunmiUtil(context)
            }
        }
    }

    @JvmStatic
    fun getTkamulPrinter(context: Context): TkamulPrinterBase {
        return CSTkamulPrinter(context)
    }
}