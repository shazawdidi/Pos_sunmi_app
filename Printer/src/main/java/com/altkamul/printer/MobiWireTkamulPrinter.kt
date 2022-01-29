package com.altkamul.printer

import com.nbbse.mobiprint3.Printer
import com.altkamul.printer.models.config.DevicePrinterStatus
import com.altkamul.printer.models.config.LinePrintingStatus
import com.altkamul.printer.models.data.TkamulPrinterImageModel
import com.altkamul.printer.models.data.TkamulPrinterTextModel
import com.altkamul.printer.models.textFormat.PrintTextDirction
import com.altkamul.printer.models.textFormat.PrinterTextScale

class MobiWireTkamulPrinter : TkamulPrinterBase() {

    private lateinit var mobiWirePrinter : Printer

    /**
     *  {@inheritDoc}
     */
    override fun setup(onReady:()->Unit , onError:()->Unit) {
        mobiWirePrinter = Printer.getInstance()
        onReady()
    }

    override fun PrintTextOnPaper(tkamulPrinterTextModel: TkamulPrinterTextModel) : LinePrintingStatus {
        val isPrinted  = mobiWirePrinter.printText(tkamulPrinterTextModel.text , getTextSize(tkamulPrinterTextModel.scale) ,getR2LFLAG(tkamulPrinterTextModel.dirction))
        return  getLastLinePrintingStatus()
    }

    /**
     * unfotuntly we cant get last printed line status like CSPrinter
     * but we can save time and parent class logic with checking with device printer status instadeof returning always true and the printer have an issue
     */
    private fun getLastLinePrintingStatus(): LinePrintingStatus {
        val status = getPrinterStatus()
        if (status.isReady){
            return LinePrintingStatus(true , null)
        }else {
            return LinePrintingStatus(false , status.status)
        }
    }

    /**
     * get desired printing text direction
     */
    private fun getR2LFLAG(dirction: PrintTextDirction): Boolean {
        return when(dirction){
            PrintTextDirction.LTR->false
            PrintTextDirction.RTL->true
        }
    }

    /**
     * getting mp3 text  size  depend on scale
     */
    fun getTextSize(scale : PrinterTextScale): Int {
        return when(scale){
            PrinterTextScale.large->  LARGE_TEXT
            PrinterTextScale.medium ->  MED_TEXT
            PrinterTextScale.normal -> NORMAL_TEXT
        }
    }


    /**
     *  {@inheritDoc}
     */
    override fun PrintImageOnPaper(tkamulPrinterImageModel: TkamulPrinterImageModel) : LinePrintingStatus {
        if (tkamulPrinterImageModel.path != null)
            mobiWirePrinter.printBitmap(tkamulPrinterImageModel.path)
//        else if (tkamulPrinterImageModel.bitmap != null)
/*            mobiWirePrinter.printBitmap(
                tkamulPrinterImageModel.bitmap
        )*/
        return getLastLinePrintingStatus()
    }

    /**
     *  {@inheritDoc}
     */
    override fun endingPrinterChild() {
        mobiWirePrinter.printEndLine()
    }


    /**
     *  {@inheritDoc}
     * get mobiewire printer status for mp3 device type
     */
    override fun getPrinterStatus(): DevicePrinterStatus {
        return when (mobiWirePrinter.printerStatus) {
            Printer.PRINTER_STATUS_NO_PAPER -> {
                DevicePrinterStatus("no paper ", false)
            }
            Printer.PRINTER_STATUS_OVER_HEAT -> {
                DevicePrinterStatus("over heat ", false)
            }
            Printer.PRINTER_STATUS_GET_FAILED -> {
                DevicePrinterStatus("print fail", false)
            }
            Printer.PRINTER_STATUS_OK -> {
                DevicePrinterStatus("ready", true)
            }
            else ->  DevicePrinterStatus("unKnown", false)
        }
    }




    /**
     * @inheritDoc
     */
    override fun getMaxCharCountInLine(scale: PrinterTextScale): Int {
        return  MAX_CHAR_COUNT
    }



    companion object {
        internal const val LARGE_TEXT = 2
        internal const val MED_TEXT = 1
        internal const val NORMAL_TEXT = 1
        internal const val MAX_CHAR_COUNT: Int = 21
        internal const val EMPTY_LINE: String = "\n"
    }
}