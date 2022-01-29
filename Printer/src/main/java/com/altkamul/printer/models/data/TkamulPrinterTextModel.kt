package com.altkamul.printer.models.data

import com.altkamul.printer.models.textFormat.PrintTextAlign
import com.altkamul.printer.models.textFormat.PrintTextDirction
import com.altkamul.printer.models.textFormat.PrinterTextScale


class TkamulPrinterTextModel(
    var text: String = "",
    var scale: PrinterTextScale = PrinterTextScale.normal,
    var align: PrintTextAlign = PrintTextAlign.LEFT,
    var dirction: PrintTextDirction = PrintTextDirction.LTR,
    var isBold: Boolean = false
): TkamulPrintingData()