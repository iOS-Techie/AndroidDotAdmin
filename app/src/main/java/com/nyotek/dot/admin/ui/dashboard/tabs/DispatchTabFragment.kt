package com.nyotek.dot.admin.ui.dashboard.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.nyotek.dot.admin.common.NSDispatchCall
import com.nyotek.dot.admin.common.NSFragment
import com.nyotek.dot.admin.common.NSVendorCall
import com.nyotek.dot.admin.common.callbacks.NSBackClickCallback
import com.nyotek.dot.admin.common.utils.setPager
import com.nyotek.dot.admin.databinding.FragmentDispatchTabBinding
import com.nyotek.dot.admin.databinding.FragmentFleetTabBinding
import com.nyotek.dot.admin.repository.network.responses.FragmentSelectModel
import com.nyotek.dot.admin.ui.dispatch.NSDispatchFragment
import com.nyotek.dot.admin.ui.dispatch.detail.NSDispatchDetailFragment
import com.nyotek.dot.admin.ui.fleets.NSFleetFragment
import com.nyotek.dot.admin.ui.fleets.detail.NSFleetDetailFragment
import com.nyotek.dot.admin.ui.fleets.employee.detail.NSDriverDetailFragment
import com.nyotek.dot.admin.ui.fleets.vehicle.detail.NSVehicleDetailFragment
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class DispatchTabFragment : NSFragment() {

    private var _binding: FragmentDispatchTabBinding? = null
    private val binding get() = _binding!!
    private val mFragmentList: MutableList<FragmentSelectModel> = ArrayList()
    private var pageIndex = 0
    private var isFragmentLoad: Boolean = false

    companion object {
        fun newInstance() = DispatchTabFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDispatchTabBinding.inflate(inflater, container, false)
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
        mFragmentList.add(FragmentSelectModel(0, NSDispatchFragment.newInstance()))
        mFragmentList.add(FragmentSelectModel(1, NSDispatchDetailFragment.newInstance(bundleOf())))
       /* mFragmentList.add(FragmentSelectModel(1, NSFleetDetailFragment.newInstance(bundleOf())))
        mFragmentList.add(FragmentSelectModel(2, NSVehicleDetailFragment.newInstance(bundleOf())))
        mFragmentList.add(FragmentSelectModel(2, NSDriverDetailFragment.newInstance(bundleOf())))*/

        binding.dispatchPager.setPager(
            requireActivity(),
            mFragmentList.map { it.framgents!! } as MutableList<Fragment>) { position ->
            pageIndex = mFragmentList[position].page
            val fragment = mFragmentList[position].framgents
            if (fragment is NSDispatchFragment) {
                fragment.loadFragment()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun onDispatchCall(event: NSDispatchCall) {

        when (event.fragment) {
            is NSDispatchDetailFragment -> {
                binding.dispatchPager.setCurrentItem(1, false)
                (mFragmentList[1].framgents as NSDispatchDetailFragment).loadFragment(event.bundle)
            }

            /*is NSVehicleDetailFragment -> {
                binding.fleetPager.setCurrentItem(2, false)
                (mFragmentList[2].framgents as NSVehicleDetailFragment).resetFragment()
                (mFragmentList[2].framgents as NSVehicleDetailFragment).loadFragment(event.bundle)
            }

            is NSDriverDetailFragment -> {
                binding.fleetPager.setCurrentItem(3, false)
                (mFragmentList[3].framgents as NSDriverDetailFragment).resetFragment()
                (mFragmentList[3].framgents as NSDriverDetailFragment).loadFragment(event.bundle)
            }*/
        }
    }

    fun onBackClick(callback: NSBackClickCallback) {
        if (pageIndex > 0) {
            pageIndex -= 1
            binding.dispatchPager.setCurrentItem(pageIndex, false)
        } else {
            callback.onBack()
        }
    }
}