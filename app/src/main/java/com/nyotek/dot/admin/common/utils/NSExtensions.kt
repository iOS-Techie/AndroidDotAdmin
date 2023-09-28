package com.nyotek.dot.admin.common.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.constraintlayout.widget.Group
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.franmontiel.localechanger.LocaleChanger
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.DelayedClickListener
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSDateTimeHelper
import com.nyotek.dot.admin.common.NSLog
import com.nyotek.dot.admin.common.NSViewPagerAdapter
import com.nyotek.dot.admin.common.SafeClickListener
import com.nyotek.dot.admin.common.SingleClickListener
import com.nyotek.dot.admin.databinding.LayoutSpinnerItemBinding
import com.nyotek.dot.admin.databinding.LayoutSpinnerItemDropDownBinding
import com.nyotek.dot.admin.repository.network.responses.NSGetServiceListData
import com.nyotek.dot.admin.repository.network.responses.SpinnerData
import com.nyotek.dot.admin.repository.network.responses.StringResourceResponse
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

/**
 * This is the file that contains the all the extensions functions.
 */

const val TAG: String = "NSExtensions"

/**
 * Used for switching from one activity to another with additional flags and bundle parameters
 *
 * @param destination destination screen to move the user
 * @param flags       flags needed to be set in the intent
 * @param bundle      additional information to be carried
 * @param <T>         represents the common template, able to get calling class using <T> format
 */
fun <T : Activity> Activity.switchActivity(
    destination: Class<T>, bundle: Bundle? = null, flags: IntArray? = null
) {
    startActivity(getIntent(destination, bundle, flags))
}

/**
 * Used for switching from one activity to another with additional flags and bundle parameters using request code to get results
 *
 * @param destination destination screen to move the user
 * @param flags       flags needed to be set in the intent
 * @param bundle      additional information to be carried
 * @param requestCode which is used to identify result
 * @param <T>         represents the common template, able to get calling class using <T> format
 */
fun <T : Activity> Activity.switchActivityForResult(
    destination: Class<T>, requestCode: Int, bundle: Bundle? = null, flags: IntArray? = null
) {
    startActivityForResult(getIntent(destination, bundle, flags), requestCode)
}

/**
 * To get the intent with the below parameter details attached to the intent
 *
 * @param destination destination screen to move the user
 * @param flags       flags needed to be set in the intent
 * @param bundle      additional information to be carried
 * @param <T>         represents the common template, able to get calling class using <T> format
 * @return the intent containing the parameter details
 */
fun <T : Activity> Activity.getIntent(
    destination: Class<T>, bundle: Bundle?, flags: IntArray?
): Intent {
    return getIntent(this, destination, flags, bundle)
}

/**
 * To dismiss the keyboard
 */
