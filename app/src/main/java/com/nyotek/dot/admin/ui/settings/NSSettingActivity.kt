package com.nyotek.dot.admin.ui.settings

import android.os.Bundle
import com.nyotek.dot.admin.common.NSActivity
import com.nyotek.dot.admin.databinding.NsActivitySettingBinding

class NSSettingActivity : NSActivity() {

    private lateinit var binding: NsActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NsActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadInitialFragment(intent.extras)
    }

    /**
     * To initialize login fragment
     *
     */
    private fun loadInitialFragment(bundle: Bundle?) {
        replaceCurrentFragment(
            NSSettingFragment.newInstance(bundle),
            false,
            binding.settingContainer.id
        )
    }
}