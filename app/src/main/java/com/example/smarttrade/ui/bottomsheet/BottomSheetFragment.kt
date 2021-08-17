package com.example.smarttrade.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import androidx.navigation.fragment.findNavController
import com.example.smarttrade.BR
import com.example.smarttrade.R
import com.example.smarttrade.databinding.FragmentPositionBottomSheetBinding
import com.example.smarttrade.db.entity.BottomSheetDataObject
import com.example.smarttrade.db.entity.Group
import com.example.smarttrade.extension.*
import com.example.smarttrade.ui.position.PositionViewModel.StopLossType.Companion.STOP_LOSS_PERCENT
import com.example.smarttrade.ui.position.PositionViewModel.StopLossType.Companion.STOP_LOSS_PNL
import com.example.smarttrade.ui.position.PositionViewModel.StopLossType.Companion.STOP_LOSS_PRICE
import com.example.smarttrade.util.Constants.GROUP_ID
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension

@KoinApiExtension
class BottomSheetFragment(private val bottomSheetDataObject: BottomSheetDataObject) : BottomSheetDialogFragment() {

    private val viewModel: BottomSheetViewModel by viewModel()

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
        logI("onViewCreated")
        when (bottomSheetDataObject) {
            is Group -> {
                viewDataBinding.selectStopLossNumberFormat.gone()
                viewDataBinding.groupDetails.visible()
                lifecycleScope.launchWhenCreated {
                    val stopLoss = viewModel.getStopLoss(bottomSheetDataObject.groupName)
                    if(stopLoss != null) viewDataBinding.stopLoss.setText(stopLoss.toString())
                }
            }
            else -> {
                viewDataBinding.selectStopLossNumberFormat.visible()
                viewDataBinding.groupDetails.gone()
            }
        }
        setClickListener()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        logI("onActivityCreated")
    }

    private fun setClickListener() {
        removeStopLossClickListener()
        submitStopLossClickListener()
        seeDetailsClickListener()
    }

    private fun seeDetailsClickListener() {
        viewDataBinding.groupDetails.setOnClickListener {
            if (bottomSheetDataObject is Group) {
                val bundle = bundleOf(GROUP_ID to bottomSheetDataObject.groupName)
                findNavController().navigate(R.id.groupPositionDetailsFragment, bundle)
                dismiss()
            }
        }
    }

    private fun submitStopLossClickListener() {
        viewDataBinding.submitStopLoss.setOnClickListener {
            val stopLoss = viewDataBinding.stopLoss.text.toString().toDouble("stop loss can not be empty", requireContext())
            when(bottomSheetDataObject) {
                is BottomSheetDataObject.PositionWithStopLoss -> {
                    val stopLossType = getStopLossType()
                    val position = bottomSheetDataObject.position
                    viewModel.updateStopLoss(
                        position.instrumentToken,
                        position.lastPrice,
                        position.averagePrice,
                        position.netQuantity,
                        stopLossType,
                        stopLoss
                    )
                }
                is Group -> {
                    //initially trailing stop loss and stop loss is same
                    viewModel.updateStopLoss(bottomSheetDataObject.groupName, -stopLoss, stopLoss)
                }
                else -> {

                }
            }

            Toast.makeText(requireContext(), "Stop Loss Updated!!", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    private fun removeStopLossClickListener() {
        viewDataBinding.removeStopLoss.setOnClickListener {
            when(bottomSheetDataObject) {
                is Group -> {
                    viewModel.updateStopLoss(bottomSheetDataObject.groupName, null,  null)
                    Toast.makeText(requireContext(), "Stop Loss Removed", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                is BottomSheetDataObject.PositionWithStopLoss -> {
                    viewModel.removeStopLoss(bottomSheetDataObject.position.instrumentToken)
                    Toast.makeText(requireContext(), "Stop Loss Removed", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                else -> {

                }
            }

        }
    }

    private fun getStopLossType(): String {
        return when (viewDataBinding.selectStopLossNumberFormat.checkedRadioButtonId) {
            R.id.stop_loss_in_number_radio_btn -> { STOP_LOSS_PRICE }
            R.id.stop_loss_in_percent_radio_btn -> { STOP_LOSS_PERCENT }
            R.id.stop_loss_pnl -> { STOP_LOSS_PNL }
            else -> { Toast.makeText(requireContext(), "Please select Radio Button", Toast.LENGTH_SHORT).show()
                ""
            }
        }
    }

    companion object {
//        var instance: PositionBottomSheetFragment? = null
        fun getInstance(positionWithStopLoss: BottomSheetDataObject): BottomSheetFragment {
            return BottomSheetFragment(positionWithStopLoss)
        }
    }
}