fun Activity.hideKeyboard() {
    try {
        val inputManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // check if no view has focus:
        val currentFocusedView = this.currentFocus
        currentFocusedView?.let {
            inputManager.hideSoftInputFromWindow(
                currentFocusedView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    } catch (exception: Exception) {
        NSLog.e(TAG, "hideKeyboard: ${exception.message}")
    }
}

fun getLocalLanguage(): String {
    return LocaleChanger.getLocale().language
}

// Extension functions for Fragment
/**
 * Used for switching to an activity from the fragment with additional flags and bundle parameters
 *
 * @param destination destination screen to move the user
 * @param flags       flags needed to be set in the intent
 * @param bundle      additional information to be carried
 * @param <T>         represents the common template, able to get calling class using <T> format
 */
fun <T : Activity> Fragment.switchActivity(
    destination: Class<T>, bundle: Bundle? = null, flags: IntArray? = null
) {
    startActivity(getIntent(destination, bundle, flags))
}

/**
 * Used for switching to an activity from the fragment with additional flags and bundle parameters
 *
 * @param destination destination screen to move the user
 * @param flags       flags needed to be set in the intent
 * @param bundle      additional information to be carried
 * @param <T>         represents the common template, able to get calling class using <T> format
 */
fun <T : Activity> Fragment.switchResultActivity(launcher: ActivityResultLauncher<Intent?>,
    destination: Class<T>, bundle: Bundle? = null, flags: IntArray? = null
) {
    launcher.launch(getIntent(destination, bundle, flags))
}

/**
 * To get the intent with the below parameter details attached to the intent
 *
 * @param destination destination screen to move the user
 * @param flags       flags needed to be set in the intent
 * @param bundle      additional information to be carried
 * @param <T>         represents the common template, able to get calling class using <T> format
 * @return the intent containing the parameter details
 */
fun <T : Activity> Fragment.getIntent(
    destination: Class<T>, bundle: Bundle?, flags: IntArray?
): Intent {
    return getIntent(context, destination, flags, bundle)
}

/**
 * To get the intent with the below parameter details attached to the intent
 *
 * @param T represents the common template, able to get calling class using <T> format
 * @param context The context
 * @param destination destination screen to move the user
 * @param flags flags needed to be set in the intent
 * @param bundle additional information to be carried
 * @return
 */
private fun <T : Activity> getIntent(
    context: Context?,
    destination: Class<T>,
    flags: IntArray?,
    bundle: Bundle?
): Intent {
    val launchIntent = Intent(context, destination)
    if (flags != null) {
        for (flag in flags) {
            launchIntent.addFlags(flag)
        }
    }
    if (bundle != null) {
        launchIntent.putExtras(bundle)
    }
    return launchIntent
}

//Extension functions for Textview
/**
 * To get the text of textview as string
 *
 * @return textview data as string
 */
fun TextView.getString(): String = this.text.toString()

fun addMultipleTextCommon(center: String, data: String, sData: String): String {
    return "$data$center$sData"
}

//Extension functions for String class
/**
 * To compare two strings with ignore case
 *
 * @param text the text to compare with this
 * @return boolean comparison result of two strings
 */
fun String?.equalsIgnoreCase(text: String?): Boolean = this.equals(text, true)

//Extension functions for List class
/**
 * To check whether the list is valid or not
 *
 * @return boolean determining the list is valid or not based on the list content and size
 */
fun List<*>?.isValidList(): Boolean {
    return this != null && this.isNotEmpty()
}

/**
 * To set the view visibility as [View.VISIBLE]
 */
fun View.visible() {
    this.visibility = View.VISIBLE
}

/**
 * To set the view visibility as [View.INVISIBLE]
 */
fun View.invisible() {
    this.visibility = View.INVISIBLE
}

/**
 * To set the view visibility as [View.GONE]
 */
fun View.gone() {
    this.visibility = View.GONE
}

/**
 * To set the group visibility as [Group.VISIBLE]
 */
fun Group.visible() {
    this.visibility = Group.VISIBLE
}

/**
 * To set the group visibility as [Group.INVISIBLE]
 */
fun Group.invisible() {
    this.visibility = Group.INVISIBLE
}

/**
 * To set the group visibility as [Group.GONE]
 */
fun Group.gone() {
    this.visibility = Group.GONE
}

fun getRadius(value: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        value,
        Resources.getSystem().displayMetrics)
}

fun getColorWithAlpha(color: Int, ratio: Float): Int {
    val percentage = ratio/100
    return ColorUtils.setAlphaComponent(color, (percentage * 255).toInt())
}

/**
 * To set the [View] visibility based on the input value. It won't set [View.INVISIBLE]
 *
 * @param isVisible Is the view visible or not
 */
fun View.setVisibility(isVisible: Boolean) {
    if (isVisible) {
        this.visible()
    } else {
        this.gone()
    }
}

fun View.setVisibilityIn(isVisible: Boolean) {
    if (isVisible) {
        this.visible()
    } else {
        this.invisible()
    }
}

/**
 * To set the [Group] visibility based on the input value. It won't set [Group.INVISIBLE]
 *
 * @param isVisible Is the group visible or not
 */
fun Group.setVisibility(isVisible: Boolean) {
    if (isVisible) {
        this.visible()
    } else {
        this.gone()
    }
}

/**
 * To round-off the given decimal value
 *
 * @return Rounded off value
 */
fun Double?.roundOff(): String {
    return if (this == null) {
        0.00.toString()
    } else {
        try {
            val decimalFormat = DecimalFormat("#0.00")
            decimalFormat.format(this)
        } catch (e: NumberFormatException) {
            NSLog.e(TAG, "roundOff: Caught exception: " + e.message, e)
            this.toString()
        }
    }
}

/**
 * To concatenate the given lists
 *
 * @param T     The type of list
 * @param lists All input list
 * @return      concatenated single list
 */
fun <T> concatenate(vararg lists: List<T>): List<T> {
    return mutableListOf(*lists).flatten()
}

/**
 * To set the alpha value for all components under single [Group]
 *
 * @param alpha The alpha value
 */
fun Group.setAlphaForAll(alpha: Float) = referencedIds.forEach {
    rootView.findViewById<View>(it).alpha = alpha
}

/**
 * To round off the number with given decimals
 *
 * @param decimals Round off decimal value
 */
fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()

fun TextView.addText(resource: Int, data: String) {
    this.text = this.resources.getString(resource, data)
}

fun TextView.addTextLast(resource: String, data: String, space: String = " ") {
    val str = "$resource$space$data"
    this.text = str
}

fun addMultipleText(activity: Activity, resource: Int,  data: String, sData: String) : String {
    with(activity.resources) {
        return getString(resource, data, sData)
    }
}

fun getDoubleValue(args: Double?) : String {
    val amount = if (args == null) 0.0 else args/100
    return String.format(Locale.ENGLISH, "%.2f", amount)
}

fun getLongValue(args: Long?) : String {
    return DecimalFormat("##.##", DecimalFormatSymbols(Locale.ENGLISH)).format(args)
}

@SuppressLint("NotifyDataSetChanged")
fun notifyAdapter(adapter: RecyclerView.Adapter<*>) {
    adapter.notifyDataSetChanged()
}

/**
 * To set the Image
 */
fun ImageView.setImage(image: Int) {
    this.setImageResource(image)
}

fun String.capitalizeWord(): String {
    val cap = this.replace("_"," ").split(" ").toMutableList()
    var output = ""
    for(word in cap){
        output += word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } +" "
    }
    output = output.trim()
    return output
}

