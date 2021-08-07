package com.example.smarttrade.ui.group

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.smarttrade.db.entity.Group
import com.example.smarttrade.repository.GroupRepository
import com.example.smarttrade.ui.base.BaseViewModel

class GroupViewModel(private val groupRepository: GroupRepository) : BaseViewModel() {

    fun getListOfGroup(): LiveData<List<Group>> {
        return groupRepository.getListOfGroup().asLiveData()
    }
}