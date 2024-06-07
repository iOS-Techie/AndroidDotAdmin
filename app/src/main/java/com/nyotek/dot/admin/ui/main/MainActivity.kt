package com.nyotek.dot.admin.ui.main

import android.os.Bundle
import com.nyotek.dot.admin.base.NSActivity
import com.nyotek.dot.admin.databinding.ActivityMainBinding


class MainActivity : NSActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadInitialFragment()
    }

    private fun loadInitialFragment() {

    }
}