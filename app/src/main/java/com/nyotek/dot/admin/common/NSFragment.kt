package com.nyotek.dot.admin.common

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.callbacks.*
import com.nyotek.dot.admin.common.utils.*
import com.nyotek.dot.admin.databinding.LayoutContactUsBinding
import com.nyotek.dot.admin.databinding.LayoutHomeHeaderBinding
import com.nyotek.dot.admin.databinding.LayoutLanguageBinding
import com.nyotek.dot.admin.databinding.LayoutServiceHeaderBinding
import com.nyotek.dot.admin.repository.network.responses.StringResourceResponse
import com.nyotek.dot.admin.ui.login.NSLoginActivity
import com.nyotek.dot.admin.ui.settings.NSLanguageRecycleAdapter
import com.nyotek.dot.admin.ui.splash.NSSplashActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * The base class for all fragments which holds the members and methods common to all fragments
 */
open class NSFragment : Fragment() {
    val tagLog: String = this::class.java.simpleName
    private lateinit var mContext: Context
    protected lateinit var activity: Activity
    private var replaceFragmentCallback: NSReplaceFragmentCallback? = null
    private var progressCallback: NSProgressCallback? = null
    private var callEmailBottomSheet: BottomSheetDialog? = null
    var selectLanguageBottomSheet: BottomSheetDialog? = null
    val pref = NSApplication.getInstance().getPrefs()
    var stringResource = StringResourceResponse()
    var viewModelMain: NSViewModel? = null
    private var languageAdapter: NSLanguageRecycleAdapter? = null

    var fleetManagementFragmentChangeCallback: NSFragmentChangeCallback? = object : NSFragmentChangeCallback {
        override fun setFragment(previousFragmentName: String, fragment: Fragment, isBackStack: Boolean,  bundle: Bundle) {
            EventBus.getDefault().post(NSVendorCall(fragment, bundle))
         }
    }

