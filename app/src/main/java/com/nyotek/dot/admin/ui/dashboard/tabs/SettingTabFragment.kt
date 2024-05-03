package com.nyotek.dot.admin.ui.dashboard.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nyotek.dot.admin.common.NSFragment
import com.nyotek.dot.admin.common.NSSettingCall
import com.nyotek.dot.admin.common.callbacks.NSBackClickCallback
import com.nyotek.dot.admin.common.utils.setPager
import com.nyotek.dot.admin.databinding.FragmentSettingTabBinding
import com.nyotek.dot.admin.repository.network.responses.FragmentSelectModel
import com.nyotek.dot.admin.ui.settings.NSSettingFragment
import com.nyotek.dot.admin.ui.settings.profile.NSUserDetailFragment
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class SettingTabFragment : NSFragment() {

    private var _binding: FragmentSettingTabBinding? = null
    private val binding get() = _binding!!
    private val mFragmentList: MutableList<FragmentSelectModel> = ArrayList()
    private var pageIndex = 0
    private var isFragmentLoad: Boolean = false

    companion object {
        fun newInstance() = SettingTabFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingTabBinding.inflate(inflater, container, false)
        setFragmentList()
        return binding.root
    }

    fun setFragment() {
        if (!isFragmentLoad) {
            isFragmentLoad = true

        }
    }

    /**
     * Set fragment list
     *
     */
    private fun setFragmentList() {
        mFragmentList.clear()
        mFragmentList.add(FragmentSelectModel(0, NSSettingFragment.newInstance()))
        mFragmentList.add(FragmentSelectModel(1, NSUserDetailFragment.newInstance()))

        binding.settingPager.setPager(
            requireActivity(),
            mFragmentList.map { it.framgents!! } as MutableList<Fragment>) { position ->
            pageIndex = mFragmentList[position].page
            val fragment = mFragmentList[position].framgents
            if (fragment is NSSettingFragment) {
                fragment.loadFragment()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun onSettingCall(event: NSSettingCall) {

        when (event.fragment) {
            is NSUserDetailFragment -> {
                binding.settingPager.setCurrentItem(1, false)
                (mFragmentList[1].framgents as NSUserDetailFragment).loadFragment()
            }
        }
    }

    fun onBackClick(callback: NSBackClickCallback) {
        if (pageIndex > 0) {
            pageIndex -= 1
            binding.settingPager.setCurrentItem(pageIndex, false)
        } else {
            callback.onBack()
        }
    }
}