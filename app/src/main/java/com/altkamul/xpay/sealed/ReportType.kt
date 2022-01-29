package com.altkamul.xpay.sealed

import com.altkamul.xpay.R

sealed class ReportType(val title: Int){
    object TransactionsReport: ReportType(title = R.string.transactions_reports)
    object ProductsReport: ReportType(title = R.string.products_reports)
    object CashiersReport: ReportType(title = R.string.cashiers_reports)
}
