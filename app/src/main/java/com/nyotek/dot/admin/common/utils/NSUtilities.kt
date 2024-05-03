package com.nyotek.dot.admin.common.utils

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.net.Uri
import android.os.CountDownTimer
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSDateTimeHelper
import com.nyotek.dot.admin.common.callbacks.NSLanguageSelectedCallback
import com.nyotek.dot.admin.common.callbacks.NSLocalJsonCallback
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
import com.nyotek.dot.admin.repository.network.responses.NSLanguageStringResponse
import com.nyotek.dot.admin.repository.network.responses.NSLocalLanguageResponse
import com.nyotek.dot.admin.repository.network.responses.SpinnerData
import com.nyotek.dot.admin.repository.network.responses.StringResourceResponse
import com.nyotek.dot.admin.repository.network.responses.VehicleDataItem
import com.nyotek.dot.admin.ui.fleets.vehicle.NSCapabilitiesVehicleRecycleAdapter
import com.nyotek.dot.admin.ui.serviceManagement.NSFleetServiceRecycleAdapter
import com.nyotek.dot.admin.ui.serviceManagement.fleetItemList
import com.nyotek.dot.admin.widgets.NSCommonEditText
import com.nyotek.dot.admin.widgets.NSCommonRecycleView
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.io.StringWriter
import java.io.Writer
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.UUID


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

    fun getDeviceId(): String {
        return getBundleId() + "_"+ getUUIDDeviceId()
    }

    fun getBundleId(): String {
        return NSApplication.getInstance().packageName
    }

    fun generateUUIDDeviceId() {
        val sharedPreferences: SharedPreferences = NSApplication.getInstance().applicationContext.getSharedPreferences(NSConstants.DEVICE_ID_STORE,
            Context.MODE_PRIVATE
        )
        val uniId = UUID.randomUUID().toString()
        if (sharedPreferences.getString(NSConstants.DEVICE_ID_STORE_VALUE, "").isNullOrEmpty()) {
            val myEdit = sharedPreferences.edit()
            myEdit.putString(NSConstants.DEVICE_ID_STORE_VALUE, uniId)
            myEdit.apply()
        }
    }

    private fun getUUIDDeviceId(): String {
        val sharedPreferences: SharedPreferences = NSApplication.getInstance().applicationContext.getSharedPreferences(
            NSConstants.DEVICE_ID_STORE,
            Context.MODE_PRIVATE
        )
        val uniId = UUID.randomUUID().toString()
        return if (sharedPreferences.getString(NSConstants.DEVICE_ID_STORE_VALUE, "").isNullOrEmpty()) {
            val myEdit = sharedPreferences.edit()
            myEdit.putString(NSConstants.DEVICE_ID_STORE_VALUE, UUID.randomUUID().toString())
            myEdit.apply()
            sharedPreferences.getString(NSConstants.DEVICE_ID_STORE_VALUE, uniId)?:uniId
        } else {
            sharedPreferences.getString(NSConstants.DEVICE_ID_STORE_VALUE, uniId)?:uniId
        }
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
        title: HashMap<String, String>?
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
                if (title != null && selectedLanguage != null) {
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

    fun capitalizeFirstLetter(input: String): String {
        val words = input.split(" ")
        val capitalizedWords = words.map { word ->
            if (word.isNotEmpty()) {
                word.substring(0, 1).uppercase() + word.substring(1).lowercase()
            } else {
                ""
            }
        }
        return capitalizedWords.joinToString(" ")
    }

    suspend fun capitalizeFirstLetterN(input: String): String = coroutineScope {
        // Start a coroutine to perform the operation asynchronously
        async {
            val words = input.split(" ")
            val capitalizedWords = words.map { word ->
                if (word.isNotEmpty()) {
                    word.substring(0, 1).uppercase() + word.substring(1).lowercase()
                } else {
                    ""
                }
            }
            capitalizedWords.joinToString(" ")
        }.await() // Wait for the result of the coroutine
    }

    fun convertToArabic(value: String): String {
        return (value + "")
            .replace("١".toRegex(), "1").replace("٢".toRegex(), "2")
            .replace("٣".toRegex(), "3").replace("٤".toRegex(), "4")
            .replace("٥".toRegex(), "5").replace("٦".toRegex(), "6")
            .replace("٧".toRegex(), "7").replace("٨".toRegex(), "8")
            .replace("٩".toRegex(), "9").replace("٠".toRegex(), "0")
    }

    private var isDatePicker: Boolean = false
    private val countDownTimer = object : CountDownTimer(3000, 1000) {
        override fun onTick(millisUntilFinished: Long) {

        }

        override fun onFinish() {
            isDatePicker = false
        }
    }

    fun openDatePicker(
        activity: Activity, selectedDate: String = "", callback: (String, String, String, String) -> Unit) {

        val dateList = if (selectedDate.isNotEmpty())  selectedDate.split("-") else NSDateTimeHelper.getCurrentDate().split("-")

        val dpd = DatePickerDialog(
            activity,
            R.style.DialogTheme,
            { _, year, monthOfYear, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, monthOfYear, dayOfMonth)

                val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                dateFormatter.timeZone = TimeZone.getTimeZone("UTC")
                val formattedDate = dateFormatter.format(selectedCalendar.time)

                val dayMonth = getDateZero(dayOfMonth)
                val monthYear = getDateZero(monthOfYear + 1)

                callback(formattedDate, dayMonth, monthYear, year.toString())
            },
            dateList[0].toInt(),
            dateList[1].toInt() - 1,
            dateList[2].toInt()
        )

        dpd.datePicker.minDate = getMillisFromYearMonthDay(1900, Calendar.JANUARY, 1)
        dpd.datePicker.maxDate = getMillisFromYearMonthDay(2100, Calendar.DECEMBER, 31)

        try {
            dpd.setOnShowListener {
                if (Resources.getSystem() != null) {
                    val headerView = dpd.findViewById<View>(
                        Resources.getSystem().getIdentifier("date_picker_header", "id", "android")
                    )
                    headerView?.setBackgroundColor(ColorResources.getPrimaryColor())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (!dpd.isShowing) {
            if (!isDatePicker) {
                isDatePicker = true
                countDownTimer.start()
                dpd.show()
            }
        }
    }

    private fun getMillisFromYearMonthDay(year: Int, month: Int, day: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        return calendar.timeInMillis
    }

    fun getDateZero(value: Int?): String {
        return if ((value ?: 0) < 10) {
            if (value == 0) {
                "01"
            } else {
                "0$value"
            }
        } else {
            value.toString()
        }
    }

    fun getLocalJsonRowData(activity: Activity, rowData: Int, callback: NSLocalJsonCallback) {
        val jsonString: String = commonJsonResponse(activity, rowData)
        callback.onLocal(Gson().fromJson(jsonString, NSLanguageStringResponse::class.java))
    }

    fun commonJsonResponse(activity: Activity, rawFile: Int): String {
        val inputStream: InputStream = activity.resources.openRawResource(rawFile)
        val writer: Writer = StringWriter()
        val buffer = CharArray(1024)
        inputStream.use { stream ->
            val reader: Reader = BufferedReader(InputStreamReader(stream, "UTF-8"))
            var n: Int
            while (reader.read(buffer).also { n = it } != -1) {
                writer.write(buffer, 0, n)
            }
        }

        return writer.toString()
    }
}