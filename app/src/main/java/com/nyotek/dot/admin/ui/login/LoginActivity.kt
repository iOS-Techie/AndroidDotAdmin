package com.nyotek.dot.admin.ui.login

import android.os.Bundle
import com.nyotek.dot.admin.base.NSActivity
import com.nyotek.dot.admin.databinding.NsActivityCommonBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : NSActivity() {
    private lateinit var binding: NsActivityCommonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NsActivityCommonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadInitialFragment()
    }

    private fun loadInitialFragment() {
        replaceCurrentFragment(LoginFragment.newInstance(), false, binding.commonContainer.id)
    }
}