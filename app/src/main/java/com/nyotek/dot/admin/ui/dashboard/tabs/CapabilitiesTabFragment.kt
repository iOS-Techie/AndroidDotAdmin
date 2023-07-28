package com.nyotek.dot.admin.ui.dashboard.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nyotek.dot.admin.common.NSFragment
import com.nyotek.dot.admin.common.callbacks.NSBackClickCallback
import com.nyotek.dot.admin.common.callbacks.NSOnPageChangeCallback
import com.nyotek.dot.admin.common.utils.setPager
import com.nyotek.dot.admin.databinding.FragmentCapabilitiesTabBinding
import com.nyotek.dot.admin.ui.capabilities.NSCapabilitiesFragment

class CapabilitiesTabFragment : NSFragment() {

    private var _binding: FragmentCapabilitiesTabBinding? = null
    private val binding get() = _binding!!
    private val mFragmentList: MutableList<Fragment> = ArrayList()
    private var pageIndex = 0
    private var isFragmentLoad: Boolean = false

    companion object {
        fun newInstance() = CapabilitiesTabFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCapabilitiesTabBinding.inflate(inflater, container, false)
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
        mFragmentList.add(NSCapabilitiesFragment.newInstance())
        binding.capabilitiesPager.setPager(
            requireActivity(),
            mFragmentList,
            object : NSOnPageChangeCallback {
                override fun onPageChange(position: Int) {
                    pageIndex = position
                    val fragment = mFragmentList[position]
                    if (fragment is NSCapabilitiesFragment) {
                        fragment.loadFragment()
                    }
                }
            })
    }

    fun onBackClick(callback: NSBackClickCallback) {
        if (pageIndex > 0) {
            pageIndex -= 1
            binding.capabilitiesPager.setCurrentItem(pageIndex, false)
        } else {
            callback.onBack()
        }
    }
}