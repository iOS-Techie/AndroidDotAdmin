package com.nyotek.dot.admin.common

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.CountDownTimer
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.base.BaseViewModel
import com.nyotek.dot.admin.common.callbacks.NSLanguageSelectedCallback
import com.nyotek.dot.admin.common.callbacks.NSLocalJsonCallback
import com.nyotek.dot.admin.common.extension.addOnTextChangedListener
import com.nyotek.dot.admin.common.extension.addOnTextChangedListenerSmall
import com.nyotek.dot.admin.common.extension.buildAlertDialog
import com.nyotek.dot.admin.common.extension.getLngValueWithLanguage
import com.nyotek.dot.admin.common.extension.gone
import com.nyotek.dot.admin.common.extension.setPlaceholderAdapter
import com.nyotek.dot.admin.common.extension.setSafeOnClickListener
import com.nyotek.dot.admin.common.extension.visible
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.databinding.LayoutCreateLocalBinding
import com.nyotek.dot.admin.databinding.LayoutRecycleViewBinding
import com.nyotek.dot.admin.databinding.LayoutRecycleViewFixBinding
import com.nyotek.dot.admin.models.responses.CapabilitiesDataItem
import com.nyotek.dot.admin.models.responses.FleetData
import com.nyotek.dot.admin.models.responses.FleetServiceResponse
import com.nyotek.dot.admin.models.responses.LanguageSelectModel
import com.nyotek.dot.admin.models.responses.NSLanguageStringResponse
import com.nyotek.dot.admin.models.responses.SpinnerData
import com.nyotek.dot.admin.models.responses.StringResourceResponse
import com.nyotek.dot.admin.models.responses.VehicleDataItem
import com.nyotek.dot.admin.ui.tabs.fleets.vehicle.NSCapabilitiesVehicleRecycleAdapter
import com.nyotek.dot.admin.ui.tabs.services.NSFleetServiceRecycleAdapter
import com.nyotek.dot.admin.widgets.NSCommonEditText
import com.nyotek.dot.admin.widgets.NSCommonRecycleView
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.io.StringWriter
import java.io.Writer
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

object NSUtilities {

    fun getStringResource(): StringResourceResponse {
        val resource = StringResourceResponse()
        resource.setMapValue(NSThemeHelper.getStringModel())
        return resource//stringResource.value
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

        edtText.addOnTextChangedListenerSmall(
            onTextChanged = { s, _, _, _ ->
                if (title != null && selectedLanguage != null) {
                    title[selectedLanguage!!] = s.toString()
                }
            }
        )
    }

