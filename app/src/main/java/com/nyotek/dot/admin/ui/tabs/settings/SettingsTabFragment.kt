package com.nyotek.dot.admin.ui.tabs.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.NavHostFragment
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.base.BaseFragment
import com.nyotek.dot.admin.databinding.FragmentSettingsTabBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsTabFragment : BaseFragment<FragmentSettingsTabBinding>() {

    private var isLoadFragment: Boolean = false
    private var navHost: NavHostFragment? = null

    companion object {
        fun newInstance() = SettingsTabFragment()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingsTabBinding {
        return FragmentSettingsTabBinding.inflate(inflater, container, false)
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
        navHost = NavHostFragment.create(R.navigation.settings)
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_settings, navHost!!)
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