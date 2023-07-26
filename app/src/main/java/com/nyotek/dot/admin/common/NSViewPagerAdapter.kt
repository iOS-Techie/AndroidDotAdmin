package com.nyotek.dot.admin.common

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class NSViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    private var mFragmentList: MutableList<Fragment> = ArrayList()

    fun setFragment(mFragmentList: MutableList<Fragment>) {
        this.mFragmentList = mFragmentList
    }

    override fun createFragment(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getItemCount(): Int {
        return mFragmentList.size
    }
}