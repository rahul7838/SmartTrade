package com.example.smarttrade.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.smarttrade.db.entity.Position

@Dao
interface PositionDao {

    @Insert(entity = Position::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosition(position: Position)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPositions(positions: List<Position>)

    @Query("select * from Position")
    suspend fun getPosition(): List<Position>
}