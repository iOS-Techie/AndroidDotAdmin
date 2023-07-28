package com.nyotek.dot.admin.ui.dashboard.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.nyotek.dot.admin.common.NSFragment
import com.nyotek.dot.admin.common.NSVendorCall
import com.nyotek.dot.admin.common.callbacks.NSBackClickCallback
import com.nyotek.dot.admin.common.callbacks.NSOnPageChangeCallback
import com.nyotek.dot.admin.common.utils.setPager
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

        binding.fleetPager.setPager(
            requireActivity(),
            mFragmentList,
            object : NSOnPageChangeCallback {
                override fun onPageChange(position: Int) {
                    pageIndex = position
                    val fragment = mFragmentList[position]
                    if (fragment is NSFleetFragment) {
                        fragment.loadFragment()
                    }
                }
            })
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