package com.nyotek.dot.admin.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.NSAlertUtils
import com.nyotek.dot.admin.common.NSLanguageConfig
import com.nyotek.dot.admin.common.NSLog
import com.nyotek.dot.admin.common.NSThemeHelper
import com.nyotek.dot.admin.common.NSUtilities
import com.nyotek.dot.admin.common.callbacks.NSProgressCallback
import com.nyotek.dot.admin.common.callbacks.NSReplaceFragmentCallback
import com.nyotek.dot.admin.common.extension.setCoilCircleImage
import com.nyotek.dot.admin.common.extension.setVisibility
import com.nyotek.dot.admin.common.extension.switchActivity
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.databinding.LayoutContactUsBinding
import com.nyotek.dot.admin.databinding.LayoutHomeHeaderBinding
import com.nyotek.dot.admin.databinding.LayoutLanguageBinding
import com.nyotek.dot.admin.models.responses.StringResourceResponse
import com.nyotek.dot.admin.ui.login.LoginActivity
import com.nyotek.dot.admin.ui.tabs.settings.NSLanguageRecycleAdapter
import javax.inject.Inject

abstract class BaseFragment<VB : ViewBinding>  : Fragment(), (Boolean) -> Unit {

    @Inject
    lateinit var nsLanguageConfig: NSLanguageConfig

    protected var _binding: VB? = null
    protected val binding get() = _binding!!

    private var progressCallback: NSProgressCallback? = null
    private lateinit var mContext: Context
    protected lateinit var activity: Activity
    private var selectLanguageBottomSheet: BottomSheetDialog? = null
    private var callEmailBottomSheet: BottomSheetDialog? = null
    private var languageAdapter: NSLanguageRecycleAdapter? = null
    private var replaceFragmentCallback: NSReplaceFragmentCallback? = null
    private var mainViewModel: BaseViewModel? = null

