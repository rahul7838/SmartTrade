package com.example.smarttrade.repository

import androidx.room.withTransaction
import com.example.smarttrade.db.PositionDatabase
import com.example.smarttrade.db.dao.GroupDao
import com.example.smarttrade.db.dao.GroupPositionDao
import com.example.smarttrade.db.entity.BottomSheetDataObject
import com.example.smarttrade.db.entity.Group
import com.example.smarttrade.db.entity.GroupPosition
import kotlinx.coroutines.flow.Flow

class GroupRepository(
    private val groupDao: GroupDao,
    private val groupPositionDao: GroupPositionDao,
    private val database: PositionDatabase
) {

    suspend fun updateGroup(listOfPositionWithStopLoss: List<BottomSheetDataObject.PositionWithStopLoss>) {
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
                groupDao.insert(Group(groupName, totalPnl, null, null))
                listOfPositionWithStopLoss.forEach {
                    groupPositionDao.insert(GroupPosition(it.position.instrumentToken, groupName))
                }
            }
        }
    }

    suspend fun updateStopLossAmount(groupName: String, trailingSL: Double?, stopLoss: Double?) {
        groupDao.updateStopLossAmount(groupName, trailingSL, stopLoss)
    }

    suspend fun updatePnl(groupName: String, pnl: Double, trailingSL: Double?) {
        groupDao.updatePnl(groupName, pnl, trailingSL)
    }

    fun getListOfGroup(): Flow<List<Group>> {
        return groupDao.getListOfGroup()
    }

    suspend fun getStopLoss(groupName: String): Double? {
        return groupDao.getStopLoss(groupName)
    }

}