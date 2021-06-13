package com.example.smarttrade.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Group")
data class Group(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val groupName: String
)