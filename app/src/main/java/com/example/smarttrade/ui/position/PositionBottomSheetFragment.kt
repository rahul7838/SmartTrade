package com.example.smarttrade.ui.position

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.smarttrade.BR
import com.example.smarttrade.R
import com.example.smarttrade.databinding.FragmentPositionBottomSheetBinding
import com.example.smarttrade.db.entity.PositionWithStopLoss
import com.example.smarttrade.extension.getViewDataBinding
import com.example.smarttrade.extension.toDouble
import com.example.smarttrade.repository.LocalPosition
import com.example.smarttrade.ui.position.PositionViewModel.StopLossType.Companion.STOP_LOSS_PERCENT
import com.example.smarttrade.ui.position.PositionViewModel.StopLossType.Companion.STOP_LOSS_PNL
import com.example.smarttrade.ui.position.PositionViewModel.StopLossType.Companion.STOP_LOSS_PRICE
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.component.KoinApiExtension

@KoinApiExtension
class PositionBottomSheetFragment : BottomSheetDialogFragment() {

    private val viewModel: PositionViewModel by sharedViewModel()

    lateinit var viewDataBinding: FragmentPositionBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        viewDataBinding = getViewDataBinding(
            R.layout.fragment_position_bottom_sheet,
            container,
            viewModel,
            BR.viewModel
        )
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListener()
    }

    private fun setClickListener() {
        viewDataBinding.removeStopLoss.setOnClickListener {
            viewModel.removeStopLoss(positionWithStopLoss1.position.instrumentToken)
            Toast.makeText(requireContext(), "Stop Loss Removed", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        viewDataBinding.submitStopLoss.setOnClickListener {
            var stopLossType = ""
            when (viewDataBinding.selectStopLossNumberFormat.checkedRadioButtonId) {
                R.id.stop_loss_in_number_radio_btn -> {
                    stopLossType = STOP_LOSS_PRICE
                }
                R.id.stop_loss_in_percent_radio_btn -> {
                    stopLossType = STOP_LOSS_PERCENT
                }
                R.id.stop_loss_pnl -> {
                    stopLossType = STOP_LOSS_PNL
                }
                else -> Toast.makeText(
                    requireContext(),
                    "Please select Radio Button",
                    Toast.LENGTH_SHORT
                ).show()
            }
            val stopLoss = viewDataBinding.stopLoss.text.toString()
                .toDouble("stop loss can not be empty", requireContext())
            val position = positionWithStopLoss1.position
            viewModel.updateStopLoss(
                position.instrumentToken,
                position.lastPrice,
                position.averagePrice,
                position.netQuantity,
                stopLossType,
                stopLoss
            )
            Toast.makeText(requireContext(), "Stop Loss Updated!!", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    companion object {
        private var instance: PositionBottomSheetFragment? = null
        lateinit var positionWithStopLoss1: PositionWithStopLoss
        fun getInstance(positionWithStopLoss: PositionWithStopLoss): PositionBottomSheetFragment {
            positionWithStopLoss1 = positionWithStopLoss
            return instance ?: PositionBottomSheetFragment().also { instance = it }
        }
    }
}