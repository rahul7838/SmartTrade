package com.example.smarttrade.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Group-Position")
data class GroupPosition(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val positionId: Int,
    val groupId: Int
)