package com.example.scanqrcode

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.google.zxing.BarcodeFormat
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory

object ScanQRCodeInitialize {
    /**
     * You Should Add This Code in OnCreate Activity method
     * registerForActivityResult(ActivityResultContracts.RequestPermission()){}.launch(Manifest.permission.CAMERA)
     */
    fun setupQRCodeConfiguration(
        context: Context
    ): ScannerResult {
        var barCodeScanResult = MutableLiveData("")
        val beepManager = BeepManager((context as Activity))
        val root: View =
            LayoutInflater.from(context).inflate(R.layout.layout, null)
        val barcodeView: DecoratedBarcodeView =
            root.findViewById(R.id.barcode_scanner)
        val formats = listOf(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39, BarcodeFormat.CODABAR)
        barcodeView.decoderFactory = DefaultDecoderFactory(formats)
        barcodeView.setStatusText("")
        barcodeView.initializeFromIntent(context.intent)
        val callback = object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult) {
                if (result.text == null || result.text == barCodeScanResult.value) {
                    return
                }
                barCodeScanResult.value = result.text
                beepManager.playBeepSoundAndVibrate()
            }
        }
        barcodeView.decodeContinuous(callback)

        barcodeView.resume()
        return ScannerResult(root, barCodeScanResult)
    }
}