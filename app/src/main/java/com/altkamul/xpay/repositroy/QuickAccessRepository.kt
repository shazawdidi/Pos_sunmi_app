package com.altkamul.xpay.repositroy

import com.altkamul.xpay.db.DataAccessObject
import com.altkamul.xpay.model.QuickItem
import javax.inject.Inject


class QuickAccessRepository @Inject constructor(
    private val dao: DataAccessObject
) {

    /** A function to store the items to quick access table */
    suspend fun saveQuickAccessItems(items: MutableList<QuickItem>){
        dao.insertQuickAccessItems(items = items)
    }
}