package com.altkamul.xpay.viewmodel


import android.annotation.SuppressLint
import android.content.Context
import android.nfc.NfcManager
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * A View model with hiltViewModel annotation that is used to access this view model everywhere needed
 */
@HiltViewModel
@SuppressLint("StaticFieldLeak")
class NFCTestViewModel @Inject constructor(
    private val context: Context
) : ViewModel() {

    val isNFCAvailable= mutableStateOf(false)
    val isNFCEnabled  = mutableStateOf(false)

    fun checkNFC(){
        val nfcAdapter =  (context.getSystemService(Context.NFC_SERVICE) as NfcManager?)?.defaultAdapter
        if (nfcAdapter != null && nfcAdapter.isEnabled) {
            /** NFC is available */
            isNFCAvailable.value = true
        } else if (nfcAdapter != null && !nfcAdapter.isEnabled) {
            /** NFC is available but not enabled */
            isNFCAvailable.value = true
            isNFCEnabled.value = false

        } else {
            /** NFC is not available */
            isNFCAvailable.value = false
        }
    }
}