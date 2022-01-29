package com.altkamul.xpay.model.response.local

import androidx.room.Embedded
import androidx.room.Relation
import com.altkamul.xpay.model.*

data class BranchWithData(
    @Embedded
    val branch: Branch,
    @Relation(parentColumn = "id", entityColumn = "branchId")
    val address: Address,
    @Relation(parentColumn = "id", entityColumn = "branchId")
    val image: Images,
    @Relation(parentColumn = "id", entityColumn = "branchId")
    val language: Language,
    @Relation(parentColumn = "id", entityColumn = "branchId")
    val userBranch: List<BranchesUsers>,
    @Relation(parentColumn = "id", entityColumn = "branchId")
    val terminal: List<Terminal>,
)