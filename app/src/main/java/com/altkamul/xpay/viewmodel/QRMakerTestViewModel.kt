package com.altkamul.xpay.viewmodel


import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * A View model with hiltViewModel annotation that is used to access this view model everywhere needed
 */
@HiltViewModel
class QRMakerTestViewModel @Inject constructor() : ViewModel() {

    private val _qr = MutableLiveData<Bitmap>()
    val qr: LiveData<Bitmap> = _qr

    fun createQr(text: String){
        QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, 512, 512).let {
            val width = 512
            val height = 512
            val tempBitMap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    tempBitMap.setPixel(x, y, if (it.get(x, y)) Color.BLACK else Color.WHITE)
                }
            }
            _qr.value = tempBitMap
        }
    }

}