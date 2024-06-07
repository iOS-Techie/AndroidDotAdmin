package com.nyotek.dot.admin.ui.tabs.services

import android.app.Activity
import com.nyotek.dot.admin.common.NSUtilities
import com.nyotek.dot.admin.common.extension.getLngValue
import com.nyotek.dot.admin.common.extension.isValidList
import com.nyotek.dot.admin.common.extension.rotation
import com.nyotek.dot.admin.common.extension.setPlaceholderAdapter
import com.nyotek.dot.admin.common.extension.setVisibility
import com.nyotek.dot.admin.common.extension.status
import com.nyotek.dot.admin.common.extension.switchEnableDisable
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.databinding.LayoutCommonSpinnerBinding
import com.nyotek.dot.admin.databinding.LayoutServiceItemBinding
import com.nyotek.dot.admin.databinding.NsFragmentServiceManagementBinding
import com.nyotek.dot.admin.models.responses.CapabilitiesDataItem
import com.nyotek.dot.admin.models.responses.FleetData
import com.nyotek.dot.admin.models.responses.FleetServiceResponse
import com.nyotek.dot.admin.models.responses.ServiceCapabilitiesDataItem
import com.nyotek.dot.admin.models.responses.SpinnerData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceUI @Inject constructor(private val activity: Activity, private val binding: NsFragmentServiceManagementBinding, private val viewModel: ServiceViewModel, private val colorResources: ColorResources) {

    init {
        binding.apply {
            setTextViews()
        }
    }

    private fun setTextViews() {
        binding.apply {
            colorResources.apply {

            }
        }
    }

    fun setServiceAdapterUI(bind: LayoutServiceItemBinding, isActive: Boolean) {
        bind.apply {
            colorResources.apply {
                getStringResource().apply {
                    layoutFleets.tvCommonTitle.text = fleet
                    layoutFleets.clSelectAll.setVisibility(true)
                    switchService.switchEnableDisable(isActive)
                    tvItemActive.status(isActive)
                    tvViewMore.text = update
                    spinner.tvCommonTitle.text = capability.lowercase()

                }
                switchService.rotation(viewModel.languageConfig.isLanguageRtl())
                tvViewMore.setTextColor(getWhiteColor())
                setBackgroundTint(clViewMore, getPrimaryColor())
            }
        }
    }

    fun fetchServiceCapabilities(bind: LayoutServiceItemBinding, serviceId: String?, capabilities: MutableList<CapabilitiesDataItem>?, callback: (String) -> Unit, callbackCapabilities: (ServiceCapabilitiesDataItem?) -> Unit) {
        fetchServiceCapabilitiesData(serviceId) {
            callbackCapabilities.invoke(it)
            setCapabilitySpinner(activity, capabilities, it, bind.spinner) { selectedCapabilities ->
                it?.capabilityId = selectedCapabilities
                callback.invoke(selectedCapabilities)
            }
        }
    }

    private fun fetchServiceCapabilitiesData(serviceId: String?, callback: (ServiceCapabilitiesDataItem?) -> Unit) {
        viewModel.apply {
            val serviceCapabilities = colorResources.themeHelper.getCapabilityItemList()[serviceId]
            if (serviceCapabilities != null) {
                callback.invoke(serviceCapabilities)
            } else {
                getServiceCapability(serviceId, callback)
            }
        }
    }

    private fun setCapabilitySpinner(activity: Activity, capabilities: MutableList<CapabilitiesDataItem>?, item: ServiceCapabilitiesDataItem?, spinner: LayoutCommonSpinnerBinding, itemCallback: ((String) -> Unit)) {
        val nameList: MutableList<String> = (capabilities?.map { getLngValue(it.label) }?: arrayListOf()).toMutableList()
        val idList: MutableList<String> = (capabilities?.map { it.id ?: "" }?: arrayListOf()).toMutableList()
        val spinnerList = SpinnerData(idList, nameList)

        spinner.spinnerAppSelect.setPlaceholderAdapter(spinnerList, activity, colorResources, item?.capabilityId, isHideFirstPosition = true, placeholderName = colorResources.getStringResource().selectCapability) { selectedId ->
            if (selectedId != item?.capabilityId && selectedId?.isNotEmpty() == true) {
                item?.capabilityId = selectedId
                itemCallback.invoke(selectedId)
            }
        }
    }

    /*---------------------------------------------------------------------------------------------------------------------------*/

    //Set Fleets
    fun setFleets(bind: LayoutServiceItemBinding, item: ServiceCapabilitiesDataItem?, fleetItemList: MutableList<FleetData>?, callback: (MutableList<String>) -> Unit) {
        bind.apply {
            CoroutineScope(Dispatchers.IO).launch {
                val listDeferred = async {
                    val filteredList = fleetItemList?.mapNotNull { it.vendorId }?.toMutableList()
                        ?: mutableListOf()

                    // Sorting the list
                    if (item?.fleets.isValidList()) {
                        item?.fleets?.sortBy { it }
                        filteredList.sortBy { it }
                    }

                    filteredList
                }

                val fleetResponseDeferred = async {
                    val fleetResponseList = mutableListOf<FleetServiceResponse>()

                    fleetItemList?.forEach { fResponse ->
                        if (fResponse.vendorId != null && item?.fleets.isValidList()) {
                            val isInFleets = item?.fleets?.contains(fResponse.vendorId)
                            val fleetServiceResponse = FleetServiceResponse(fResponse, isInFleets?:false)
                            fleetResponseList.add(fleetServiceResponse)
                        } else {
                            val fleetServiceResponse = FleetServiceResponse(fResponse, false)
                            fleetResponseList.add(fleetServiceResponse)
                        }
                    }

                    fleetResponseList
                }

                val list = listDeferred.await()
                val fleetResponse = fleetResponseDeferred.await()

                CoroutineScope(Dispatchers.Main).launch {
                    layoutFleets.cbCheck.isChecked = item?.fleets == list

                    NSUtilities.setFleet(
                        activity, colorResources,
                        layoutFleets, fleetItemList,
                        fleetResponse
                    ) {
                        item?.fleets = it
                        callback.invoke(it)
                    }
                }
            }
        }
    }
}