package com.nyotek.dot.admin.common.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import androidx.recyclerview.widget.GridLayoutManager
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.callbacks.NSCapabilitiesCallback
import com.nyotek.dot.admin.common.callbacks.NSCapabilityListCallback
import com.nyotek.dot.admin.common.callbacks.NSFleetServiceCallback
import com.nyotek.dot.admin.common.callbacks.NSItemSelectCallback
import com.nyotek.dot.admin.common.callbacks.NSLanguageSelectedCallback
import com.nyotek.dot.admin.common.callbacks.NSLanguageSubItemSelectCallback
import com.nyotek.dot.admin.common.callbacks.NSLocalLanguageCallback
import com.nyotek.dot.admin.databinding.LayoutCreateLocalBinding
import com.nyotek.dot.admin.databinding.LayoutRecycleViewBinding
import com.nyotek.dot.admin.databinding.LayoutRecycleViewFixBinding
import com.nyotek.dot.admin.databinding.LayoutSelectAddressBinding
import com.nyotek.dot.admin.databinding.LayoutSpinnerItemBinding
import com.nyotek.dot.admin.databinding.LayoutSpinnerItemDropDownBinding
import com.nyotek.dot.admin.repository.NSLanguageRepository
import com.nyotek.dot.admin.repository.network.callbacks.NSGenericViewModelCallback
import com.nyotek.dot.admin.repository.network.responses.CapabilitiesDataItem
import com.nyotek.dot.admin.repository.network.responses.FleetData
import com.nyotek.dot.admin.repository.network.responses.FleetServiceResponse
import com.nyotek.dot.admin.repository.network.responses.LanguageSelectModel
import com.nyotek.dot.admin.repository.network.responses.NSLocalLanguageResponse
import com.nyotek.dot.admin.repository.network.responses.ServiceCapabilitiesDataItem
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

    fun clearFilter(map: HashMap<String, Boolean>) {
        for (entry in map.entries) {
            entry.setValue(false)
        }
        NSApplication.getInstance().setFilterOrderType(map)
    }

    fun switchEnableDisable(imageView: ImageView, isEnable: Boolean) {
        imageView.setImageResource(if (isEnable) R.drawable.ic_switch_on else R.drawable.ic_switch_off)
    }

    fun setSpinner(activity: Activity, spinner: Spinner, arrayList1: MutableList<String>, arrayList2: MutableList<String>, itemSelectCallback: NSItemSelectCallback, isHide: Boolean = true) {
        val adapter: ArrayAdapter<String> =
            object : ArrayAdapter<String>(activity, R.layout.layout_spinner_item, android.R.id.text1, arrayList1.ifEmpty { arrayListOf() }) {
                override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {
                    val view = super.getDropDownView(position, convertView, parent)
                    if (isHide) {
                        val bind = LayoutSpinnerItemDropDownBinding.bind(view)
                        if (position == 0) {
                            bind.text1.visibility = View.GONE
                        } else {
                            bind.text1.visibility = View.VISIBLE
                        }
                    }
                    return view
                }

                override fun getCount(): Int {
                    return arrayList1.ifEmpty { arrayListOf() }.size
                }

                override fun getView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {
                    val view = super.getView(position, convertView, parent)
                    val bind = LayoutSpinnerItemBinding.bind(view)
                    if (isHide && position == 0) {
                        bind.text1.setTextColor(ColorResources.getPrimaryLightColor())
                    } else {
                        bind.text1.setTextColor(ColorResources.getPrimaryColor())
                    }
                    return view
                }
            }
        adapter.setDropDownViewResource(R.layout.layout_spinner_item_drop_down)
        spinner.adapter = adapter

        val themeCallback = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                itemSelectCallback.onItemSelect(arrayList2[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
        spinner.onItemSelectedListener = themeCallback
    }

    fun setLanguageText(edtText: NSCommonEditText, recycleView: NSCommonRecycleView, title: HashMap<String, String>) {
        var selectedLanguage: String? = null
        recycleView.notifyAdapter()
        recycleView.languageSelectCallback = object : NSLanguageSelectedCallback {
            override fun onItemSelect(language: String) {
                selectedLanguage = language
                edtText.setText(getLngValueWithLanguage(title, language))
                edtText.setSelection(edtText.length())
            }
        }

        edtText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (selectedLanguage != null) {
                    title[selectedLanguage!!] = s.toString()
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    fun setLanguageText(edtText: NSCommonEditText, fleetData: FleetData?, isUrl: Boolean) {

        edtText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUrl) {
                    fleetData?.url = s.toString()
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    fun setupSelectAddressView(bind: LayoutSelectAddressBinding) {
        val stringResource = NSApplication.getInstance().getStringModel()
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

    fun showCreateLocalDialog(activity: Activity, languageList: MutableList<LanguageSelectModel>, callback: NSLanguageSubItemSelectCallback) {

        val builder = AlertDialog.Builder(activity)
        val view: View =
            activity.layoutInflater.inflate(R.layout.layout_create_local, null)
        builder.setView(view)
        builder.setCancelable(false)
        val layoutCreateLocal = LayoutCreateLocalBinding.bind(view)
        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        layoutCreateLocal.apply {
            NSApplication.getInstance().getStringModel().apply {
                tvBranchTitle.text = createLocal
                layoutName.tvCommonTitle.text = local
                layoutFromCheckout.tvCommonTitle.text = fromCheckout
                tvSave.text = create
                tvCancel.text = cancel
            }

            val languageLocalList: MutableList<String> = arrayListOf()
            for (language in languageList) {
                if (!language.locale.equals("+")) {
                    language.locale?.let { languageLocalList.add(it) }
                }
            }

            val adapter =
                activity.let {
                    ArrayAdapter(
                        it,
                        R.layout.layout_spinner_language_dialog_item,
                        android.R.id.text1,
                        languageLocalList
                    )
                }

            adapter.setDropDownViewResource(R.layout.layout_spinner_item_drop_down)
            layoutFromCheckout.spinnerAppSelect.adapter = adapter

            layoutFromCheckout.spinnerAppSelect.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        callback.onItemSelect(languageList[position])
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                }

            tvCancel.setOnClickListener {
                layoutName.edtValue.setText("")
                dialog.dismiss()
            }

            tvSave.setOnClickListener {
                layoutName.edtValue.setText("")
                dialog.dismiss()
            }

            if (!dialog.isShowing) {
                dialog.show()
            }
        }
    }

    fun localLanguageApiCall(serviceId: String = "", callback: NSLocalLanguageCallback) {
        NSLanguageRepository.localLanguages(serviceId, object : NSGenericViewModelCallback {
            override fun <T> onSuccess(data: T) {
                if (data is NSLocalLanguageResponse) {
                    if (serviceId.isNotEmpty()) {
                        val map: HashMap<String, MutableList<LanguageSelectModel>> = hashMapOf()
                        map[serviceId] = data.data
                        NSApplication.getInstance().setMapLocalLanguage(map)
                    }
                    callback.onItemSelect(data)
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
        dialog?.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        progressBar.visible()
    }

    fun hideProgressBar(progressBar: ProgressBar?, dialog: Dialog?) {
        progressBar?.progressTintList = ColorResources.getViewEnableDisableState()
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        progressBar?.gone()
    }

    fun setCapability(activity: Activity, isSmallLayout: Boolean, layoutCapability: LayoutRecycleViewBinding, capabilities: MutableList<CapabilitiesDataItem>, dataItem: VehicleDataItem? = null, callback: NSCapabilityListCallback) {
        val selectedCapabilities: MutableList<String> = arrayListOf()
        layoutCapability.rvCommonView.layoutManager = GridLayoutManager(activity, 2)
        val capabilityAdapter = NSCapabilitiesVehicleRecycleAdapter(object : NSCapabilitiesCallback {
            override fun onItemSelect(
                model: CapabilitiesDataItem,
                isDelete: Boolean
            ) {
                if (isDelete) {
                    selectedCapabilities.remove(model.id)
                } else {
                    model.id?.let { selectedCapabilities.add(it) }
                }
                callback.onCapability(selectedCapabilities)
            }

        }, isSmallLayout)
        layoutCapability.rvCommonView.adapter = capabilityAdapter
        capabilityAdapter.updateData(capabilities, dataItem?.capabilities?: arrayListOf())
    }

    fun setFleet(activity: Activity, layoutFleets: LayoutRecycleViewFixBinding, fleetList: MutableList<FleetServiceResponse>, callback: NSCapabilityListCallback) {
        val selectedCapabilities: MutableList<String> = arrayListOf()
        layoutFleets.rvCommonView.layoutManager = GridLayoutManager(activity, 2)
        val fleetAdapter = NSFleetServiceRecycleAdapter(object : NSFleetServiceCallback {
            override fun onItemSelect(
                model: FleetData,
                isSelected: Boolean
            ) {
                if (isSelected) {
                    selectedCapabilities.remove(model.vendorId)
                } else {
                    model.vendorId?.let { selectedCapabilities.add(it) }
                }
                callback.onCapability(selectedCapabilities)
            }

        })
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
                    callback.onCapability(list)
                } else {
                    cbCheck.isChecked = true
                    list = fleetItemList.map { it.vendorId!! } as MutableList<String>
                    callback.onCapability(list)
                }
                fleetAdapter.setSubList(list)
                fleetAdapter.setData(fleetList)
            }
        }
    }
}