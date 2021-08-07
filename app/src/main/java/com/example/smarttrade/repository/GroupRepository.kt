package com.example.smarttrade.repository

import androidx.room.withTransaction
import com.example.smarttrade.db.PositionDatabase
import com.example.smarttrade.db.dao.GroupDao
import com.example.smarttrade.db.dao.GroupPositionDao
import com.example.smarttrade.db.entity.Group
import com.example.smarttrade.db.entity.GroupPosition
import com.example.smarttrade.db.entity.PositionWithStopLoss
import kotlinx.coroutines.flow.Flow

class GroupRepository(
    private val groupDao: GroupDao,
    private val groupPositionDao: GroupPositionDao,
    private val database: PositionDatabase
) {

    suspend fun updateGroup(listOfPositionWithStopLoss: List<PositionWithStopLoss>) {
        if (listOfPositionWithStopLoss.size >= 2) {
            val item1 = listOfPositionWithStopLoss[0]
            val item2 = listOfPositionWithStopLoss[1]
            val groupName =
                item1.position.tradingSymbol.commonPrefixWith(item2.position.tradingSymbol)
            var totalPnl = 0.0
            listOfPositionWithStopLoss.forEach {
                totalPnl += it.position.pnl
            }
            database.withTransaction {
                groupDao.insert(Group(groupName, totalPnl, null))
                listOfPositionWithStopLoss.forEach {
                    groupPositionDao.insert(GroupPosition(groupName, it.position.instrumentToken))
                }
            }
        }
    }

    fun getListOfGroup(): Flow<List<Group>> {
        return groupDao.getListOfGroup()
    }
}