    var dispatchManagementFragmentChangeCallback: NSFragmentChangeCallback? = object : NSFragmentChangeCallback {
        override fun setFragment(previousFragmentName: String, fragment: Fragment, isBackStack: Boolean,  bundle: Bundle) {
            EventBus.getDefault().post(NSDispatchCall(fragment, bundle))
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        activity = requireActivity()
        try {
            progressCallback = context as NSProgressCallback
            replaceFragmentCallback = context as NSReplaceFragmentCallback
        } catch (exception: ClassCastException) {
            NSLog.e(tagLog, "onAttach: ClassCastException: " + exception.message)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        stringResource = StringResourceResponse()
    }

    override fun onStop() {
        super.onStop()
        (mContext as FragmentActivity).hideKeyboard()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    /**
     * To replace the fragments
     *
     * @param fragmentToReplace The fragment to replace
     * @param addToBackStack    Whether to add the fragment to backstack or not
     * @param containerId       The frame layout containing the fragment
     */
    protected open fun replaceFragment(
        fragmentToReplace: Fragment, addToBackStack: Boolean, containerId: Int
    ) {
        replaceFragmentCallback?.replaceCurrentFragment(
            fragmentToReplace, addToBackStack, containerId
        )
    }

    /**
     * To show alert dialog
     *
     * @param message The message to show as alert message
     */
    //Use of Outside change to Protected
    private fun showAlertDialog(message: String?, callback: ((Boolean) -> Unit)? = null) {
        val errorMessage: String = message ?: stringResource.somethingWentWrong
        NSAlertUtils.showAlertDialog(mContext as FragmentActivity, errorMessage, callback = callback)
    }

    /**
     * To show alert dialog
     *
     * @param message The message to show as alert message
     */
    protected fun showSuccessDialog(title: String?, message: String?, alertKey: String = NSConstants.POSITIVE_CLICK, callback: ((Boolean) -> Unit)? = null) {
        val errorMessage: String = message ?: stringResource.somethingWentWrong
        NSAlertUtils.showAlertDialog(mContext as FragmentActivity, errorMessage, title, alertKey = alertKey, callback = callback)
    }

    /**
     * To show logout alert dialog
     *
     * @param message The message to show as alert message
     */
    protected fun showCommonDialog(title: String?, message: String?, alertKey: String = NSConstants.POSITIVE_CLICK, positiveButton: String, negativeButton: String, callback: ((Boolean) -> Unit)? = null) {
        val errorMessage: String = message ?: stringResource.somethingWentWrong
        NSAlertUtils.showAlertDialog(mContext as FragmentActivity, errorMessage, title, alertKey = alertKey, positiveButtonText = positiveButton, negativeButtonText = negativeButton, isCancelNeeded = true, callback = callback)
    }

    /**
     * To show logout alert dialog
     *
     * @param message The message to show as alert message
     */
    protected fun showLogoutDialog(title: String?, message: String?, positiveButton: String, negativeButton: String, callback: ((Boolean) -> Unit)? = null) {
        val errorMessage: String = message ?: stringResource.somethingWentWrong
        NSAlertUtils.showAlertDialog(mContext as FragmentActivity, errorMessage, title, alertKey = NSConstants.LOGOUT_CLICK, positiveButtonText = positiveButton, negativeButtonText = negativeButton, isCancelNeeded = true, callback = callback)
    }

    /**
     * To display the no network dialog
     */
    //Use of Outside change to Protected
    private fun showNoNetworkAlertDialog(title: String?, message: String?, callback: ((Boolean) -> Unit)? = null) {
        val errorMessage: String = message ?: stringResource.somethingWentWrong
        NSAlertUtils.showAlertDialog(mContext as FragmentActivity, errorMessage, title, callback = callback)
    }

    /**
     * To parse api error and show alert
     *
     * @param apiErrorList api error list
     */
    //Use of Outside change to Protected
    private fun parseAndShowApiError(apiErrorList: List<Any>) {
        showAlertDialog(NSUtilities.parseApiErrorList(mContext, apiErrorList))
    }

    /**
     * To back press
     */
    protected fun onBackPress() {
        @Suppress("DEPRECATION")
        (mContext as FragmentActivity).onBackPressed()
    }

    /**
     * To finish
     */
    protected fun finish() {
        (mContext as FragmentActivity).finish()
    }

    /**
     * To update the progress bar by communicating with base activity
     *
     * @param shouldShowProgress The boolean to determine whether to show progress bar
     */
    protected open fun updateProgress(shouldShowProgress: Boolean) {
        progressCallback?.updateProgress(shouldShowProgress)
    }

    /**
     * To observe the view model for data changes
     */
    fun baseObserveViewModel(mainViewModel: NSViewModel) {
        viewModelMain = mainViewModel
        with(mainViewModel) {
            isProgressShowing.observe(
                viewLifecycleOwner
            ) { shouldShowProgress ->
                updateProgress(shouldShowProgress)
            }

            failureErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
                isSwipeRefresh.value = true
                showAlertDialog(errorMessage)
            }

            apiErrors.observe(viewLifecycleOwner) { apiErrors ->
                isSwipeRefresh.value = true
                parseAndShowApiError(apiErrors)
            }

            noNetworkAlert.observe(viewLifecycleOwner) {
                isSwipeRefresh.value = true
                val title = stringResource.noNetworkAvailable.ifEmpty { resources.getString(R.string.no_network_available) }
                val detail = stringResource.networkUnreachableTitle.ifEmpty { resources.getString(R.string.networkUnreachable_title) }

                showNoNetworkAlertDialog(
                    title,
                    detail
                )
            }

            validationErrorId.observe(viewLifecycleOwner) { errorId ->
                isSwipeRefresh.value = true
                showAlertDialog(errorId)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshSolved(@Suppress("UNUSED_PARAMETER") refreshEvent: NSRefreshEvent) {
        viewModelMain?.apply {
            isSwipeRefresh.value = true
        }
    }


    /**
     * Show dialog call email action
     *
     * @param callValue contact using call
     * @param emailValue contact using email id
     */
    @SuppressLint("InflateParams")
    fun showDialogCallEmailAction(callValue: String, emailValue: String) {
        try {
            val sheetView: View = activity.layoutInflater
                .inflate(R.layout.layout_contact_us, null)
            callEmailBottomSheet = BottomSheetDialog(activity, R.style.MyBottomSheetDialogTheme)
            callEmailBottomSheet!!.setContentView(sheetView)
            callEmailBottomSheet!!.setCanceledOnTouchOutside(false)
            callEmailBottomSheet!!.show()
            callEmailBottomSheet!!.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            val contactBinding = LayoutContactUsBinding.bind(sheetView)
            ColorResources.setCardBackground(contactBinding.btnClCall, 8f, 2, ColorResources.getBackgroundColor(), ColorResources.getBorderColor())
            ColorResources.setCardBackground(contactBinding.btnClEmail, 8f, 2, ColorResources.getBackgroundColor(), ColorResources.getBorderColor())

            contactBinding.apply {
                stringResource.apply {
                    tvContactUsTitle.text = contactUs
                    tvEmail.text = email
                    tvCall.text = call
                    tvCancelContactUs.text = cancel
                }
            }

            // dismiss dialog
            contactBinding.tvCancelContactUs.setOnClickListener {
                callEmailBottomSheet!!.dismiss()
            }

            // select email
            contactBinding.btnClEmail.setOnClickListener {
                callEmailBottomSheet!!.dismiss()
                val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${emailValue}"))
                startActivity(Intent.createChooser(emailIntent, "Send Email"))
            }

            //select call
            contactBinding.btnClCall.setOnClickListener {
                callEmailBottomSheet!!.dismiss()
                NSUtilities.callUser(activity, callValue)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Is language selected
     *
     * @return is english language selected
     */
    fun isLanguageSelected(): Boolean {
        return pref.isLanguageRTL
    }

    /**
     * Show dialog language select
     *
     */
    @SuppressLint("InflateParams")
    fun showDialogLanguageSelect() {
        try {
            val sheetView: View = activity.layoutInflater
                .inflate(R.layout.layout_language, null)
            selectLanguageBottomSheet =
                BottomSheetDialog(activity, R.style.MyBottomSheetDialogTheme)
            selectLanguageBottomSheet!!.setContentView(sheetView)
            selectLanguageBottomSheet!!.setCanceledOnTouchOutside(false)
            selectLanguageBottomSheet!!.show()
            selectLanguageBottomSheet!!.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            val contactBinding = LayoutLanguageBinding.bind(sheetView)
            val pref = NSApplication.getInstance().getPrefs()
            val languageList = NSApplication.getInstance().getLocalLanguages()

            with(contactBinding) {
                stringResource.apply {
                    tvLanguageTitle.text = selectLanguage
                    tvCancelLanguageDialog.text = cancel
                }

                rvLanguage.layoutManager = LinearLayoutManager(activity)
                languageAdapter =
                    NSLanguageRecycleAdapter(pref) {position ->
                        notifyAdapter(languageAdapter!!)
                        selectLanguageBottomSheet!!.dismiss()
                        pref.isLanguageSelected = true

                        NSLanguageConfig.setLanguagesPref(
                            position,
                            (languageList[position].locale?:"").lowercase(),
                            languageList[position].direction.equals("rtl")
                        )
                        NSApplication.getInstance().setSelectedNavigationType(NSConstants.DASHBOARD_TAB)
                        switchActivity(
                            NSSplashActivity::class.java,
                            flags = intArrayOf(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK),
                        )
                    }
                rvLanguage.adapter = languageAdapter
                languageAdapter?.setData(languageList)
                rvLanguage.isNestedScrollingEnabled = false

                // dismiss dialog
                tvCancelLanguageDialog.setOnClickListener {
                    selectLanguageBottomSheet!!.dismiss()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLogoutEvent(@Suppress("UNUSED_PARAMETER") logoutEvent: NSLogoutEvent) {
        NSLanguageConfig.logout()
        switchActivity(
            NSLoginActivity::class.java,
            flags = intArrayOf(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }

    fun setLayoutHeader(bind: LayoutHomeHeaderBinding, headerTitle: String, headerButton: String = "", isSearch: Boolean = false, isProfile: Boolean = false, isBack: Boolean = false) {
        bind.apply {
            stringResource.apply {
                viewSpace.setVisibility(!isBack)
                ivBack.rotation = if (isLanguageSelected()) 180f else 0f
                ivProfile.setCircleImage(R.drawable.ic_profile_demo)
                clProfileDetail.setVisibility(isProfile)
                etSearch.hint = searchHere
                tvHeaderTitle.text = headerTitle
                tvHeaderBtn.text = headerButton
                tvHeaderBtn.setVisibility(headerButton.isNotEmpty())
                clSearch.setVisibility(isSearch)
                ivBack.setVisibility(isBack)
            }
        }
    }

    fun setLayoutServiceHeader(bind: LayoutServiceHeaderBinding, headerTitle: String, headerDate: String = "", subHeaderTitle: String = "", isProfile: Boolean, isBack: Boolean, isSearch: Boolean = false) {
        bind.apply {
            stringResource.apply {
                viewSpace.setVisibility(!isBack)
                ivBack.rotation = if (isLanguageSelected()) 180f else 0f
                ivProfile.setCircleImage(R.drawable.ic_profile_demo)
                clProfileDetail.setVisibility(isProfile)
                clSearch.setVisibility(isSearch)
                tvHeaderDate.text = headerDate
                tvHeaderDate.setVisibility(headerDate.isNotEmpty())
                tvSubHeaderTitle.text = subHeaderTitle
                tvSubHeaderTitle.setVisibility(subHeaderTitle.isNotEmpty())
                tvHeaderTitle.text = headerTitle
                ivBack.setVisibility(isBack)
            }
        }
    }
}
