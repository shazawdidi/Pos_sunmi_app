package com.altkamul.xpay.utils

import android.app.DownloadManager
import android.content.Context
import android.graphics.*
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.ContextCompat
import com.altkamul.printer.TkamulPrinterFactory
import com.altkamul.printer.models.textFormat.PrintTextAlign
import com.altkamul.printer.models.textFormat.PrinterTextScale
import com.altkamul.xpay.model.Transaction
import com.altkamul.xpay.model.response.print.Data
import com.altkamul.xpay.utils.LoggedMerchantPref.branch
import timber.log.Timber
import java.io.*
import java.util.*


class PrinterUtil(private val context: Context) {
    val printer = TkamulPrinterFactory.getTkamulPrinterOptions(context)

    fun printing(data: List<Data>, transaction: Transaction): List<Data> {


        data.forEach {
            val title = it.title.replace("_", " ", true).replaceFirstChar { char ->
                char.uppercase()
            }

            when {
                ////////////////PRINT BMP IMAGES ///////////////////
                it.title.equals("Image", true) -> {
                    printLogo()
                }
                it.title.equals("Qr", true) -> {
                    printer.addEmptyLine()
                    printer.addEmptyLine()
                    printer.addEmptyLine()
                    printer.addDashLine()
                    printer.addEmptyLine()
                    printer.addEmptyLine()

                }
                it.title.equals("Bar Code", true) -> {
                }

                ////////////////PRINT BMP IMAGES ///////////////////
                ////////////////PRINT SPLITTERS ///////////////////
                it.title.contains("Star") -> {
                    printer.addAsterisksLine()
                }
                it.title.contains("Dash") -> {
                    printer.addDashLine()
                }
                it.title.contains("Line") -> {
                    printer.addEmptyLine()
                }
                ////////////////PRINT SPLITTERS ///////////////////


                /** PRINT TEXT*/
                it.title.equals("Merchant Name", true) -> {
                    printer.addText(branch?.name + "", align = PrintTextAlign.CENTER)
                }
                it.title.equals("date", true) -> {
                    printer.addText(title + ":" + transaction.transactionDateTime)
                }
                it.title.equals("Merchant Id", true) -> {
                    printer.addText(title + ":" + transaction.merchantId)
                }
                it.title.equals("Terminal Id", true) -> {
                    printer.addText(title + ":" + branch?.id)
                }
                it.title.equals("Address", true) -> {
                    printer.addText("$title:" + transaction.address)
                }
                it.title.equals("City", true) -> {
                    printer.addText("$title:" + transaction.city)
                }
                it.title.equals("Country", true) -> {
                    printer.addText("$title: " + transaction.country)
                }
                it.title.equals("Phone", true) -> {
                    printer.addText("$title: " + transaction.phone)
                }
                /** PRINT AFTER CHECKOUT */
                it.title.equals("Transaction Id", true) -> {

                    printer.addText("$title: " + transaction.transactionMasterId)

                }

                it.title.equals("Total Qty", true) -> {
                    printer.addText("$title: " + transaction.totalQty)


                }
                it.title.equals("Total", true) -> {
                    printer.addText("$title: " + transaction.total,
                        PrinterTextScale.medium,
                        align = PrintTextAlign.CENTER)

                }

                /** CUSTOMER DATA FIXED VALUES FOR NOW*/
                it.title.equals("Car Number", true) -> {
                    printer.addText("$title: 348349483495834")
                }
                it.title.equals("Customer No", true) -> {

                    printer.addText("$title: " + transaction.phone)
                }
                /** INVOICE DATA */
                it.title.equals("Header", true) -> {
                    printer.addText("Tax Invoice",
                        PrinterTextScale.medium,
                        align = PrintTextAlign.CENTER)
                }
                it.title.equals("Voucher No", true) -> {
                    printer.addText("$title: " + transaction.voucherNO)
                }
                it.title.equals("total Amount", true) -> {
                    printer.addText("$title: " + transaction.totalAmount)
                }
                it.title.equals("Tax", true) -> {
                    printer.addText("$title: " + transaction.totalTaxAmount)
                }
                 it.title.equals("Name",true)->{
                     printer.addEmptyLine()
                     transaction.transactionDetail.forEach {
                         printer.addText("Name: " + it.itemName)
                         printer.addText("Service: " + it.service)
                         printer.addText("Price: " + it.faceValue.toString())
                         printer.addText("Discount: "+ it.discount.toString())
                         printer.addText( "Charges: " + it.charges.toString())
                         printer.addText("Quantity: " + it.qty.toString())
                         printer.addEmptyLine()
                     }
                 }
                it.title.equals("Service",true)->{
                    transaction.transactionDetail.forEach {

                    }
                }
                it.title.equals("Original Price",true)->{
                    transaction.transactionDetail.forEach {

                    }
                }
                it.title.equals("Discount",true)->{
                    transaction.transactionDetail.forEach {

                    }
                }
                it.title.equals("Charges",true)->{
                    transaction.transactionDetail.forEach {

                    }
                }
                it.title.equals("Quantity",true)->{
                    transaction.transactionDetail.forEach {

                    }
                }


            }
//       return@forEach


        }
        printer.printOnPaper {
            it.printed
        }
        return data
    }


