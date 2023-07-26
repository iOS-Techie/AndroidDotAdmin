package com.nyotek.dot.admin.ui.login

import android.os.Bundle
import com.nyotek.dot.admin.common.NSActivity
import com.nyotek.dot.admin.databinding.NsActivityLoginBinding

class NSLoginActivity : NSActivity() {

    private lateinit var binding: NsActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NsActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadInitialFragment()
    }

    /**
     * To initialize login fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSLoginFragment.newInstance(), false, binding.loginContainer.id)
    }
}