    fun showCreateLocalDialog(
        activity: Activity,
        colorResources: ColorResources,
        languageList: MutableList<LanguageSelectModel>,
        callback: ((LanguageSelectModel) -> Unit)
    ) {
        buildAlertDialog(
            activity,
            LayoutCreateLocalBinding::inflate
        ) { dialog, binding ->
            binding.apply {
                colorResources.getStringResource().apply {
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
                    activity, colorResources,
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

    fun convertToArabic(value: String): String {
        return (value + "")
            .replace("١".toRegex(), "1").replace("٢".toRegex(), "2")
            .replace("٣".toRegex(), "3").replace("٤".toRegex(), "4")
            .replace("٥".toRegex(), "5").replace("٦".toRegex(), "6")
            .replace("٧".toRegex(), "7").replace("٨".toRegex(), "8")
            .replace("٩".toRegex(), "9").replace("٠".toRegex(), "0")
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


    fun setFleet(
        activity: Activity,
        colorResources: ColorResources,
        layoutFleets: LayoutRecycleViewFixBinding,
        fleetItemList: MutableList<FleetData>?,
        fleetList: MutableList<FleetServiceResponse>,
        callback: ((MutableList<String>, String, Boolean) -> Unit)
    ) {
        val selectedFleets: MutableList<String> = arrayListOf()
        selectedFleets.addAll(fleetList.filter { it.isSelected }.map { it.data?.vendorId!! } as MutableList<String>)
        layoutFleets.rvCommonView.layoutManager = GridLayoutManager(activity, 2)
        val fleetAdapter = NSFleetServiceRecycleAdapter(colorResources) { model, isSelected ->
            if (isSelected) {
                selectedFleets.remove(model.vendorId)
            } else {
                model.vendorId?.let { selectedFleets.add(it) }
            }
            callback.invoke(selectedFleets, model.vendorId?:"", !isSelected)
        }
        layoutFleets.rvCommonView.adapter = fleetAdapter
        fleetAdapter.setData(fleetList)
        layoutFleets.rvCommonView.isNestedScrollingEnabled = false

        layoutFleets.apply {
            clSelectAll.setSafeOnClickListener {
                val finalList: MutableList<FleetServiceResponse> = arrayListOf()
                val list: MutableList<String>
                if (cbCheck.isChecked) {
                    cbCheck.isChecked = false
                    list = arrayListOf()
                    finalList.addAll(fleetList.map { it.copy(isSelected = false) })
                    callback.invoke(list, "", false)
                } else {
                    cbCheck.isChecked = true
                    list = fleetItemList?.map { it.vendorId!! } as MutableList<String>
                    finalList.addAll(fleetList.map { it.copy(isSelected = true) })
                    callback.invoke(list, "", true)
                }
                fleetAdapter.setData(finalList)
            }
        }
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

    fun hideProgressBar(progressBar: ProgressBar?, dialog: Dialog?, colorResources: ColorResources) {
        progressBar?.progressTintList = colorResources.getViewEnableDisableState()
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        progressBar?.gone()
    }

    fun showProgressBar(progressBar: ProgressBar, dialog: Dialog?, colorResources: ColorResources) {
        progressBar.progressTintList = colorResources.getViewEnableDisableState()
        dialog?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        progressBar.visible()
    }

    fun setCapability(
        activity: Activity,
        viewModel: BaseViewModel,
        isSmallLayout: Boolean,isShowActiveDot: Boolean = true,
        layoutCapability: LayoutRecycleViewBinding,
        capabilities: MutableList<CapabilitiesDataItem>,
        dataItem: VehicleDataItem? = null,
        callback: ((MutableList<String>) -> Unit)
    ) {
        val selectedCapabilities: MutableList<String> = arrayListOf()
        selectedCapabilities.addAll(dataItem?.capabilities ?: arrayListOf())
        layoutCapability.rvCommonView.layoutManager = GridLayoutManager(activity, 2)

        val capabilityAdapter = NSCapabilitiesVehicleRecycleAdapter(viewModel, { model, isDelete ->
            if (isDelete) {
                selectedCapabilities.remove(model.id)
            } else {
                model.id?.let { selectedCapabilities.add(it) }
            }
            callback.invoke(selectedCapabilities)
        }, isSmallLayout, isShowActiveDot)

        layoutCapability.rvCommonView.adapter = capabilityAdapter
        capabilityAdapter.updateData(capabilities, dataItem?.capabilities ?: arrayListOf())
    }

    fun callUser(activity: Activity, call: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${call}")
        activity.startActivity(intent)
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
        activity: Activity,colorResources: ColorResources, selectedDate: String = "", callback: (String, String, String, String) -> Unit) {

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
                    headerView?.setBackgroundColor(colorResources.getPrimaryColor())
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
    
    fun getLocalJsonRowDataNew(activity: Activity, rowData: Int, callback: (HashMap<String, HashMap<String, String>>) -> Unit) {
        val jsonString: String = commonJsonResponse(activity, rowData)
        val type: Type = object : TypeToken<HashMap<String, HashMap<String, String>>>() {}.type
        callback.invoke(Gson().fromJson(jsonString, type))
    }

    private fun commonJsonResponse(activity: Activity, rawFile: Int): String {
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
    
    fun showKeyboard(activity: Activity, editText: EditText) {
        editText.requestFocus()
        val mgr = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        @Suppress("DEPRECATION")
        mgr!!.showSoftInput(editText, InputMethodManager.SHOW_FORCED)
    }
    
    fun hideKeyboard(activity: Activity, editText: EditText) {
        val inputManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            editText.windowToken,
            0
        )
    }
}