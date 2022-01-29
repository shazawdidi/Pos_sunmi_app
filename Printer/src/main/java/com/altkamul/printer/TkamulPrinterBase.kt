package com.altkamul.printer

import android.os.Build
import androidx.annotation.UiThread
import kotlinx.coroutines.*
import org.jetbrains.annotations.NotNull
import com.altkamul.printer.core.Config
import com.altkamul.printer.core.LineUtils
import com.altkamul.printer.core.Logger
import com.altkamul.printer.models.config.LinePrintingStatus
import com.altkamul.printer.models.config.DevicePrinterStatus
import com.altkamul.printer.models.config.PrinterType
import com.altkamul.printer.models.data.TkamulPrinterImageModel
import com.altkamul.printer.models.data.TkamulPrinterTextModel
import com.altkamul.printer.models.data.TkamulPrintingData
import com.altkamul.printer.models.textFormat.PrintTextAlign
import com.altkamul.printer.models.textFormat.PrintTextDirction
import com.altkamul.printer.models.textFormat.PrinterTextScale
import java.lang.RuntimeException
import java.lang.StringBuilder
import java.util.*


abstract class TkamulPrinterBase {

    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob)


    private  var  isSeparateTextToLinesEnabled = true
    private  var  combineLineCount = 100
    /**
     * setup child printer
     */
    protected abstract fun setup(onReady:()->Unit , onError:()->Unit)

    /**
     * get printer child status before printing
     */
    public abstract fun getPrinterStatus() : DevicePrinterStatus

    /**
     * get printer child  max char by line
     */
    protected abstract fun getMaxCharCountInLine(textScale  : PrinterTextScale):Int

    /**
     * make printer child  print text model
     */
    @UiThread
    protected abstract fun PrintTextOnPaper(tkamulPrinterTextModel : TkamulPrinterTextModel) : LinePrintingStatus

    /**
     * make printer child  print image model
     */
    @UiThread
    protected abstract fun PrintImageOnPaper(tkamulPrinterImageModel : TkamulPrinterImageModel) : LinePrintingStatus

    /**
     * make printer child  finish printing to save voltage & temperature
     */
    protected abstract fun endingPrinterChild()

    /**
     *  enable text separation to lines and set combineLineCount value
     */
    fun setSeparateTextToLines(enable : Boolean , combineLineCount:Int = 100) : TkamulPrinterBase {
        isSeparateTextToLinesEnabled = enable
        this.combineLineCount = combineLineCount
        return this
    }
    fun enableSeparateTextToLines(combineLineCount : Int) : TkamulPrinterBase {
        isSeparateTextToLinesEnabled = true
        this.combineLineCount = combineLineCount
        return this
    }
    fun disableSeparateTextToLines() : TkamulPrinterBase {
        isSeparateTextToLinesEnabled = false
        this.combineLineCount = 1
        return this
    }
    /**
     * add text with format : align , diricion size
     * @param scale : text size default : normal
     * @param align : text align : left
     * @param diriction : text diriction [RTL , LTR]  default : LTR
     */
    fun  addText(text: String, scale: PrinterTextScale = PrinterTextScale.normal, align: PrintTextAlign = PrintTextAlign.LEFT, diriction: PrintTextDirction = PrintTextDirction.LTR, isBold : Boolean = false): TkamulPrinterBase {
        if (isSeparateTextToLinesEnabled){
            val lineList = LineUtils.convertTextToLine(text,getMaxCharCountInLine(scale)*combineLineCount)
            for ( child in lineList ){
                printQueue.add(TkamulPrinterTextModel().apply {
                    this.scale = scale
                    this.dirction = diriction
                    this.text = child
                    this.align = align
                    this.isBold = isBold
                })
            }
        }else{
            printQueue.add(TkamulPrinterTextModel().apply {
                this.scale = scale
                this.dirction = diriction
                this.text = text
                this.align = align
                this.isBold = isBold
            })
        }
        return this
    }

    /**
     * add bitmap to queue
     */
    fun addImage(bitmap: ByteArray): @NotNull TkamulPrinterBase {
        val model = TkamulPrinterImageModel(bitmap)
        printQueue.add(model)
        return this
    }

    /**
     * add bitmap by  Internal path to queue
     */