/**
 * RecycleView Linear Layout Manager
 *
 * @param activity The activity's context
 */
fun RecyclerView.linear(activity: Activity) {
    this.layoutManager = LinearLayoutManager(activity)
}

fun RecyclerView.linearHorizontal(activity: Activity) {
    this.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
}

fun TextView.getConvertedDate(date: String?) {
    this.text = NSDateTimeHelper.getDateTimeForView(date)
}

fun TextView.getConvertedDateForUserView(date: String?) {
    this.text = NSDateTimeHelper.getDateTimeForUserView(date)
}

fun ImageView.setCircleImage(resource: Int = 0, url: String? = null) {
    Glide.with(NSApplication.getInstance().applicationContext).load(url?:resource).circleCrop().into(this)
}

fun ImageView.glide(resource: Int = 0, url: String? = null) {
    Glide.with(NSApplication.getInstance().applicationContext).load(url?:resource).into(this)
}

fun ImageView.setGlideWithOutPlace(url: String? = null) {
    Glide.with(NSApplication.getInstance().applicationContext).load(url).into(this)
}

fun ImageView.glideCenter(resource: Int = 0, url: String? = null) {
    Glide.with(NSApplication.getInstance().applicationContext).load(url?:resource).apply(
        RequestOptions().transform(
            CenterCrop()
        )).into(this)
}

fun ImageView.setGlideRound(activity: Activity, url: String?, resource: Int = R.drawable.ic_place_holder_product, scale: String? = "fill", corners: Int = 30) {
    val transform = if (scale == "fill") CenterCrop() else FitCenter()
    Glide.with(activity.applicationContext).load(if (url.isNullOrBlank()) resource else url).placeholder(resource).error(resource).circleCrop().into(this)
}

fun ImageView.glide200(resource: Int = 0, url: String? = null, scale: String?) {
    Glide.with(NSApplication.getInstance().applicationContext).load(url?:resource).apply(
        RequestOptions().transform(
            if (scale.equals(NSConstants.FILL)) CenterCrop() else FitCenter(),
            RoundedCorners(20)
        ).override(200, 200)
    ).placeholder(R.drawable.ic_place_holder_img)
        .error(R.drawable.ic_place_holder_img).into(this)
}

fun getLngValue(hashMap: Map<String, String>?): String {
    if (hashMap != null) {
        val languageCode = getLocalLanguage().lowercase()

        return when {
            hashMap[languageCode] != null -> {
                hashMap[languageCode]!!
            }
            hashMap["en-us"] != null -> {
                hashMap["en-us"]!!
            }
            hashMap["en-US"] != null -> {
                hashMap["en-US"]!!
            }
            hashMap["en"] != null -> {
                hashMap["en"]!!
            }
            else -> {
                ""
            }
        }
    } else {
        return ""
    }
}

fun getLngValueWithLanguage(hashMap: Map<String, String>?, languageCodeValue: String): String {
    if (hashMap != null) {
        val languageCode = languageCodeValue.lowercase()

        return when {
            hashMap[languageCode] != null -> {
                hashMap[languageCode]!!
            }
            else -> {
                ""
            }
        }
    } else {
        return ""
    }
}

fun getLanguageCode(language: String): String {
    val languages = getLanguageSplit(language)
    return if (languages.isValidList()) {
        languages[0]
    } else {
        "en"
    }
}

fun getLanguageRegion(language: String): String {
    val languages = getLanguageSplit(language)
    return if (languages.size > 1) {
        languages[1]
    } else {
        ""
    }
}

fun getLanguageSplit(language: String): List<String> {
    return language.split("-")
}


fun TextView.addTextCol(resource: String, data: String) {
    val str = "$resource: $data"
    this.text = str
}

fun <T : ViewBinding> buildAlertDialog(
    context: Context,
    viewBindingInflater: (LayoutInflater) -> T,
    dialogSetup: (AlertDialog, T) -> Unit
) {
    val inflater = LayoutInflater.from(context)
    val binding = viewBindingInflater.invoke(inflater)

    val builder = AlertDialog.Builder(context)
    builder.setView(binding.root)
    builder.setCancelable(false)
    val dialog = builder.create()
    dialogSetup.invoke(dialog, binding)
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    if (!dialog.isShowing) {
        dialog.show()
    }
}

