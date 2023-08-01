package com.nyotek.dot.admin.ui.dashboard.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nyotek.dot.admin.common.NSFragment
import com.nyotek.dot.admin.common.callbacks.NSBackClickCallback
import com.nyotek.dot.admin.common.utils.setPager
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

        binding.servicesPager.setPager(
            requireActivity(),
            mFragmentList) {
            pageIndex = it
            val fragment = mFragmentList[it]
            if (fragment is NSServiceManagementFragment) {
                fragment.loadFragment()
            }
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