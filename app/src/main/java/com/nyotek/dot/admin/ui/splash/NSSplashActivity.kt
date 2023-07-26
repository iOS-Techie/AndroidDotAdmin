package com.nyotek.dot.admin.ui.splash

import android.os.Bundle
import com.nyotek.dot.admin.common.NSActivity
import com.nyotek.dot.admin.databinding.NsActivitySplashBinding

class NSSplashActivity : NSActivity() {
    private lateinit var activitySplashBinding: NsActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySplashBinding = NsActivitySplashBinding.inflate(layoutInflater)
        setContentView(activitySplashBinding.root)
        loadInitialFragment()
    }

    /**
     * To initialize select address fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSSplashFragment.newInstance(), false, activitySplashBinding.splashContainer.id)
    }
}