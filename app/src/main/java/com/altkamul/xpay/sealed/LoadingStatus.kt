package com.altkamul.xpay.sealed

sealed class LoadingStatus() {
    object start: LoadingStatus()
    object notStart: LoadingStatus()
}
//sealed class LoadingConfig() {
//    object start: LoadingConfig()
//    object notStart: LoadingConfig()
//}
//sealed class Loadingcategories() {
//    object start: Loadingcategories()
//    object notStart: Loadingcategories()
//}
//sealed class LoadingsubCategory() {
//    object start: LoadingsubCategory()
//    object notStart: LoadingsubCategory()
//}
//sealed class LoadingItems() {
//    object start: LoadingItems()
//    object notStart: LoadingItems()
//}
//sealed class LoadingLayout() {
//    object start: LoadingLayout()
//    object notStart: LoadingLayout()
//}