    @Throws(RuntimeException::class)
    fun printImage() {
        DownloadUtilsFake.enqueue(context = context,
            url = "http://smartepaystaging.altkamul.ae/Content/img/printing.bmp",
            downloadListener = object : DownloadUtilsFake.DownloadListener {
                override fun onFinish(
                    referenceId: Long,
                    downloadStatus: DownloadUtilsFake.DownloadStatus,
                ) {
                    printerTester()
                    printer.printOnPaper {
                    }
                }

                override fun deliverStatus(downloadStatus: DownloadUtilsFake.DownloadStatus) {
                    Timber.d("deliverStatus  : $downloadStatus")
                }
            })

    }

    fun printLogo() {
        val downloadQuery = DownloadManager.Query()
        val cursor = ContextCompat.getSystemService(context, DownloadManager::class.java)!!
            .query(downloadQuery)
        if (cursor.moveToFirst()) {
            //get the downloaded filename
            var internalFileName: String? = null
            val filUrlIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
            val fileUrl = cursor.getString(filUrlIndex)
            if (fileUrl != null) {
                internalFileName = Uri.parse(fileUrl).path
            }
            var file = File(internalFileName)
            if (!file.exists()) file = File(internalFileName)
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            val bitmap = BitmapFactory.decodeStream(FileInputStream(file), null, options)
            val arrayBitmap = ArrayList<Bitmap?>()
            val emptyBitmap: Bitmap = "".drawText(
                null, 20, true, false, 0, null
            )
            arrayBitmap.add(emptyBitmap)
            arrayBitmap.add(bitmap)
            val isBlack = FileInputStream(internalFileName)
            val input: ByteArray =
                inputStreamToByte(isBlack)

            printer.addImage(input)
            printer.addEmptyLine()
            printer.addEmptyLine()


        }
    }

