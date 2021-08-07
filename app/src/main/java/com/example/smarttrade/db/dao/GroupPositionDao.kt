package com.example.smarttrade.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.smarttrade.db.entity.GroupDetails
import com.example.smarttrade.db.entity.GroupPosition
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupPositionDao : BaseDao<GroupPosition> {

    @Query("select * from `Group` where groupName=:groupName")
    fun getGroupDetails(groupName: String?): Flow<GroupDetails>

}