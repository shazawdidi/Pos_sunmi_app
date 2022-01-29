package com.altkamul.xpay.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.altkamul.xpay.model.*
import com.altkamul.xpay.model.response.TransactionPayment
import com.altkamul.xpay.model.response.print.Data

@Database(
    entities = [
        Merchant::class, Address::class, Branch::class, Terminal::class, Images::class,
        BranchesUsers::class, Permission::class, City::class, Language::class,
        LocalDataVersions::class, Category::class,
        SubCategory::class, Item::class,PosRole::class,
        ContactUs::class, LocalMainConfiguration::class, Tax::class,
        TaxType::class, TransactionItem::class, Transaction::class, QuickItem::class, Data::class, TransactionPayment::class,Cashiers::class],
    version = 1,
    exportSchema = false
)
abstract class DataBase : RoomDatabase() {
    abstract fun getDataAccessObject(): DataAccessObject
}