    fun printerTester() {
        val downloadQuery = DownloadManager.Query()
        val cursor = ContextCompat.getSystemService(context, DownloadManager::class.java)
            ?.query(downloadQuery)
            ?: throw IllegalArgumentException("Printer tester method - cursor is null !")
        if (cursor.moveToFirst()) {
            //get the downloaded filename
            var internalFileName: String? = null
            val filUrlIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
            val fileUrl = cursor.getString(filUrlIndex)
            if (fileUrl != null) {
                internalFileName = Uri.parse(fileUrl).path
            }
            var file = File(internalFileName)
            if (!file.exists()) file = File(internalFileName)
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            val bitmap = BitmapFactory.decodeStream(FileInputStream(file), null, options)
            val arrayBitmap = ArrayList<Bitmap?>()
            val emptyBitmap: Bitmap = "".drawText(
                null, 20, true, false, 0, null
            )
            arrayBitmap.add(emptyBitmap)
            arrayBitmap.add(bitmap)
            val isBlack = FileInputStream(internalFileName)
            val input: ByteArray =
                inputStreamToByte(isBlack)

            printer.addImage(input)
            printer.addEmptyLine()
            printer.addEmptyLine()
            printer.addText("Name : Altkamul Altiqani")
            printer.addText("Date: 12/2/2021, 10:52:42 AM")
            printer.addText("Branch ID: 34343434534")
            printer.addText("Terminal ID: 435734844743")
            printer.addText("TRN: 02938292")
            printer.addText("Voucher No: 43-45454445")
            printer.addText("Car Number: 348349483495834")
            printer.addText("Customer No: +971 (06) 5288988")
            printer.addAsterisksLine()
            printer.addEmptyLine()
            printer.addText("Tax Invoice", PrinterTextScale.medium, align = PrintTextAlign.CENTER)
            printer.addEmptyLine()
            printer.addDashLine()
            printer.addText("Total QTY: 1.0")
            printer.addText("Total Amount: 100.00 AED")
            printer.addText("Tax: 2.00 AED")
            printer.addAsterisksLine()
            printer.addEmptyLine()
            printer.addText("Total: 0.04", PrinterTextScale.medium, align = PrintTextAlign.CENTER)
            printer.addAsterisksLine()
            printer.addText("Dubai Festival City - Dubai - United Arab Emirates")
            printer.addText("City: Dubai")
            printer.addText("Country:  United Arab Emirates")
            printer.addText("Call Us: +971 (06) 5288988")
            printer.addDashLine()
            printer.addEmptyLine()
            printer.addEmptyLine()

        }
    }

    ////////////////////////////////////////////BITMAP OPERATION ///////////////////////////////////
    @Throws(IOException::class)
    fun inputStreamToByte(`is`: InputStream): ByteArray {
        val byteStream = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var ch: Int
        while (`is`.read(buffer).also { ch = it } != -1) {
            byteStream.write(buffer, 0, ch)
        }
        val data = byteStream.toByteArray()
        byteStream.close()
        return data
    }

    private fun String.drawText(
        textTwo: String?,
        textSize: Int,
        isBold: Boolean,
        isUnderline: Boolean,
        align: Int,
        ttf: Typeface?,
    ): Bitmap {
        var bitmapText = this
        val textWidth = 384
        if (textTwo != null) {
            val leftLen = 20
            val rightLen = 20
            val text1: String = paddingRight(bitmapText, rightLen)
            val text2: String = paddingLeft(textTwo, leftLen)
            bitmapText = text1 + text2
        }
        val textPaint = TextPaint(
            Paint.ANTI_ALIAS_FLAG
                    or Paint.LINEAR_TEXT_FLAG
        )
        textPaint.style = Paint.Style.FILL
        textPaint.color = Color.BLACK
        textPaint.textAlign = Paint.Align.LEFT
        textPaint.textSize = textSize.toFloat()
        textPaint.isFakeBoldText = isBold
        textPaint.typeface = ttf
        if (isUnderline) textPaint.flags = Paint.UNDERLINE_TEXT_FLAG
        var mTextLayout = StaticLayout(
            bitmapText, textPaint,
            textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false)
        when (align) {
            0 -> mTextLayout = StaticLayout(
                bitmapText, textPaint,
                textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false)
            1 -> mTextLayout = StaticLayout(
                bitmapText, textPaint,
                textWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false)
            2 -> mTextLayout = StaticLayout(
                bitmapText, textPaint,
                textWidth, Layout.Alignment.ALIGN_OPPOSITE, 1.0f, 0.0f, false)
        }
        val b = Bitmap.createBitmap(textWidth, mTextLayout.height, Bitmap.Config.RGB_565)
        val c = Canvas(b)
        val paint = Paint(
            (Paint.ANTI_ALIAS_FLAG or Paint.LINEAR_TEXT_FLAG))
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        c.drawPaint(paint)
        c.save()
        c.translate(0f, 0f)
        mTextLayout.draw(c)
        c.restore()
        return b
    }

    private fun paddingRight(s: String?, n: Int): String {
        return String.format("%1$-" + n + "s", s)
    }

    private fun paddingLeft(s: String?, n: Int): String {
        return String.format("%1$" + n + "s", s)
    }
}