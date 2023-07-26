package com.nyotek.dot.admin.ui.dashboard.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.nyotek.dot.admin.common.NSFragment
import com.nyotek.dot.admin.common.NSVendorCall
import com.nyotek.dot.admin.common.NSViewPagerAdapter
import com.nyotek.dot.admin.common.callbacks.NSBackClickCallback
import com.nyotek.dot.admin.databinding.FragmentFleetTabBinding
import com.nyotek.dot.admin.ui.fleets.NSFleetFragment
import com.nyotek.dot.admin.ui.fleets.detail.NSFleetDetailFragment
import com.nyotek.dot.admin.ui.fleets.vehicle.detail.NSVehicleDetailFragment
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class FleetTabFragment : NSFragment() {

    private var _binding: FragmentFleetTabBinding? = null
    private val binding get() = _binding!!
    private val mFragmentList: MutableList<Fragment> = ArrayList()
    private var pageIndex = 0
    private var isFragmentLoad: Boolean = false

    companion object {
        fun newInstance() = FleetTabFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFleetTabBinding.inflate(inflater, container, false)
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
        mFragmentList.add(NSFleetFragment.newInstance())
        mFragmentList.add(NSFleetDetailFragment.newInstance(bundleOf()))
        mFragmentList.add(NSVehicleDetailFragment.newInstance(bundleOf()))
        setupViewPager(requireActivity(), binding.fleetPager)
    }

    /**
     * Setup view pager
     *
     * @param activity set Fragment Activity
     * @param viewPager set fragments in viewpager
     */
    private fun setupViewPager(activity: FragmentActivity, viewPager: ViewPager2) {
        try {
            val adapter = NSViewPagerAdapter(activity)
            adapter.setFragment(mFragmentList)
            viewPager.adapter = adapter
            viewPager.isUserInputEnabled = false
            viewPager.offscreenPageLimit = 2
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    pageIndex = position
                    if (mFragmentList[position] is NSFleetFragment) {
                        (mFragmentList[position] as NSFleetFragment).loadFragment()
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun onVendorCall(event: NSVendorCall) {
        if (event.fragment is NSFleetDetailFragment) {
            binding.fleetPager.setCurrentItem(1, false)
            (mFragmentList[1] as NSFleetDetailFragment).loadFragment(event.bundle)
        } else if (event.fragment is NSVehicleDetailFragment) {
            binding.fleetPager.setCurrentItem(2, false)
            (mFragmentList[2] as NSVehicleDetailFragment).resetFragment()
            (mFragmentList[2] as NSVehicleDetailFragment).loadFragment(event.bundle)
        }
    }

    fun onBackClick(callback: NSBackClickCallback) {
        if (pageIndex > 0) {
            pageIndex -= 1
            binding.fleetPager.setCurrentItem(pageIndex, false)
        } else {
            callback.onBack()
        }
    }
}