package com.example.smarttrade.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.smarttrade.db.dao.GroupDao
import com.example.smarttrade.db.dao.GroupPositionDao
import com.example.smarttrade.db.dao.PositionDao
import com.example.smarttrade.db.dao.StopLossDao
import com.example.smarttrade.db.entity.Group
import com.example.smarttrade.db.entity.GroupPosition
import com.example.smarttrade.db.entity.Position
import com.example.smarttrade.db.entity.StopLoss

@Database(entities = [Group::class, GroupPosition::class, Position::class, StopLoss::class], version = 1, exportSchema = true)
abstract class PositionDatabase : RoomDatabase() {
    abstract fun getPositionDao(): PositionDao
    abstract fun getGroupPositionDao(): GroupPositionDao
    abstract fun getGroupDao(): GroupDao
    abstract fun getStopLossDao(): StopLossDao
}