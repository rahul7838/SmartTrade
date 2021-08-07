package com.example.smarttrade.db.entity

import androidx.room.*

@Entity(tableName = "Group")
data class Group(
    @PrimaryKey
    val groupName: String,
    val totalPnl: Double,
    val stopLossAmount: Double?
//    @PrimaryKey(autoGenerate = true)
//    val id: Int = 0
)

data class GroupDetails(
    @Embedded val group: Group,
    @Relation(
        parentColumn = "groupName",
        entityColumn = "instrumentToken",
        associateBy = Junction(GroupPosition::class)
    )
    val listOfPosition: List<Position>
)

