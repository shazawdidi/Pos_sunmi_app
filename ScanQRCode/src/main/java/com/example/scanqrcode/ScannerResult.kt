package com.example.scanqrcode

import android.view.View
import androidx.lifecycle.LiveData

data class ScannerResult(val view: View, val barCodeScanResult: LiveData<String>)