package com.nyotek.dot.admin.common.extension

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
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
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.constraintlayout.widget.Group
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.widget.ViewPager2
import coil.load
import coil.size.Scale
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.franmontiel.localechanger.LocaleChanger
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.DelayedClickListener
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSLog
import com.nyotek.dot.admin.common.NSThemeHelper
import com.nyotek.dot.admin.common.NSUtilities
import com.nyotek.dot.admin.common.NSViewPagerAdapter
import com.nyotek.dot.admin.common.SafeClickListener
import com.nyotek.dot.admin.common.SingleClickListener
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.databinding.LayoutSpinnerItemBinding
import com.nyotek.dot.admin.databinding.LayoutSpinnerItemDropDownBinding
import com.nyotek.dot.admin.models.responses.LanguageSelectModel
import com.nyotek.dot.admin.models.responses.SpinnerData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.Serializable
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * This is the file that contains the all the extensions functions.
 */

const val TAG: String = "NSExtensions"

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(value: T) {
            removeObserver(this)
            observer.onChanged(value)
        }
    })
}

inline fun <reified T : Parcelable> Bundle.retrieveParcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}

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

//fun TextView.getConvertedDate(date: String?) {
//    this.text = NSDateTimeHelper.getDateTimeForView(date)
//}
//
//fun TextView.getConvertedDateForUserView(date: String?) {
//    this.text = NSDateTimeHelper.getDateTimeForUserView(date)
//}

fun ImageView.setCircleImage(resource: Int = 0, url: String? = null) {
    Glide.with(NSApplication.getInstance().applicationContext).load(url?:resource).circleCrop().into(this)
}

fun ImageView.setCoilCircleImage(url: Int?) {
    load(url) {
        scale(Scale.FILL).error(R.drawable.ic_place_holder_home).placeholder(R.drawable.ic_place_holder_progress)
        transformations(CircleCropTransformation())
    }
}

fun ImageView.glide(resource: Int = 0, url: String? = null) {
    Glide.with(NSApplication.getInstance().applicationContext).load(url?:resource).into(this)
}

fun ImageView.glideWithPlaceHolder(resource: Int = R.drawable.ic_place_holder_product, url: String? = null) {
    Glide.with(NSApplication.getInstance().applicationContext).load(url?:resource).placeholder(resource).into(this)
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

fun ImageView.setGlideWithPlaceHolder(
    activity: Activity,
    url: String?,
    resource: Int = R.drawable.ic_place_holder_product
) {
    Glide.with(activity.applicationContext).load(if (url.isNullOrBlank()) resource else url).placeholder(resource).error(resource).circleCrop().into(this)
}

fun ImageView.glide200(resource: Int = 0, url: String? = null, scale: String?) {
    Glide.with(NSApplication.getInstance().applicationContext).load(url?:resource).apply(
        RequestOptions().transform(
            if (scale.equals(NSConstants.FILL)) CenterCrop() else FitCenter(),
            RoundedCorners(20)
        )
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
    val stringResource = NSUtilities.getStringResource()
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

private const val DEBOUNCE_DELAY = 1000L
private val debounceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
private var debounceJob: Job? = null
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
            debounceJob?.cancel()

            // Start a new debounce job
            debounceJob = debounceScope.launch {
                delay(DEBOUNCE_DELAY)

                // Perform search operation here
                onTextChanged?.invoke(s, start, before, count)
            }
        }

        override fun afterTextChanged(s: Editable?) {
            afterTextChanged?.invoke(s)
        }
    }

    addTextChangedListener(textWatcher)
}

