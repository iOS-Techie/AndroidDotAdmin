package com.nyotek.dot.admin.common.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.GridLayoutManager
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.callbacks.NSLanguageSelectedCallback
import com.nyotek.dot.admin.databinding.LayoutCreateLocalBinding
import com.nyotek.dot.admin.databinding.LayoutRecycleViewBinding
import com.nyotek.dot.admin.databinding.LayoutRecycleViewFixBinding
import com.nyotek.dot.admin.databinding.LayoutSelectAddressBinding
import com.nyotek.dot.admin.repository.NSLanguageRepository
import com.nyotek.dot.admin.repository.network.callbacks.NSGenericViewModelCallback
import com.nyotek.dot.admin.repository.network.responses.CapabilitiesDataItem
import com.nyotek.dot.admin.repository.network.responses.FleetData
import com.nyotek.dot.admin.repository.network.responses.FleetServiceResponse
import com.nyotek.dot.admin.repository.network.responses.LanguageSelectModel
import com.nyotek.dot.admin.repository.network.responses.NSLocalLanguageResponse
import com.nyotek.dot.admin.repository.network.responses.SpinnerData
import com.nyotek.dot.admin.repository.network.responses.StringResourceResponse
import com.nyotek.dot.admin.repository.network.responses.VehicleDataItem
import com.nyotek.dot.admin.ui.fleets.vehicle.NSCapabilitiesVehicleRecycleAdapter
import com.nyotek.dot.admin.ui.serviceManagement.NSFleetServiceRecycleAdapter
import com.nyotek.dot.admin.ui.serviceManagement.fleetItemList
import com.nyotek.dot.admin.widgets.NSCommonEditText
import com.nyotek.dot.admin.widgets.NSCommonRecycleView


/**
 * The utility class that handles tasks that are common throughout the application
 */
object NSUtilities {

    /**
     * To parse api error list and get string message from resource id
     *
     * @param apiErrorList error list that received from api
     */
    fun parseApiErrorList(context: Context, apiErrorList: List<Any>): String {
        var errorMessage = ""
        for (apiError in apiErrorList) {
            if (apiError is Int) {
                errorMessage += """${context.getString(apiError)} """
            } else if (apiError is String) {
                errorMessage += "$apiError "
            }
        }
        return errorMessage.trim()
    }

    fun getBundleId(): String {
        return NSApplication.getInstance().packageName
    }

    fun callUser(activity: Activity, call: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${call}")
        activity.startActivity(intent)
    }

    fun switchEnableDisable(imageView: ImageView, isEnable: Boolean) {
        imageView.setImageResource(if (isEnable) R.drawable.ic_switch_on else R.drawable.ic_switch_off)
    }

    fun setLanguageText(
        edtText: NSCommonEditText,
        recycleView: NSCommonRecycleView,
        title: HashMap<String, String>
    ) {
        var selectedLanguage: String? = null
        recycleView.notifyAdapter()
        recycleView.languageSelectCallback = object : NSLanguageSelectedCallback {
            override fun onItemSelect(language: String, isNotify: Boolean) {
                selectedLanguage = language
                edtText.setText(getLngValueWithLanguage(title, language))
                edtText.setSelection(edtText.length())
            }
        }

        edtText.addOnTextChangedListener(
            onTextChanged = { s, _, _, _ ->
                if (selectedLanguage != null) {
                    title[selectedLanguage!!] = s.toString()
                }
            }
        )
    }

    fun setLanguageText(edtText: NSCommonEditText, fleetData: FleetData?, isUrl: Boolean) {
        edtText.addOnTextChangedListener(
            onTextChanged = { s, _, _, _ ->
                if (isUrl) {
                    fleetData?.url = s.toString()
                }
            }
        )
    }

