package com.example.smarttrade.db.dao

import androidx.room.*
import com.example.smarttrade.db.entity.Group
import com.example.smarttrade.db.entity.GroupPosition
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao : BaseDao<Group> {

//    @Transaction
//    suspend fun updateGroupAndCommonTable(groupName: String, totalPnl: Double, stopLossAmount: Double, instrumentToken: String) {
//        insert(Group(groupName, totalPnl, stopLossAmount))
//        insertGroupPosition((GroupPosition(instrumentToken, groupName)))
//    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupPosition(groupPosition: GroupPosition)

    @Query("Select * from `Group` where groupName IN (select groupName from `Group-Position` Group by groupName)")
    fun getListOfGroup(): Flow<List<Group>>

    @Query("Update `Group` set trailingSL=:trailingSL, stopLoss=:stopLoss where groupName=:groupName")
    suspend fun updateStopLossAmount(groupName: String, trailingSL: Double?, stopLoss:Double?)

    @Query("Update `Group` set totalPnl=:pnl, trailingSL=:trailingSL where groupName=:groupName")
    suspend fun updatePnl(groupName: String, pnl: Double, trailingSL: Double?)

    @Query("Select stopLoss from `Group` where groupName=:groupName")
    suspend fun getStopLoss(groupName: String): Double?

    @Delete
    suspend fun delete(listOfGroup: List<Group>)
}