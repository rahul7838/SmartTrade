package com.example.smarttrade.db.dao

import androidx.room.*
import com.example.smarttrade.db.entity.Group
import com.example.smarttrade.db.entity.GroupPosition
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao : BaseDao<Group> {

    @Transaction
    suspend fun updateGroupAndCommonTable(groupName: String, totalPnl: Double, stopLossAmount: Double, instrumentToken: String) {
        insert(Group(groupName, totalPnl, stopLossAmount))
        insertGroupPosition((GroupPosition(instrumentToken, groupName)))
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupPosition(groupPosition: GroupPosition)

    @Query("Select * from `Group`")
    fun getListOfGroup(): Flow<List<Group>>


}