    fun setupSelectAddressView(bind: LayoutSelectAddressBinding) {
        val stringResource = StringResourceResponse()
        stringResource.apply {
            bind.tvAddressTitle.text = address
            bind.tilAddress.hint = address
            bind.tilCity.hint = cityTitle
            bind.tilPostalCode.hint = postalCode
            bind.tilState.hint = state
            bind.tilCountry.hint = countryTitle
            bind.btnSaveAddress.text = save
            bind.tvAddressDone.text = done
            bind.tvStandard.text = standardTitle
            bind.tvSatellite.text = satelliteTitle
            bind.tvHybrid.text = hybridTitle
        }

        bind.ivAdd.imageTintList = ColorResources.getViewEnableDisableState()
        ColorResources.setCardBackground(
            bind.rlAddressForm,
            5f,
            1,
            ColorResources.getWhiteColor(),
            ColorResources.getGrayColor()
        )
        ColorResources.setCardBackground(
            bind.rlCityForm,
            5f,
            1,
            ColorResources.getWhiteColor(),
            ColorResources.getGrayColor()
        )
        ColorResources.setCardBackground(
            bind.rlCountryForm,
            5f,
            1,
            ColorResources.getWhiteColor(),
            ColorResources.getGrayColor()
        )
        ColorResources.setCardBackground(
            bind.rlPostalCodeForm,
            5f,
            1,
            ColorResources.getWhiteColor(),
            ColorResources.getGrayColor()
        )
        ColorResources.setCardBackground(
            bind.rlStateForm,
            5f,
            1,
            ColorResources.getWhiteColor(),
            ColorResources.getGrayColor()
        )
        ColorResources.setBackground(bind.clHeader, ColorResources.getSecondaryColor())
        ColorResources.setBackground(bind.viewLine, ColorResources.getGrayColor())
        ColorResources.setBackground(bind.viewLine2, ColorResources.getGrayColor())
        ColorResources.setCardBackground(bind.clTopMapView, 8f, 2)
        ColorResources.setCardBackground(bind.rlSegmentBg, 8f, 0, ColorResources.getGrayColor())
    }

    fun setMapButtonUI(position: Int, bind: LayoutSelectAddressBinding) {
        bind.apply {
            tvStandard.setBackgroundResource(0)
            tvSatellite.setBackgroundResource(0)
            tvHybrid.setBackgroundResource(0)
            viewLine.setVisibility(position == 2)
            viewLine2.setVisibility(position == 0)

            ColorResources.setWhiteBackgroundRadius5(if (position == 0) tvStandard else if (position == 1) tvSatellite else tvHybrid)
        }
    }

    fun showCreateLocalDialog(
        activity: Activity,
        languageList: MutableList<LanguageSelectModel>,
        callback: ((LanguageSelectModel) -> Unit)
    ) {

        buildAlertDialog(
            activity,
            LayoutCreateLocalBinding::inflate
        ) { dialog, binding ->
            binding.apply {
                StringResourceResponse().apply {
                    tvBranchTitle.text = createLocal
                    layoutName.tvCommonTitle.text = local
                    layoutFromCheckout.tvCommonTitle.text = fromCheckout
                    tvSave.text = create
                    tvCancel.text = cancel
                }

                val list = languageList.map { it.label ?: "" }
                    .filterNot { it == "+" } as MutableList<String>
                val spinnerList = SpinnerData(list, list)
                layoutFromCheckout.spinnerAppSelect.setPlaceholderAdapter(
                    spinnerList,
                    activity,
                    "",
                    isHideFirstPosition = false
                ) { selectedId ->
                    val spinnerPosition = languageList.map { it.label }.indexOf(selectedId)
                    callback.invoke(languageList[spinnerPosition])
                }

                tvCancel.setOnClickListener {
                    layoutName.edtValue.setText("")
                    dialog.dismiss()
                }

                tvSave.setOnClickListener {
                    layoutName.edtValue.setText("")
                    dialog.dismiss()
                }
            }
        }
    }

