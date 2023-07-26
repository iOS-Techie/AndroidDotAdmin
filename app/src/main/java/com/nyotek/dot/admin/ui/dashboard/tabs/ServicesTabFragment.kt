package com.nyotek.dot.admin.ui.dashboard.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.nyotek.dot.admin.common.NSFragment
import com.nyotek.dot.admin.common.NSViewPagerAdapter
import com.nyotek.dot.admin.common.callbacks.NSBackClickCallback
import com.nyotek.dot.admin.databinding.FragmentServicesTabBinding
import com.nyotek.dot.admin.ui.serviceManagement.NSServiceManagementFragment

class ServicesTabFragment : NSFragment() {

    private var _binding: FragmentServicesTabBinding? = null
    private val binding get() = _binding!!
    private val mFragmentList: MutableList<Fragment> = ArrayList()
    private var pageIndex = 0
    private var isFragmentLoad: Boolean = false

    companion object {
        fun newInstance() = ServicesTabFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentServicesTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun setFragment() {
        if (!isFragmentLoad) {
            isFragmentLoad = true
            setFragmentList()
        }
    }

    /**
     * Set fragment list
     *
     */
    private fun setFragmentList() {
        mFragmentList.clear()
        mFragmentList.add(NSServiceManagementFragment.newInstance())
        setupViewPager(requireActivity(), binding.servicesPager)
    }

    /**
     * Setup view pager
     *
     * @param activity fragment Activity
     * @param viewPager set fragment in viewpager
     */
    private fun setupViewPager(activity: FragmentActivity, viewPager: ViewPager2) {
        try {
            val adapter = NSViewPagerAdapter(activity)
            adapter.setFragment(mFragmentList)
            viewPager.adapter = adapter
            viewPager.isUserInputEnabled = false
            viewPager.offscreenPageLimit = 3
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    pageIndex = position
                    if (mFragmentList[position] is NSServiceManagementFragment) {
                        (mFragmentList[position] as NSServiceManagementFragment).loadFragment()
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onBackClick(callback: NSBackClickCallback) {
        if (pageIndex > 0) {
            pageIndex -= 1
            binding.servicesPager.setCurrentItem(pageIndex, false)
        } else {
            callback.onBack()
        }
    }
}