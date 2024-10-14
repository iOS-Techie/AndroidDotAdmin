package com.nyotek.dot.admin.ui.tabs.fleets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.base.BaseFragment
import com.nyotek.dot.admin.common.event.EventHelper
import com.nyotek.dot.admin.databinding.FragmentFleetsTabBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FleetsTabFragment : BaseFragment<FragmentFleetsTabBinding>() {

    private var isLoadFragment: Boolean = false
    private var drawerSelectedItemId = R.id.fleets
    private var navHost: NavHostFragment? = null
    val eventViewModel = EventHelper.getEventViewModel()
    
    companion object {
        fun newInstance() = FleetsTabFragment()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFleetsTabBinding {
        return FragmentFleetsTabBinding.inflate(inflater, container, false)
    }

    override fun loadFragment() {
        super.loadFragment()
        if (!isLoadFragment) {
            isLoadFragment = true
            setupBottomNavigationBar()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackPressedHandler()
    }

    private fun setupBottomNavigationBar() {
        navHost = NavHostFragment.create(R.navigation.fleets)
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_fleets, navHost!!)
            .setPrimaryNavigationFragment(navHost!!)
            .commit()
    }


    private fun setBackPressedHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
               findNavController().popBackStack()
            }
        })
    }
    
    override fun onResume() {
        super.onResume()
        eventViewModel.resumeEvent(true)
    }
}