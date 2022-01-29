package com.altkamul.xpay.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "QuickItem_table")
data class QuickItem(
    @PrimaryKey val itemId: Int,
    val branchId: String,
    val date: String,
    val auto: Boolean = false, /** Indicate whether or not its added automatically - items that are frequently bought , used in later phases */
)
