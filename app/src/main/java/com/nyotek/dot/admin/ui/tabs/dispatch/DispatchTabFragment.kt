package com.nyotek.dot.admin.ui.tabs.dispatch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.NavHostFragment
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.base.BaseFragment
import com.nyotek.dot.admin.databinding.FragmentDispatchTabBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DispatchTabFragment : BaseFragment<FragmentDispatchTabBinding>() {

    private var isLoadFragment: Boolean = false
    private var drawerSelectedItemId = R.id.fleets
    private var navHost: NavHostFragment? = null

    companion object {
        fun newInstance() = DispatchTabFragment()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDispatchTabBinding {
        return FragmentDispatchTabBinding.inflate(inflater, container, false)
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

        navHost = NavHostFragment.create(R.navigation.dispatch)
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_dispatch, navHost!!)
            .setPrimaryNavigationFragment(navHost!!)
            .commit()
    }

    private fun setBackPressedHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navHost?.navController?.popBackStack()
            }
        })
    }
}