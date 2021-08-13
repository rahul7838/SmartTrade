package com.example.smarttrade.ui.groupdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.smarttrade.db.entity.BottomSheetDataObject
import com.example.smarttrade.repository.GroupDetailsRepository
import com.example.smarttrade.ui.base.BaseViewModel

class GroupDetailsViewModel(private val groupDetailsRepository: GroupDetailsRepository) : BaseViewModel() {

    fun getListOfGroupDetails(groupName: String): LiveData<BottomSheetDataObject.GroupDetails> {
        return groupDetailsRepository.getGroupDetails(groupName).asLiveData()
    }
}