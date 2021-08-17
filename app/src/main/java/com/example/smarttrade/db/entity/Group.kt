package com.example.smarttrade.db.entity

import androidx.room.*

@Entity(tableName = "Group")
data class Group(
    @PrimaryKey
    val groupName: String,
    val totalPnl: Double,
    val trailingSL: Double?,
    val stopLoss: Double?,
): BottomSheetDataObject()



sealed class BottomSheetDataObject {
    data class GroupDetails(
        @Embedded val group: Group,
        @Relation(
            parentColumn = "groupName",
            entityColumn = "instrumentToken",
            associateBy = Junction(GroupPosition::class)
        )
        val listOfPosition: List<Position>
    ) : BottomSheetDataObject()

    data class PositionWithStopLoss(
        @Embedded
        val position: Position,
        @Relation(
            parentColumn = "instrumentToken",
            entityColumn = "instrumentToken"
        )
        val stopLoss: StopLoss?
    ) : BottomSheetDataObject()
}

