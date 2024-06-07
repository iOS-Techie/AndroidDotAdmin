package com.nyotek.dot.admin.ui.login

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.base.BaseFragment
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSUtilities
import com.nyotek.dot.admin.common.callbacks.NSLocalJsonCallback
import com.nyotek.dot.admin.common.extension.switchActivity
import com.nyotek.dot.admin.databinding.FragmentLoginBinding
import com.nyotek.dot.admin.models.responses.NSLanguageStringResponse
import com.nyotek.dot.admin.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    private val viewModel by viewModels<LoginViewModel>()

    private lateinit var loginUI: LoginUI

    companion object {
        fun newInstance() = LoginFragment()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        super.setupViews()
        observeBaseViewModel(viewModel)
        loginUI = LoginUI(binding, viewModel.colorResources)
    }

    override fun setListener() {
        super.setListener()
        binding.apply {
            viewModel.apply {
                btnLogin.setOnClickListener {
                    viewModelScope.launch {
                        login(etEmailPhone.text.toString(), etPassword.text.toString(), obj)
                    }
                }
            }
        }
    }

    val obj: (Boolean, Boolean, Boolean) -> Unit = { isThemeAvailable, isLocalLng, isShowLanguageSelectDialog ->
        if (isThemeAvailable) {
            NSConstants.IS_LANGUAGE_UPDATE = true
            switchActivity(
                MainActivity::class.java,
                flags = intArrayOf(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        } else if (isLocalLng) {
            getLocalLng()
        } else if (isShowLanguageSelectDialog) {
            showDialogLanguageSelect(true, viewModel.colorResources, viewModel.languageConfig)
        }
    }

    private fun getLocalLng() {
        viewModel.apply {
            NSUtilities.getLocalJsonRowData(requireActivity(), R.raw.local_json, object :
                NSLocalJsonCallback {
                override fun onLocal(json: NSLanguageStringResponse) {
                    setLanguageJsonData(json, obj)
                }
            })
        }
    }
}