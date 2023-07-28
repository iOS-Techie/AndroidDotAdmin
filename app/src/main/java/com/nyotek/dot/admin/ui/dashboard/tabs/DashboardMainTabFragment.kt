package com.nyotek.dot.admin.ui.dashboard.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nyotek.dot.admin.common.NSFragment
import com.nyotek.dot.admin.common.callbacks.NSBackClickCallback
import com.nyotek.dot.admin.common.callbacks.NSOnPageChangeCallback
import com.nyotek.dot.admin.common.utils.isValidList
import com.nyotek.dot.admin.common.utils.setPager
import com.nyotek.dot.admin.databinding.FragmentDashboardMainTabBinding
import com.nyotek.dot.admin.ui.dashboardTab.NSDashboardTabFragment

class DashboardMainTabFragment : NSFragment() {

    private var _binding: FragmentDashboardMainTabBinding? = null
    private val binding get() = _binding!!
    private val mFragmentList: MutableList<Fragment> = ArrayList()
    private var pageIndex = 0
    private var isFragmentLoad: Boolean = false

    companion object {
        fun newInstance() = DashboardMainTabFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardMainTabBinding.inflate(inflater, container, false)
        setFragmentList()
        return binding.root
    }

    fun setFragment() {
        if (!isFragmentLoad) {
            isFragmentLoad = true
            setFragmentList()
        }
    }

    override fun onResume() {
        super.onResume()
        onEventBus(true)
    }

    override fun onPause() {
        super.onPause()
        onEventBus(false)
    }

    private fun onEventBus(isRegister: Boolean) {
        if (mFragmentList.isValidList()) {
            (mFragmentList[0] as NSDashboardTabFragment).eventRegister(isRegister)
        }
    }

    /**
     * Set fragment list
     *
     */
    private fun setFragmentList() {
        mFragmentList.clear()
        mFragmentList.add(NSDashboardTabFragment.newInstance())

        binding.dashboardPager.setPager(
            requireActivity(),
            mFragmentList,
            object : NSOnPageChangeCallback {
                override fun onPageChange(position: Int) {
                    pageIndex = position
                    val fragment = mFragmentList[position]
                    if (fragment is NSDashboardTabFragment) {
                        fragment.loadFragment()
                    }
                }
            })
    }

    fun onBackClick(callback: NSBackClickCallback) {
        if (pageIndex > 0) {
            pageIndex -= 1
            binding.dashboardPager.setCurrentItem(pageIndex, false)
        } else {
            callback.onBack()
        }
    }
}