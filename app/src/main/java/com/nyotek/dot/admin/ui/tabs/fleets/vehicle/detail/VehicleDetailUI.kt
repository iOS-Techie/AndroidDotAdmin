package com.nyotek.dot.admin.ui.tabs.fleets.vehicle.detail

import android.app.Activity
import com.nyotek.dot.admin.common.extension.gone
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.databinding.NsFragmentVehicleDetailBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VehicleDetailUI @Inject constructor(private val activity: Activity, private val binding: NsFragmentVehicleDetailBinding, private val colorResources: ColorResources) {

    init {
        binding.apply {
            colorResources.setCardBackground(clVehicleItem, 10f, 1, colorResources.getWhiteColor(), colorResources.getBorderColor())
            tvStatus.gone()
            setTextViews()
        }
    }

    private fun setTextViews() {
        binding.apply {
            colorResources.apply {
                getStringResource().apply {
                    layoutHomeHeader.tvHeaderTitle.text = vehicleDetails
                    tvVehicleTitle.text = vehicleDetails
                    layoutManufacturer.tvCommonTitle.text = manufacturer
                    layoutManufacturerYear.tvCommonTitle.text = manufacturerYear
                    layoutLoadCapacity.tvCommonTitle.text = loadCapacity
                    layoutModel.tvCommonTitle.text = model
                    layoutRegistrationNo.tvCommonTitle.text = vehicleRegistrationNo
                    layoutCapability.tvCommonTitle.text = capability
                    layoutNotes.tvCommonTitle.text = additionalNote
                    spinner.tvCommonTitle.text = updateDriver
                    clVehicleItem.gone()
                    layoutManufacturer.edtValue.isEnabled = false
                    layoutManufacturerYear.edtValue.isEnabled = false
                    layoutModel.edtValue.isEnabled = false
                    layoutRegistrationNo.edtValue.isEnabled = false
                    layoutLoadCapacity.edtValue.isEnabled = false
                }
            }
        }
    }
}