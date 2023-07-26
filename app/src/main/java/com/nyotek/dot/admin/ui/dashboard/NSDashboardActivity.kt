package com.nyotek.dot.admin.ui.dashboard

import android.os.Bundle
import com.nyotek.dot.admin.common.NSActivity
import com.nyotek.dot.admin.common.NSOnBackPressEvent
import com.nyotek.dot.admin.common.NSOnBackPressReceiveEvent
import com.nyotek.dot.admin.databinding.NsActivityDashboardBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class NSDashboardActivity : NSActivity() {
    private lateinit var binding: NsActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NsActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadInitialFragment()
    }

    /**
     * To initialize login fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSDashboardFragment.newInstance(), false, binding.dashboardContainer.id)
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onBackPressed() {
        EventBus.getDefault().post(NSOnBackPressEvent())
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBackPress(@Suppress("UNUSED_PARAMETER") event: NSOnBackPressReceiveEvent) {
        @Suppress("DEPRECATION")
        super.onBackPressed()
    }
}