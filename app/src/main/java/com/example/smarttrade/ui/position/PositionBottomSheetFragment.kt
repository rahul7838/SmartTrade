package com.example.smarttrade.ui.position

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.smarttrade.BR
import com.example.smarttrade.R
import com.example.smarttrade.databinding.FragmentPositionBottomSheetBinding
import com.example.smarttrade.extension.getViewDataBinding
import com.example.smarttrade.repository.LocalPosition
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
            viewModel.removeStopLoss(localPosition.instrumentToken)
            Toast.makeText(requireContext(), "Stop Loss Removed", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        viewDataBinding.submitStopLoss.setOnClickListener {
            var isStopLossInPercent: Boolean = false
            when(viewDataBinding.selectStopLossNumberFormat.checkedRadioButtonId) {
                R.id.stop_loss_in_number_radio_btn -> {isStopLossInPercent = false}
                R.id.stop_loss_in_percent_radio_btn -> {isStopLossInPercent = true}
                else -> Toast.makeText(requireContext(), "Please select Radio Button", Toast.LENGTH_SHORT).show()
            }
            val stopLoss = viewDataBinding.stopLossInPercent.text.toString().toDouble()
            viewModel.updateStopLoss(localPosition.instrumentToken, localPosition.lastPrice, isStopLossInPercent, stopLoss)
            Toast.makeText(requireContext(), "Stop Loss Updated!!", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    companion object {
        private var instance: PositionBottomSheetFragment? = null
        lateinit var localPosition: LocalPosition
        fun getInstance(position: LocalPosition): PositionBottomSheetFragment {
            localPosition = position
            return instance ?: PositionBottomSheetFragment().also { instance = it }
        }
    }
}