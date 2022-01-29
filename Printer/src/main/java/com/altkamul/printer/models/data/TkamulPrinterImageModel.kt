package com.altkamul.printer.models.data

import android.graphics.Bitmap


class TkamulPrinterImageModel : TkamulPrintingData {


    var bitmap:ByteArray?
    var path: Bitmap? = null

    constructor(bitmap: ByteArray) {
        this.bitmap = bitmap
        path = null
    }

    constructor(bitmapPath: Bitmap) {
        path = bitmapPath
        bitmap = null
    }

}