    // Rename this property to avoid collision
    val stringResource: StringResourceResponse
        get() = NSUtilities.getStringResource()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            _binding = getFragmentBinding(inflater, container)
        }
        return binding.root
    }

    abstract fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): VB

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setupViews()
        setListener()
    }

    open fun setupViews() {
        // Perform any common view setup here
    }

    open fun observeViewModel() {
        // Observe any LiveData or perform ViewModel-related setup here
    }

    open fun loadFragment() {
        // Perform any fragment-specific setup here
    }

    open fun loadFragment(bundle: Bundle?) {
        // Perform any fragment-specific setup here
    }

    open fun setListener() {
        // Perform any fragment-specific setup here
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //_binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        activity = requireActivity()
        try {
            progressCallback = context as NSProgressCallback
            replaceFragmentCallback = context as NSReplaceFragmentCallback
        } catch (exception: ClassCastException) {
            NSLog.e("TAG", "onAttach: ClassCastException: " + exception.message)
        }
    }

    protected open fun replaceFragment(
        fragmentToReplace: Fragment, addToBackStack: Boolean, containerId: Int
    ) {
        replaceFragmentCallback?.replaceCurrentFragment(
            fragmentToReplace, addToBackStack, containerId
        )
    }

    protected fun finish() {
        (mContext as FragmentActivity).finish()
    }

    protected open fun updateProgress(shouldShowProgress: Boolean) {
        progressCallback?.updateProgress(shouldShowProgress)
    }

    protected fun observeBaseViewModel(viewModel: BaseViewModel) {
        mainViewModel = viewModel
        viewModel.apply {
            viewModel.error.observe(viewLifecycleOwner) { error ->
                showErrorDialog(error?.error){}
            }

            viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
                updateProgress(isLoading)
            }

            viewModel.networkStatus.observe(viewLifecycleOwner) { isOnline ->
                if (!isOnline) {
                    val title = stringResource.noNetworkAvailable.ifEmpty { resources.getString(R.string.no_network_available) }
                    val detail = stringResource.networkUnreachableTitle.ifEmpty { resources.getString(R.string.networkUnreachable_title) }
                    showNoNetworkAlertDialog(title, message = detail){}
                }
            }

            viewModel.sessionTimeOut.observe(viewLifecycleOwner) { isSessionTimeOut ->
                if (isSessionTimeOut) {
                    logoutFinal()
                }
            }
        }
    }

    private fun showErrorDialog(message: String?, callback: ((Boolean) -> Unit)) {
        if (message != null) {
            if (message.isNotEmpty()) {
                NSAlertUtils.showAlertDialog(mContext as FragmentActivity, message, callback = callback, sessionCallback = this)
            }
        }
    }

    protected fun showSuccessDialog(title: String?, message: String?, callback: (Boolean) -> Unit) {
        if (message != null) {
            if (message.isNotEmpty()) {
                NSAlertUtils.showAlertDialog(
                    mContext as FragmentActivity,
                    message,
                    title,
                    callback = callback, sessionCallback = this
                )
            }
        }
    }

    protected fun showCommonDialog(title: String?, message: String?, positiveButton: String, negativeButton: String, callback: (Boolean) -> Unit) {
        val errorMessage: String = message ?: stringResource.somethingWentWrong
        NSAlertUtils.showAlertDialog(mContext as FragmentActivity, errorMessage, title, positiveButtonText = positiveButton, negativeButtonText = negativeButton, isCancelNeeded = true, callback = callback, sessionCallback = this)
    }

    protected fun showLogoutDialog(title: String?, message: String?, positiveButton: String, negativeButton: String, callback: (Boolean) -> Unit) {
        val errorMessage: String = message ?: stringResource.somethingWentWrong
        NSAlertUtils.showAlertDialog(mContext as FragmentActivity, errorMessage, title, positiveButtonText = positiveButton, negativeButtonText = negativeButton, isCancelNeeded = true, callback = callback, sessionCallback = this)
    }

    private fun showNoNetworkAlertDialog(title: String?, message: String?, callback: (Boolean) -> Unit) {
        val errorMessage: String = message ?: stringResource.somethingWentWrong
        NSAlertUtils.showAlertDialog(mContext as FragmentActivity, errorMessage, title, callback = callback, sessionCallback = this)
    }

    override fun invoke(isLogout: Boolean) {
      if (isLogout) {
        logoutFinal()
      }
    }

    fun setLayoutHeader(bind: LayoutHomeHeaderBinding, headerTitle: String, headerButton: String = "", isSearch: Boolean = false, isProfile: Boolean = false, isBack: Boolean = false) {
        bind.apply {
            stringResource.apply {
                ivBack.rotation = if (nsLanguageConfig.isLanguageRtl()) 180f else 0f
                ivProfile.setCoilCircleImage(R.drawable.ic_profile_demo)
                etSearch.hint = searchHere
                tvHeaderTitle.text = headerTitle
                tvHeaderBtn.text = headerButton
                clProfileDetail.setVisibility(isProfile)
                viewSpace.setVisibility(!isBack)
                tvHeaderBtn.setVisibility(headerButton.isNotEmpty())
                clSearch.setVisibility(isSearch)
                ivBack.setVisibility(isBack)
            }
        }
    }

    fun logoutFinal() {
        nsLanguageConfig.logout()
        switchActivity(
            LoginActivity::class.java,
            flags = intArrayOf(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }

    fun showDialogLanguageSelect(isFromHome: Boolean = false, colorResources: ColorResources, languageConfig: NSLanguageConfig, themeHelper: NSThemeHelper, callback: () -> Unit) {
        try {
            val sheetView: View = activity.layoutInflater
                .inflate(R.layout.layout_language, null)
            selectLanguageBottomSheet = BottomSheetDialog(activity, R.style.MyBottomSheetDialogTheme)
            selectLanguageBottomSheet?.setContentView(sheetView)
            selectLanguageBottomSheet?.setCanceledOnTouchOutside(false)
            selectLanguageBottomSheet?.show()
            selectLanguageBottomSheet?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
            val contactBinding = LayoutLanguageBinding.bind(sheetView)
            val pref = languageConfig.dataStorePreference
            val languageList = colorResources.themeHelper.getLocalLanguageLists()

            with(contactBinding) {
                colorResources.getStringResource().apply {
                    tvLanguageTitle.text = selectLanguage
                    tvCancelLanguageDialog.text = cancel
                    tvCancelLanguageDialog.setVisibility(!isFromHome)
                    viewSpace.setVisibility(isFromHome)
                }
                selectLanguageBottomSheet?.setCancelable(!isFromHome)
                rvLanguage.layoutManager = LinearLayoutManager(activity)
                pref.languagePosition = languageList.indexOfFirst { it.locale == pref.languageData }
                var selectedLanguage: Int

                languageAdapter =
                    NSLanguageRecycleAdapter(colorResources, languageConfig, languageConfig.isLanguageRtl()) { position ->
                        pref.languagePosition = position
                        languageAdapter?.notifyItemRangeChanged(0, languageList.size)
                        selectedLanguage = position

                        if (languageList[selectedLanguage].locale != pref.languageData) {
                            selectLanguageBottomSheet?.dismiss()
                            pref.isLanguageSelected = true
                            NSThemeHelper.isLanguageChange = true
                            
                            val language = (languageList[selectedLanguage].locale ?: "").lowercase()
                            val isRtl = languageList[selectedLanguage].direction.equals("rtl")
                            
                            languageConfig.setLanguagesPref(
                                language,
                                isRtl
                            )
                            languageConfig.createLanguageMap(activity, themeHelper.getBootStrapData()?.strings, language){}
                            mainViewModel?.localChange(languageConfig)
                            if (!isFromHome) {
                                restartActivity(activity)
                            } else {
                                callback.invoke()
                            }
                        }
                    }
                rvLanguage.adapter = languageAdapter
                languageAdapter?.setData(languageList)
                rvLanguage.isNestedScrollingEnabled = false

                // dismiss dialog
                tvCancelLanguageDialog.setOnClickListener {
                    selectLanguageBottomSheet?.dismiss()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun restartActivity(activity: Activity) {
        val intent = activity.intent
        activity.finish()
        activity.startActivity(intent)
    }

    fun showDialogCallEmailAction(callValue: String, emailValue: String, colorResources: ColorResources) {
        try {
            val sheetView: View = activity.layoutInflater
                .inflate(R.layout.layout_contact_us, null)
            callEmailBottomSheet = BottomSheetDialog(activity, R.style.MyBottomSheetDialogTheme)
            callEmailBottomSheet?.setContentView(sheetView)
            callEmailBottomSheet?.setCanceledOnTouchOutside(false)
            callEmailBottomSheet?.show()
            callEmailBottomSheet?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
            val contactBinding = LayoutContactUsBinding.bind(sheetView)
            colorResources.setCardBackground(contactBinding.btnClCall, 8f, 2, colorResources.getBackgroundColor(), colorResources.getBorderColor())
            colorResources.setCardBackground(contactBinding.btnClEmail, 8f, 2, colorResources.getBackgroundColor(), colorResources.getBorderColor())

            contactBinding.apply {
                colorResources.getStringResource().apply {
                    tvContactUsTitle.text = contactUs
                    tvEmail.text = email
                    tvCall.text = call
                    tvCancelContactUs.text = cancel
                }
            }

            // dismiss dialog
            contactBinding.tvCancelContactUs.setOnClickListener {
                callEmailBottomSheet?.dismiss()
            }

            // select email
            contactBinding.btnClEmail.setOnClickListener {
                callEmailBottomSheet?.dismiss()
                val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${emailValue}"))
                startActivity(Intent.createChooser(emailIntent, "Send Email"))
            }

            //select call
            contactBinding.btnClCall.setOnClickListener {
                callEmailBottomSheet?.dismiss()
                NSUtilities.callUser(activity, callValue)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}