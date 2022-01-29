package com.altkamul.xpay.viewmodel


import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.scanqrcode.ScanQRCodeInitialize
import com.example.scanqrcode.ScannerResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * A View model with hiltViewModel annotation that is used to access this view model everywhere needed
 */
@HiltViewModel
class QRReaderTestViewModel @Inject constructor() : ViewModel() {

    val showQrReader = mutableStateOf(true)
    val qrText = mutableStateOf("")

    fun updateQrReaderState(){
        showQrReader.value = showQrReader.value.not()
    }

    fun updateQrText(text: String){
        qrText.value = text
    }

    /** Setup Configuration For QRCode Scanner Test */
    fun setupQRCodeConfiguration(context: Context): ScannerResult {
        return ScanQRCodeInitialize.setupQRCodeConfiguration(context)
    }
}