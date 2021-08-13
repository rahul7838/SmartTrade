package com.example.smarttrade.ui.position

import androidx.annotation.StringDef
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.smarttrade.db.entity.BottomSheetDataObject
import com.example.smarttrade.db.entity.StopLoss
import com.example.smarttrade.extension.calculateTrigger
import com.example.smarttrade.extension.isBuyCall
import com.example.smarttrade.extension.logI
import com.example.smarttrade.repository.GroupRepository
import com.example.smarttrade.repository.PositionRepository
import com.example.smarttrade.repository.StopLossRepository
import com.example.smarttrade.ui.base.BaseViewModel
import com.example.smarttrade.ui.position.PositionViewModel.StopLossType.Companion.STOP_LOSS_PERCENT
import com.example.smarttrade.ui.position.PositionViewModel.StopLossType.Companion.STOP_LOSS_PNL
import com.example.smarttrade.ui.position.PositionViewModel.StopLossType.Companion.STOP_LOSS_PRICE
import kotlinx.coroutines.launch
import org.koin.core.component.KoinApiExtension

@KoinApiExtension
class PositionViewModel(
    private val positionRepository: PositionRepository,
    private val stopLossRepository: StopLossRepository,
    private val groupRepository: GroupRepository,
    requestToken: String?
) : BaseViewModel() {

    init {
        if (requestToken != null) {
            createSession(requestToken)
        }
    }

    private fun createSession(requestToken: String) {
        ioDispatcher.launch {
            positionRepository.createSession(requestToken)
            positionRepository.fetchAndInsertPosition()
        }
    }

    fun refreshPosition() {
        ioDispatcher.launch {
            val instrumentToken = positionRepository.getInstrument()
            val quotes = positionRepository.getQuote(instrumentToken)
            quotes.forEach {
                calculateTrigger(
                    positionRepository,
                    stopLossRepository,
                    it.key,
                    it.value.lastPrice
                )
            }
        }
    }

    fun getPosition(): LiveData<List<BottomSheetDataObject.PositionWithStopLoss>> {
        return positionRepository.getPosition().asLiveData()
    }

    suspend fun getTime(): Long {
        return positionRepository.getTime()
    }


    override fun onCleared() {
        super.onCleared()
        logI("onClear")
    }

    fun updateGroup(listOfPositionWithStopLoss: List<BottomSheetDataObject.PositionWithStopLoss>) {
        viewModelScope.launch {
            groupRepository.updateGroup(listOfPositionWithStopLoss)
        }
    }

    @StringDef(value = [STOP_LOSS_PERCENT, STOP_LOSS_PRICE, STOP_LOSS_PNL], open = false)
    annotation class StopLossType {
        companion object {
            const val STOP_LOSS_PERCENT = "stopLossPercent"
            const val STOP_LOSS_PRICE = "stopLossPrice"
            const val STOP_LOSS_PNL = "stopLossPnl"
        }
    }
}