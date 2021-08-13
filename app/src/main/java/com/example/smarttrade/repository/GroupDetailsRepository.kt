package com.example.smarttrade.repository

import com.example.smarttrade.db.dao.GroupPositionDao
import com.example.smarttrade.db.entity.BottomSheetDataObject
import kotlinx.coroutines.flow.Flow

class GroupDetailsRepository(private val groupPositionDao: GroupPositionDao) {

    fun getGroupDetails(groupName: String): Flow<BottomSheetDataObject.GroupDetails> {
        return groupPositionDao.getGroupDetails(groupName)
    }

    fun getAllGroupPosition(): List<BottomSheetDataObject.GroupDetails> {
        return groupPositionDao.getAllGroupPosition()
    }
}