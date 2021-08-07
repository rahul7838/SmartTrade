package com.example.smarttrade.ui.groupdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.smarttrade.db.entity.GroupDetails
import com.example.smarttrade.repository.GroupDetailsRepository
import com.example.smarttrade.ui.base.BaseViewModel

class GroupDetailsViewModel(private val groupDetailsRepository: GroupDetailsRepository) : BaseViewModel() {

    fun getListOfGroupDetails(groupName: String?): LiveData<GroupDetails> {
        return groupDetailsRepository.getGroupDetails(groupName).asLiveData()
    }
}