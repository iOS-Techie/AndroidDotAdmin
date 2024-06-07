package com.nyotek.dot.admin.common.extension

import android.content.Intent
import android.os.Bundle
import android.util.SparseArray
import android.view.MenuItem
import androidx.core.util.containsKey
import androidx.core.util.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nyotek.dot.admin.ui.tabs.dispatch.DispatchFragmentDirections

fun NavController.clearBackStack() {
    popBackStack(graph.startDestinationId, false)
}

fun NavController.navigateSafe(directions: NavDirections) {
    currentDestination?.getAction(directions.actionId)?.let { navigate(directions) }
}

fun NavController.navigateSafeNew(directions: NavDirections) {
    val currentDestinationId = currentDestination?.id
    if (currentDestinationId != null) {
        navigate(directions)
    }
}

fun BottomNavigationView.setupWithNavController(
    fragmentManager: FragmentManager,
    navGraphIds: List<Int>,
    containerId: Int,
    currentItemId: Int,
    intent: Intent
): LiveData<NavController> {
    // Map of tags
    val graphIdToTagMap = SparseArray<String>()

    // Result. Mutable live data with the selected controller
    val selectedNavController = MutableLiveData<NavController>()

    // First create a NavHostFragment for each NavGraph ID
    navGraphIds.forEachIndexed { index, navGraphId ->
        val fragmentTag = getFragmentTag(index)

        // Find or create the Navigation host fragment
        val navHostFragment = obtainNavHostFragment(
            fragmentManager,
            fragmentTag,
            navGraphId,
            containerId
        )

        // Obtain its id
        val graphId = navHostFragment.navController.graph.id

        // Save to the map
        graphIdToTagMap[graphId] = fragmentTag

        // Attach or detach nav host fragment depending on whether it's the selected item
        if (graphId == currentItemId) {
            selectedNavController.value = navHostFragment.navController
            attachNavHostFragment(fragmentManager, navHostFragment)
        } else {
            detachNavHostFragment(fragmentManager, navHostFragment)
        }
    }

    setOnItemSelectedListener { menuItem ->
        if (fragmentManager.isStateSaved) {
            false
        } else {
            val newItemId = menuItem.itemId
            if (!graphIdToTagMap.containsKey(newItemId)) {
                // Optional: if the selected item is meant to be a destination separate
                // to the supplied graphs, navigate to it from the parent navController
                //parentNavController.navigate(newItemId)
                return@setOnItemSelectedListener true
            }

            val newlySelectedItemTag = graphIdToTagMap[newItemId]
            val selectedFragment =
                fragmentManager.findFragmentByTag(newlySelectedItemTag) as NavHostFragment

            // Optional: When the already selected item is re-selected
            // You can also add a call to hideDrawer() if desired
            if (selectedItemId == newItemId) {
                return@setOnItemSelectedListener popToStart(selectedFragment)
            }

            showSelectedFragment(
                fragmentManager,
                selectedNavController,
                selectedFragment,
                graphIdToTagMap
            )
            //hideDrawer()
            true
        }
    }

    // Optional: handle deep links
    setupDeepLinks(
        fragmentManager,
        selectedNavController,
        graphIdToTagMap,
        navGraphIds,
        containerId,
        intent
    )

    return selectedNavController
}

private fun BottomNavigationView.popToStart(selectedFragment: NavHostFragment): Boolean {
    val navController = selectedFragment.navController
    val startDestination = navController.graph.startDestinationId
    val currentDestination = navController.currentDestination?.id
    // Check if the current destination is not the start destination
    if (currentDestination != startDestination) {
        // Navigate to the start destination without adding to the back stack
        navController.navigate(startDestination)
        return true
    }
    return false
}

