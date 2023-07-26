package com.nyotek.dot.admin.ui.passwordReset

import android.os.Bundle
import com.nyotek.dot.admin.common.NSActivity
import com.nyotek.dot.admin.databinding.NsActivityPasswordResetBinding

class NSPasswordResetActivity : NSActivity() {

    private lateinit var binding: NsActivityPasswordResetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NsActivityPasswordResetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadInitialFragment()
    }

    /**
     * To initialize password reset fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSPasswordResetFragment.newInstance(), false, binding.resetContainer.id)
    }
}