fun ImageView.switchEnableDisable(isEnable: Boolean) {
    this.setImageResource(if (isEnable) R.drawable.ic_switch_on else R.drawable.ic_switch_off)
}

fun TextView.status(isActive: Boolean) {
    val stringResource = StringResourceResponse()
    stringResource.apply {
        this@status.text = if (isActive) active else inActive
    }
}

fun TextView.getMapValue(label: HashMap<String, String>?) {
    this.text = getLngValue(label)
}

fun View.setSafeOnClickListener(onClickAction: () -> Unit) {
    setOnClickListener(SafeClickListener {
        onClickAction.invoke()
    })
}

fun View.setSingleClickListener(onClickAction: () -> Unit) {
    setOnClickListener(SingleClickListener {
        onClickAction.invoke()
    })
}

fun View.setDelayedOnClickListener(delay: Long, onClickAction: () -> Unit) {
    setOnClickListener(DelayedClickListener(delay) {
        onClickAction.invoke()
    })
}

fun RecyclerView.setupWithAdapter(adapter: RecyclerView.Adapter<*>) {
    this.adapter = adapter
    layoutManager = LinearLayoutManager(context)
}

fun RecyclerView.setupWithAdapterAndCustomLayoutManager(adapter: RecyclerView.Adapter<*>, layoutManager: RecyclerView.LayoutManager) {
    this.adapter = adapter
    this.layoutManager = layoutManager
}

fun EditText.addOnTextChangedListener(
    beforeTextChanged: ((CharSequence?, Int, Int, Int) -> Unit)? = null,
    onTextChanged: ((CharSequence?, Int, Int, Int) -> Unit)? = null,
    afterTextChanged: ((Editable?) -> Unit)? = null
) {
    val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            beforeTextChanged?.invoke(s, start, count, after)
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged?.invoke(s, start, before, count)
        }

        override fun afterTextChanged(s: Editable?) {
            afterTextChanged?.invoke(s)
        }
    }

    addTextChangedListener(textWatcher)
}

fun View.setAlphaP6(isActive: Boolean) {
    alpha = if (isActive) 1f else 0.6f
}

fun ViewPager2.setPager(
    activity: FragmentActivity,
    list: MutableList<Fragment>,
    callback: ((Int) -> Unit)? = null
) {
    val pager = NSViewPagerAdapter(activity)
    pager.setFragment(list)
    adapter = pager
    isUserInputEnabled = false
    offscreenPageLimit = list.size
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            callback?.invoke(position)
        }
    })
}

fun Spinner.setPlaceholderAdapter(
    items: SpinnerData,
    context: Context,
    selectedId: String?,
    isHideFirstPosition: Boolean,
    placeholderName: String? = null,
    onItemSelectedListener: ((String?) -> Unit)?
) {
    if (isHideFirstPosition) {
        items.title.add(0, placeholderName?:"")
        items.id.add(0,"")
    }
    // Create a custom adapter with the items
    val adapter = object : ArrayAdapter<String>(context, R.layout.layout_spinner_item, android.R.id.text1, items.title) {
        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getDropDownView(position, convertView, parent)
            if (isHideFirstPosition) {
                val bind = LayoutSpinnerItemDropDownBinding.bind(view)
                if (position == 0) {
                    bind.text1.visibility = View.GONE
                } else {
                    bind.text1.visibility = View.VISIBLE
                }
            }
            return view
        }

        override fun getView(
            position: Int,
            convertView: View?,
            parent: ViewGroup
        ): View {
            val view = super.getView(position, convertView, parent)
            val bind = LayoutSpinnerItemBinding.bind(view)
            if (isHideFirstPosition && position == 0) {
                bind.text1.setTextColor(ColorResources.getPrimaryLightColor())
            } else {
                bind.text1.setTextColor(ColorResources.getPrimaryColor())
            }
            return view
        }
    }

    // Set the drop-down layout style
    adapter.setDropDownViewResource(R.layout.layout_spinner_item_drop_down)

    // Set the adapter to the Spinner
    this.adapter = adapter

    // Set a default selection to the first item (placeholder item) if needed
    if (!isHideFirstPosition && items.title.isNotEmpty()) {
        this.setSelection(1, false)
    } else {
        this.setSelection(0, false)
    }

    // Set the OnItemSelectedListener to handle item selection
    this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val selectedItem = if (position >= 0) items.id[position] else null
            onItemSelectedListener?.invoke(selectedItem)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            onItemSelectedListener?.invoke(null)
        }
    }

    val spinnerPosition = items.id.indexOf(selectedId)
    if (spinnerPosition != -1) {
        setSelection(spinnerPosition)
    }
}