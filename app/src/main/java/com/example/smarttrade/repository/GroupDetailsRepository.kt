package com.example.smarttrade.repository

import com.example.smarttrade.db.dao.GroupPositionDao
import com.example.smarttrade.db.entity.GroupDetails
import kotlinx.coroutines.flow.Flow

class GroupDetailsRepository(private val groupPositionDao: GroupPositionDao) {

    fun getGroupDetails(groupName: String?): Flow<GroupDetails> {
        return groupPositionDao.getGroupDetails(groupName)
    }
}