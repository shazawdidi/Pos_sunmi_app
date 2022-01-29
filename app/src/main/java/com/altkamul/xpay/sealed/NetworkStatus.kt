package com.altkamul.xpay.sealed

sealed class NetworkStatus {
    object Connected: NetworkStatus()
    object Disconnected: NetworkStatus()
}