private const val DEBOUNCE_DELAY_SMALL = 400L
fun EditText.addOnTextChangedListenerSmall(
    beforeTextChanged: ((CharSequence?, Int, Int, Int) -> Unit)? = null,
    onTextChanged: ((CharSequence?, Int, Int, Int) -> Unit)? = null,
    afterTextChanged: ((Editable?) -> Unit)? = null
) {
    val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            beforeTextChanged?.invoke(s, start, count, after)
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            debounceJob?.cancel()

            // Start a new debounce job
            debounceJob = debounceScope.launch {
                delay(DEBOUNCE_DELAY_SMALL)

                // Perform search operation here
                onTextChanged?.invoke(s, start, before, count)
            }
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

//fun Spinner.setPlaceholderAdapter(
//    items: SpinnerData,
//    context: Context,
//    selectedId: String?,
//    isHideFirstPosition: Boolean,
//    placeholderName: String? = null,
//    onItemSelectedListener: ((String?) -> Unit)?
//) {
//    if (isHideFirstPosition) {
//        items.title.add(0, placeholderName?:"")
//        items.id.add(0,"")
//    }
//    // Create a custom adapter with the items
//    val adapter = object : ArrayAdapter<String>(context, R.layout.layout_spinner_item, android.R.id.text1, items.title) {
//        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
//            val view = super.getDropDownView(position, convertView, parent)
//            if (isHideFirstPosition) {
//                val bind = LayoutSpinnerItemDropDownBinding.bind(view)
//                if (position == 0) {
//                    bind.text1.visibility = View.GONE
//                } else {
//                    bind.text1.visibility = View.VISIBLE
//                }
//            }
//            return view
//        }
//
//        override fun getView(
//            position: Int,
//            convertView: View?,
//            parent: ViewGroup
//        ): View {
//            val view = super.getView(position, convertView, parent)
//            val bind = LayoutSpinnerItemBinding.bind(view)
//            if (isHideFirstPosition && position == 0) {
//                bind.text1.setTextColor(ColorResources.getPrimaryLightColor())
//            } else {
//                bind.text1.setTextColor(ColorResources.getPrimaryColor())
//            }
//            return view
//        }
//    }
//
//    // Set the drop-down layout style
//    adapter.setDropDownViewResource(R.layout.layout_spinner_item_drop_down)
//
//    // Set the adapter to the Spinner
//    this.adapter = adapter
//
//    // Set a default selection to the first item (placeholder item) if needed
//    if (!isHideFirstPosition && items.title.isNotEmpty()) {
//        this.setSelection(1, false)
//    } else {
//        this.setSelection(0, false)
//    }
//
//    // Set the OnItemSelectedListener to handle item selection
//    this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//            val selectedItem = if (position >= 0) items.id[position] else null
//            onItemSelectedListener?.invoke(selectedItem)
//        }
//
//        override fun onNothingSelected(parent: AdapterView<*>?) {
//            onItemSelectedListener?.invoke(null)
//        }
//    }
//
//    val spinnerPosition = items.id.indexOf(selectedId)
//    if (spinnerPosition != -1) {
//        setSelection(spinnerPosition)
//    }
//}

fun TextView.setTexts(text: String?) {
    val stringResource = NSUtilities.getStringResource()
    this.text = if (text?.isNotEmpty() == true) text else stringResource.unknown
}

public data class Quadruple<out A, out B, out C, out D>(
    public val first: A,
    public val second: B,
    public val third: C,
    public val fourth: D
) : Serializable {

    /**
     * Returns string representation of the [Triple] including its [first], [second], [third] and [fourth] values.
     */
    public override fun toString(): String = "($first, $second, $third $fourth)"
}

public data class QuadrupleFive<out A, out B, out C, out D, out E>(
    public val first: A,
    public val second: B,
    public val third: C,
    public val fourth: D,
    public val fifth: E
) : Serializable {

    /**
     * Returns string representation of the [Triple] including its [first], [second], [third], [fourth] and [fifth] values.
     */
    public override fun toString(): String = "($first, $second, $third $fourth $fifth)"
}

fun MutableList<String>?.getTags(): String {
    this?.remove(" ")
    var tagsList = this?.joinToString(" ")?:""
    if (!tagsList.endsWith(" ")) {
        tagsList = "$tagsList "
    }
    return tagsList
}

fun String.getTagLists(): MutableList<String> {
    if (this.isNotEmpty()) {
        val list: MutableList<String> = this.split(" ").toMutableList()
        list.remove(" ")
        return list
    } else {
        return arrayListOf()
    }
}

fun Spinner.setPlaceholderAdapter(
    items: SpinnerData,
    context: Context,
    colorResources: ColorResources,
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
                bind.text1.setTextColor(colorResources.getPrimaryLightColor())
            } else {
                bind.text1.setTextColor(colorResources.getPrimaryColor())
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

fun ImageView.setCoil(url: String?, exScale: String?, corners: Float = 4f) {
    load(url) {
        scale(if(exScale == NSConstants.FILL) Scale.FILL else Scale.FIT).placeholder(R.drawable.ic_place_holder_progress).error(R.drawable.ic_place_holder_img)
        transformations(RoundedCornersTransformation(corners, corners, corners,corners))
    }
}

fun ImageView.setGlideWithHolder(url: String?, scale: String? = NSConstants.FILL, widthHeight: Int, corners: Int = 30, placeHolder: Int = R.drawable.ic_place_holder_product) {
    val transform = if (scale == NSConstants.FILL) CenterCrop() else FitCenter()
    Glide.with(this.context).load(url).placeholder(R.drawable.ic_place_holder_progress).error(placeHolder).apply(
        if (corners > 0) {
            RequestOptions().transform(
                transform,
                RoundedCorners(corners)
            ).override(widthHeight, widthHeight)
        } else {
            RequestOptions().transform(
                transform
            ).override(widthHeight, widthHeight)
        }
    ).into(this)
}

fun ImageView.setCoil(resource: Int = R.drawable.ic_place_holder_product, url: String? = null) {
    load(url) {
        placeholder(R.drawable.ic_place_holder_progress)
        error(resource)
    }
}

fun ImageView.setCoilCenter(url: String?) {
    load(url) {
        scale(Scale.FILL).error(R.drawable.ic_place_holder_product).placeholder(R.drawable.ic_place_holder_progress)
    }
}

fun ImageView.setNormalCoil(url: String?) {
    load(url) {
        placeholder(R.drawable.ic_place_holder_progress)
        error(R.drawable.ic_place_holder_img)
    }
}

fun ImageView.setCoilCircle(url: String?) {
    load(url) {
        scale(Scale.FILL).placeholder(R.drawable.ic_place_holder_progress).error(R.drawable.ic_place_holder_img)
        transformations(CircleCropTransformation())
    }
}

fun ImageView.setCoilCircle(url: Int) {
    load(url) {
        scale(Scale.FILL).placeholder(R.drawable.ic_place_holder_progress).error(R.drawable.ic_place_holder_img)
        transformations(CircleCropTransformation())
    }
}

fun ImageView.rotation(isRtl: Boolean) {
    this.rotation = if (isRtl) 180f else 0f
}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun String.convertToInt(): Int {
    return if (this.isNotEmpty()) {
        this.toInt()
    } else {
        0
    }
}

fun EditText.onTextChanged(delay: Long = 500L, callback: suspend (String) -> Unit): Job {
    var job: Job? = null

    val watcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Not needed for this implementation
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            job?.cancel()
            job = CoroutineScope(Dispatchers.Main).launch {
                delay(delay)
                s?.toString()?.let { text ->
                    callback(text)
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {
            // Not needed for this implementation
        }
    }

    addTextChangedListener(watcher)

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        // Handle exceptions if any during coroutine execution
        throwable.printStackTrace()
    }

    return Job().apply {
        invokeOnCompletion {
            removeTextChangedListener(watcher)
        }
    }
}

fun getCompareAndGetDeviceLanguage(language: MutableList<LanguageSelectModel>): LanguageSelectModel {
    val countryCode = Locale.getDefault().country
    val languageCode = Locale.getDefault().language
    val locale = "$languageCode-$countryCode"
    val model = language.find { it.locale == locale.lowercase() }
    var code: LanguageSelectModel? = null

    if (model == null) {
        code = language.find { it.locale == languageCode.lowercase() }
    }

    return model
        ?: if (code != null){
            return code
        } else {
            return LanguageSelectModel(locale = "en-us", direction = "rtl")
        }
}

fun ImageView.glideNormal(url: String? = null, callback: (Boolean) -> Unit) {
    Glide.with(NSApplication.getInstance().applicationContext)
        .load(url)
        .listener(object : RequestListener<Drawable> {

            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                callback.invoke(false)
                return false
            }

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                callback.invoke(true)
                return false
            }
        })
        .into(this)
}

fun EditText.formatText(isFirstLetterCapital: Boolean = false) {
    val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            val text = s.toString()
            val formattedText = if (!isFirstLetterCapital) {
                text.uppercase()
            } else {
                if (text.isNotEmpty()) {
                    text[0].uppercase() + text.substring(1)
                } else {
                    text
                }
            }

            if (text != formattedText) {
                setText(formattedText)
                setSelection(formattedText.length) // Place cursor at the end
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Not needed for this implementation
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Not needed for this implementation
        }
    }

    addTextChangedListener(textWatcher)
}