    fun localLanguageApiCall(serviceId: String = "", callback: ((NSLocalLanguageResponse) -> Unit)) {
        NSLanguageRepository.localLanguages(serviceId, object : NSGenericViewModelCallback {
            override fun <T> onSuccess(data: T) {
                if (data is NSLocalLanguageResponse) {
                    if (serviceId.isNotEmpty()) {
                        val map: HashMap<String, MutableList<LanguageSelectModel>> = hashMapOf()
                        map[serviceId] = data.data
                        NSApplication.getInstance().setMapLocalLanguage(map)
                    }
                    callback.invoke(data)
                }
            }

            override fun onError(errors: List<Any>) {

            }

            override fun onFailure(failureMessage: String?) {

            }

            override fun <T> onNoNetwork(localData: T) {

            }
        })
    }

    fun showProgressBar(progressBar: ProgressBar, dialog: Dialog?) {
        progressBar.progressTintList = ColorResources.getViewEnableDisableState()
        dialog?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        progressBar.visible()
    }

    fun hideProgressBar(progressBar: ProgressBar?, dialog: Dialog?) {
        progressBar?.progressTintList = ColorResources.getViewEnableDisableState()
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        progressBar?.gone()
    }

    fun setCapability(
        activity: Activity,
        isSmallLayout: Boolean,
        layoutCapability: LayoutRecycleViewBinding,
        capabilities: MutableList<CapabilitiesDataItem>,
        dataItem: VehicleDataItem? = null,
        callback: ((MutableList<String>) -> Unit)
    ) {
        val selectedCapabilities: MutableList<String> = arrayListOf()
        selectedCapabilities.addAll(dataItem?.capabilities ?: arrayListOf())
        layoutCapability.rvCommonView.layoutManager = GridLayoutManager(activity, 2)

        val capabilityAdapter = NSCapabilitiesVehicleRecycleAdapter({ model, isDelete ->
            if (isDelete) {
                selectedCapabilities.remove(model.id)
            } else {
                model.id?.let { selectedCapabilities.add(it) }
            }
            callback.invoke(selectedCapabilities)
        }, isSmallLayout)

        layoutCapability.rvCommonView.adapter = capabilityAdapter
        capabilityAdapter.updateData(capabilities, dataItem?.capabilities ?: arrayListOf())
    }

    fun setFleet(
        activity: Activity,
        layoutFleets: LayoutRecycleViewFixBinding,
        fleetList: MutableList<FleetServiceResponse>,
        callback: ((MutableList<String>) -> Unit)
    ) {
        val selectedFleets: MutableList<String> = arrayListOf()
        selectedFleets.addAll(fleetList.filter { it.isSelected }.map { it.data?.vendorId!! } as MutableList<String>)
        layoutFleets.rvCommonView.layoutManager = GridLayoutManager(activity, 2)
        val fleetAdapter = NSFleetServiceRecycleAdapter { model, isSelected ->
            if (isSelected) {
                selectedFleets.remove(model.vendorId)
            } else {
                model.vendorId?.let { selectedFleets.add(it) }
            }
            callback.invoke(selectedFleets)
        }
        layoutFleets.rvCommonView.adapter = fleetAdapter
        //fleetAdapter.setSubList(dataItem?.fleets?: arrayListOf())
        fleetAdapter.setData(fleetList)
        layoutFleets.rvCommonView.isNestedScrollingEnabled = false

        layoutFleets.apply {
            clSelectAll.setSafeOnClickListener {
                val list: MutableList<String>
                if (cbCheck.isChecked) {
                    cbCheck.isChecked = false
                    list = arrayListOf()
                    callback.invoke(list)
                } else {
                    cbCheck.isChecked = true
                    list = fleetItemList.map { it.vendorId!! } as MutableList<String>
                    callback.invoke(list)
                }
                fleetAdapter.setSubList(list)
                fleetAdapter.setData(fleetList)
            }
        }
    }

    fun capitalizeFirstLetter(sentence: String): String {
        if (sentence.isEmpty()) {
            return sentence
        }

        val firstChar = sentence[0]
        val capitalizedFirstChar = firstChar.uppercaseChar()
        val lowercaseRest = sentence.substring(1).lowercase()

        return "$capitalizedFirstChar$lowercaseRest"
    }
}