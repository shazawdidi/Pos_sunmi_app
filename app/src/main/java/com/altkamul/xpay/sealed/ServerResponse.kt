package com.altkamul.xpay.sealed

sealed class ServerResponse<T>(
    var data: T? = null,
    var message: String? = null,
) {
    class Success<T>(data: T) : ServerResponse<T>(data = data)
    class Error<T>(message: String) : ServerResponse<T>(message = message)
}