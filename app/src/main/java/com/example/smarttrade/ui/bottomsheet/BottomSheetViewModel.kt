package com.example.smarttrade.ui.bottomsheet

import androidx.lifecycle.viewModelScope
import com.example.smarttrade.db.entity.StopLoss
import com.example.smarttrade.extension.isBuyCall
import com.example.smarttrade.repository.GroupRepository
import com.example.smarttrade.repository.PositionRepository
import com.example.smarttrade.repository.StopLossRepository
import com.example.smarttrade.ui.base.BaseViewModel
import com.example.smarttrade.ui.position.PositionViewModel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinApiExtension

@KoinApiExtension
class BottomSheetViewModel(
    private val groupRepository: GroupRepository,
    private val positionRepository: PositionRepository,
    private val stopLossRepository: StopLossRepository
) : BaseViewModel() {

    fun updateStopLoss(groupName: String, trailingSL: Double?, stopLoss: Double?) {
        ioDispatcher.launch {
            groupRepository.updateStopLossAmount(groupName, trailingSL, stopLoss)
        }
    }

    fun removeStopLoss(instrumentToken: String) {
        ioDispatcher.launch {
            stopLossRepository.updateAndInsertStopLoss(StopLoss(instrumentToken, null, null))
        }
    }

    fun updateStopLoss(
        instrumentToken: String,
        lastPrice: Double,
        averagePrice: Double,
        netQuantity: Int,
        @PositionViewModel.StopLossType stopLossType: String,
        stopLoss: Double
    ) {
        ioDispatcher.launch {
            val isBuyCall = netQuantity.isBuyCall()
            if (isBuyCall) {
                when (stopLossType) {
                    PositionViewModel.StopLossType.STOP_LOSS_PERCENT -> {
                        val findStopLossValue = lastPrice * (1 - stopLoss / 100)
                        stopLossRepository.updateAndInsertStopLoss(
                            StopLoss(
                                instrumentToken,
                                stopLossInPercent = stopLoss,
                                stopLossPrice = findStopLossValue
                            )
                        )
                    }
                    PositionViewModel.StopLossType.STOP_LOSS_PRICE -> {
                        val findStopLossInPercent = 100 * (1 - stopLoss / lastPrice)
                        stopLossRepository.updateAndInsertStopLoss(
                            StopLoss(
                                instrumentToken,
                                findStopLossInPercent,
                                stopLoss
                            )
                        )
                    }
                    PositionViewModel.StopLossType.STOP_LOSS_PNL -> {
                        stopLossRepository.updateAndInsertStopLoss(
                            StopLoss(
                                instrumentToken,
                                stopLoss,
                                null,
                                stopLoss
                            )
                        )
                    }
                }
            } else {
                when (stopLossType) {
                    PositionViewModel.StopLossType.STOP_LOSS_PERCENT -> {
                        val findStopLossValue = lastPrice * (1 + stopLoss / 100)
                        stopLossRepository.updateAndInsertStopLoss(
                            StopLoss(
                                instrumentToken,
                                stopLoss,
                                findStopLossValue
                            )
                        )
                    }
                    PositionViewModel.StopLossType.STOP_LOSS_PRICE -> {
                        val findStopLossInPercent = 100 * (1 + stopLoss / lastPrice)
                        stopLossRepository.updateAndInsertStopLoss(
                            StopLoss(
                                instrumentToken,
                                findStopLossInPercent,
                                stopLoss
                            )
                        )
                    }
                    PositionViewModel.StopLossType.STOP_LOSS_PNL -> {
                        stopLossRepository.updateAndInsertStopLoss(
                            StopLoss(
                                instrumentToken,
                                stopLoss,
                                null,
                                stopLoss
                            )
                        )
                    }
                }
            }
        }
    }

    suspend fun getStopLoss(groupName: String): Double? {
        return groupRepository.getStopLoss(groupName)
    }

}