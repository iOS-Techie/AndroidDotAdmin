package com.nyotek.dot.admin.ui.tabs.fleets.map

import com.nyotek.dot.admin.common.NSUtilities
import com.nyotek.dot.admin.common.extension.setVisibility
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.databinding.LayoutSelectAddressBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationPickerUI @Inject constructor(private val binding: LayoutSelectAddressBinding, private val colorResources: ColorResources) {

    init {
        binding.apply {
            setUpViews()
        }
    }

    private fun setUpViews() {
        binding.apply {
            colorResources.apply {
                getStringResource().apply {
                    tvAddressTitle.text = address
                    tilAddress.hint = address
                    tilCity.hint = cityTitle
                    tilPostalCode.hint = postalCode
                    tilState.hint = state
                    tilCountry.hint = countryTitle
                    btnSaveAddress.text = save
                    tvAddressDone.text = done
                    tvStandard.text = standardTitle
                    tvSatellite.text = satelliteTitle
                    tvHybrid.text = hybridTitle
                }

                ivAdd.imageTintList = colorResources.getViewEnableDisableState()
                setCardBackground(rlAddressForm, 5f, 1, getWhiteColor(), getGrayColor())
                setCardBackground(rlCityForm, 5f, 1, getWhiteColor(), getGrayColor())
                setCardBackground(rlCountryForm, 5f, 1, getWhiteColor(), getGrayColor())
                setCardBackground(rlPostalCodeForm, 5f, 1, getWhiteColor(), getGrayColor())
                setCardBackground(rlStateForm, 5f, 1, getWhiteColor(), getGrayColor())
                setBackground(clHeader, getSecondaryColor())
                setBackground(viewLine, getGrayColor())
                setBackground(viewLine2, getGrayColor())
                setCardBackground(clTopMapView, 8f, 2)
                setCardBackground(rlSegmentBg, 8f, 0, getGrayColor())
            }
        }
    }

    fun setMapButtonUI(position: Int, binding: LayoutSelectAddressBinding) {
        binding.apply {
            colorResources.apply {
                tvStandard.setBackgroundResource(0)
                tvSatellite.setBackgroundResource(0)
                tvHybrid.setBackgroundResource(0)
                viewLine.setVisibility(position == 2)
                viewLine2.setVisibility(position == 0)

                setWhiteBackgroundRadius5(if (position == 0) tvStandard else if (position == 1) tvSatellite else tvHybrid)
            }
        }
    }
}