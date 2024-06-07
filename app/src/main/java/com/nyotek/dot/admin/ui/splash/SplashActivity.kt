package com.nyotek.dot.admin.ui.splash

import android.os.Bundle
import com.nyotek.dot.admin.base.NSActivity
import com.nyotek.dot.admin.databinding.ActivityMainBinding
import com.nyotek.dot.admin.databinding.NsActivityCommonBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : NSActivity() {
    private lateinit var binding: NsActivityCommonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NsActivityCommonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadInitialFragment()
    }

    private fun loadInitialFragment() {
        replaceCurrentFragment(SplashFragment.newInstance(), false, binding.commonContainer.id)
    }
}