//     fun addImagePath(path: String): TkamulPrinterBase {
//        val model = TkamulPrinterImageModel(path)
//        printQueue.add(model)
//        return this
//    }

    /**
     * add add Asterisks Line to queue
     */
     fun addAsterisksLine(scale: PrinterTextScale = PrinterTextScale.normal): TkamulPrinterBase {
        printQueue.add(
            TkamulPrinterTextModel(
                text  = LineUtils.getLineOfChar(lineCount = getMaxCharCountInLine(scale) , char = LineUtils.ASTERISK) ,
                scale = scale
            )
        )
        return this
    }

    /**
     * add dash line to queue
     */
    fun addDashLine(scale: PrinterTextScale = PrinterTextScale.normal): TkamulPrinterBase {
        printQueue.add(
            TkamulPrinterTextModel(
                text  = LineUtils.getLineOfChar(lineCount = getMaxCharCountInLine(scale) , char = LineUtils.DASH) ,
                scale = scale
            )
        )
        return this
    }

    /**
     * add empty line to queue
     */
    fun addEmptyLine(): TkamulPrinterBase {
        printQueue.add(TkamulPrinterTextModel(LineUtils.EMPTY_LINE))
        return this
    }


    /**
     * print queue on paper or throw runtime error
     * @return last printed line status
     */

    @Throws(RuntimeException::class)
     fun printOnPaper(result : (LinePrintingStatus)->Unit) {
        coroutineScope.async(Dispatchers.Main){
            // printing service have leaks on mp4+ so you have to wail binding service and dismiss you outgoing job when service disconnected
            setup({
                printingJob =  checkisPrinterReadyThenPrint(result)
            },{
                if (!disconnectShouldWait){
                    // dismiss outgoing printing job
                    printingJob?.cancel()
                    //clear our queue
                    result(LinePrintingStatus().apply {
                        errorMessage = "service is not ready , contact support "
                    })
                }
            })
        }
    }

    private fun checkisPrinterReadyThenPrint(result: (LinePrintingStatus) -> Unit ): Deferred<Unit> {
        return coroutineScope.async(Dispatchers.Main){
            // printing service is ready now
            val printerStatus = getPrinterStatus()
            if (printerStatus.isReady){
                // print and emit last printed line status
                   if (!isPrintRunning){
                       isPrintRunning =true
                       val status = processQueue().await()
                       isPrintRunning =false
                       result(status)
                   }
            }else{
                if (printerStatus.status!=null && printerStatus.status!!.contains("unknown",true) && retryCount < 4 ){
                    disconnectShouldWait = true
                    retryCount +=1
                    //bnhet el tab3a
                    endingPrinterChild()
                    delay(11*1000)
                    // recall this function and return
                    printOnPaper(result)
                }else{
                    // printer not ready and ether out of voltage or out of paper or both of them
                    // clear our queue printQueue.clear() todo un comment this line
                    retryCount =0
                    // retun error to function consumer
                    result(LinePrintingStatus().apply {
                        errorMessage = getPrinterStatus().status
                    })
                }
            }
        }
    }

    // loop on queue
    // call responsible method to print on paper to print text or image  :  printTkamulPrintingDataOnPaper()
    // log printed lines
    // clear queue
    // finish printer
    private suspend fun processQueue(): Deferred<LinePrintingStatus> = coroutineScope.async(Dispatchers.Default){
        // builder to loog queue text
        var logLines =StringBuilder()
        // get last printed line status
        var lastPrintedLineStatus =  LinePrintingStatus()
        while (printQueue.iterator().hasNext()){
            // print head
            lastPrintedLineStatus = printTkamulPrintingDataOnPaper(printQueue.element(),logLines).await()
            // case printer have error
            // then clear queue and end printing
            if (!lastPrintedLineStatus.printed){
                Logger.logd("PrinterLines : $logLines")
                // clear queue
                clearAndFinish()
                return@async lastPrintedLineStatus
            }else{
                // remove head
                printQueue.poll()
            }
        }
        // log printed lines
        Logger.logd("PrinterLines : $logLines")
        // clear queue
        clearAndFinish()
        // return last printed line status
        return@async lastPrintedLineStatus
    }

    private fun clearAndFinish() = coroutineScope.launch(Dispatchers.Main){
        // clear queue
        printQueue.clear()
        // ending printer to save voltage
        endingPrinterChild()
    }


    /**
     * method to print on paper to print text or image
     * @return  LinePrintingStatus obj
     */
    @UiThread
    private suspend fun printTkamulPrintingDataOnPaper(tkamulPrintingData: TkamulPrintingData, logLines :StringBuilder) : Deferred<LinePrintingStatus> = coroutineScope.async(Dispatchers.Main){
        var linePrintingStatus = LinePrintingStatus()
        when(tkamulPrintingData){
            // printing text
            is TkamulPrinterTextModel ->{
                linePrintingStatus = PrintTextOnPaper(tkamulPrintingData)
                logLines.append(tkamulPrintingData.text).also {
                    it.append("/n")
                    it.append("LinePrintingStatus $linePrintingStatus")
                    it.append("/n")
                }
                return@async linePrintingStatus
            }
            //printing image
            is TkamulPrinterImageModel ->{
                linePrintingStatus = PrintImageOnPaper(tkamulPrintingData)
                logLines.append("bitmap").also {
                    it.append("/n")
                    it.append("PrintingStatus $linePrintingStatus")
                    it.append("/n")
                }
                return@async linePrintingStatus
            }
        }
        return@async linePrintingStatus
    }


    companion object{

        @JvmStatic
        var isPrintRunning : Boolean = false

        @JvmStatic
        var printingJob : Job? = null

        @JvmStatic
        private  var retryCount = 0

        @JvmStatic
        var disconnectShouldWait = false

        @JvmStatic
        var printQueue: Queue<TkamulPrintingData> = ArrayDeque()

        /**
         * get printer type by device model
         */
        @JvmStatic
        fun getPrinterType(): PrinterType {
            Logger.logd("model" +""+ Build.MODEL)
            Build.MODEL
            return when (Build.MODEL.trim { it <= ' ' }) {
                Config.MP3_MODEL_NAME -> PrinterType.MOBIEWIRE
                Config.MP4P_MODEL_NAME -> PrinterType.CSPRINTER
                Config.MP4_MODEL_NAME -> PrinterType.CSPRINTER
                Config.MP3P_MODEL_NAME -> PrinterType.CSPRINTER
                else -> PrinterType.BIGSUNMI
            }
        }
    }


}