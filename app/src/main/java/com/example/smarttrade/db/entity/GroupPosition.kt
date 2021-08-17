package com.example.smarttrade.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "Group-Position", indices = [Index(value = ["instrumentToken", "groupName"], unique = true)])
data class GroupPosition(
    @ForeignKey(
        entity = Position::class,
        parentColumns = ["instrumentToken"],
        childColumns = ["instrumentToken"],
        onDelete = ForeignKey.CASCADE
    )
    val instrumentToken: String,
    @ForeignKey(
        entity = Group::class,
        parentColumns = ["groupName"],
        childColumns = ["groupName"],
        onDelete = ForeignKey.CASCADE
    )
    val groupName: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)