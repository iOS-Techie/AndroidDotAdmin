package com.nyotek.dot.admin.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.nyotek.dot.admin.common.NSFragment

abstract class BaseViewModelFragment<VM : ViewModel, VB : ViewBinding> : NSFragment() {

    protected abstract val viewModel: VM
    private var _binding: VB? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = getFragmentBinding(inflater, container)
        return binding.root
    }

    abstract fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): VB

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
    }

    open fun setupViews() {
        // Perform any common view setup here
    }

    open fun observeViewModel() {
        // Observe any LiveData or perform ViewModel-related setup here
    }

    open fun loadFragment() {
        // Perform any fragment-specific setup here
    }

    open fun loadFragment(bundle: Bundle?) {
        // Perform any fragment-specific setup here
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}