private fun BottomNavigationView.setupDeepLinks(
    fragmentManager: FragmentManager,
    selectedNavController: MutableLiveData<NavController>,
    graphIdToTagMap: SparseArray<String>,
    navGraphIds: List<Int>,
    containerId: Int,
    intent: Intent
) {
    navGraphIds.forEachIndexed { index, navGraphId ->
        val fragmentTag = getFragmentTag(index)

        // Find or create the Navigation host fragment
        val navHostFragment = obtainNavHostFragment(
            fragmentManager,
            fragmentTag,
            navGraphId,
            containerId
        )
        // Handle Intent
        val graphId = navHostFragment.navController.graph.id
        if (navHostFragment.navController.handleDeepLink(intent)
            && selectedItemId != graphId
        ) {
            selectedItemId = graphId
            val selectedTag = graphIdToTagMap[graphId]
            showSelectedFragment(
                fragmentManager,
                selectedNavController,
                fragmentManager.findFragmentByTag(selectedTag) as NavHostFragment,
                graphIdToTagMap
            )
        }
    }
}

private fun showSelectedFragment(
    fragmentManager: FragmentManager,
    selectedNavController: MutableLiveData<NavController>,
    selectedFragment: NavHostFragment,
    graphIdToTagMap: SparseArray<String>
) {
    /*fragmentManager.beginTransaction()
        .setCustomAnimations(
            androidx.navigation.ui.R.anim.nav_default_enter_anim,
            androidx.navigation.ui.R.anim.nav_default_exit_anim,
            androidx.navigation.ui.R.anim.nav_default_pop_enter_anim,
            androidx.navigation.ui.R.anim.nav_default_pop_exit_anim
        )
        .attach(selectedFragment)
        .setPrimaryNavigationFragment(selectedFragment)
        .apply {
            // Detach all other Fragments
            graphIdToTagMap.forEach { _, fragmentTag ->
                if (fragmentTag != selectedFragment.tag) {
                    detach(fragmentManager.findFragmentByTag(fragmentTag)!!)
                }
            }
        }
        .setReorderingAllowed(true)
        .commit()
    selectedNavController.value = selectedFragment.navController*/

    fragmentManager.beginTransaction()
        .setCustomAnimations(
            androidx.navigation.ui.R.anim.nav_default_enter_anim,
            androidx.navigation.ui.R.anim.nav_default_exit_anim,
            androidx.navigation.ui.R.anim.nav_default_pop_enter_anim,
            androidx.navigation.ui.R.anim.nav_default_pop_exit_anim
        )
        .hideAllFragmentsExcept(fragmentManager, selectedFragment)
        .commitNow()

    selectedNavController.value = selectedFragment.navController
}

private fun FragmentTransaction.hideAllFragmentsExcept(
    fragmentManager: FragmentManager,
    selectedFragment: Fragment
): FragmentTransaction {
    fragmentManager.fragments.forEach { fragment ->
        if (fragment != selectedFragment) {
            hide(fragment)
        }
    }
    return this
}

private fun detachNavHostFragment(
    fragmentManager: FragmentManager,
    navHostFragment: NavHostFragment
) {
    fragmentManager.beginTransaction()
        .detach(navHostFragment)
        .commitNow()
}

private fun attachNavHostFragment(
    fragmentManager: FragmentManager,
    navHostFragment: NavHostFragment
) {
    fragmentManager.beginTransaction()
        .attach(navHostFragment)
        .apply {
            setPrimaryNavigationFragment(navHostFragment)
        }
        .commitNow()
}

private fun obtainNavHostFragment(
    fragmentManager: FragmentManager,
    fragmentTag: String,
    navGraphId: Int,
    containerId: Int
): NavHostFragment {
    // If the Nav Host fragment exists, return it
    val existingFragment = fragmentManager.findFragmentByTag(fragmentTag) as NavHostFragment?
    existingFragment?.let { return it }

    // Otherwise, create it and return it.
    val navHostFragment = NavHostFragment.create(navGraphId)
    fragmentManager.beginTransaction()
        .add(containerId, navHostFragment, fragmentTag)
        .commitNow()
    return navHostFragment
}

private fun getFragmentTag(index: Int) = "navigationView#$index"

private fun FragmentManager.isOnBackStack(backStackName: String): Boolean {
    val backStackCount = backStackEntryCount
    for (index in 0 until backStackCount) {
        if (getBackStackEntryAt(index).name == backStackName) {
            return true
        }
    }
    return false
}

val FragmentManager.currentNavigationFragment: Fragment?
    get() = primaryNavigationFragment?.childFragmentManager?.fragments?.first()