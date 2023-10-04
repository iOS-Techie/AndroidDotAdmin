package com.nyotek.dot.admin.ui.dispatch.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.nyotek.dot.admin.base.fragment.BaseViewModelFragment
import com.nyotek.dot.admin.common.utils.gone
import com.nyotek.dot.admin.common.utils.invisible
import com.nyotek.dot.admin.common.utils.setSafeOnClickListener
import com.nyotek.dot.admin.common.utils.visible
import com.nyotek.dot.admin.databinding.NsFragmentDispatchDetailBinding

class NSDispatchDetailFragment : BaseViewModelFragment<NSDispatchDetailViewModel, NsFragmentDispatchDetailBinding>() {

    //Get Service list
    override val viewModel: NSDispatchDetailViewModel by lazy {
        ViewModelProvider(this)[NSDispatchDetailViewModel::class.java]
    }

    private var isFragmentLoad = false

    companion object {
        fun newInstance(bundle: Bundle?) = NSDispatchDetailFragment().apply {
            arguments = bundle
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentDispatchDetailBinding {
        return NsFragmentDispatchDetailBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        super.setupViews()
        baseObserveViewModel(viewModel)
        observeViewModel()
    }

    override fun loadFragment(bundle: Bundle?) {
        super.loadFragment(bundle)
        initUI()
        viewCreated()
        setListener()
    }

    override fun observeViewModel() {
        super.observeViewModel()
        with(viewModel) {
            with(binding) {

            }
        }
    }

    private fun initUI() {
        binding.apply {
            stringResource.apply {
                setLayoutHeader(
                    layoutHomeHeader,
                    dispatchManagement,
                    isSearch = false,
                    isBack = true
                )

                tvStatus.text = orderStatus
                tvDriverDetailTitle.text = driverDetail
                tvVehicleDetailTitle.text = vehicleDetails
                tvCustomerDetailTitle.text = customerDetails
                tvVendorDetailTitle.text = vendorDetails
                tvRejectedDetailTitle.text = rejectedTripDriverList
                tvUpdateStatus.text = update
                tvUpdateDriver.text = update
                tvTitleTrack.text = track
                tvDriverId.text = "ID: #908787"
                tvCustomerId.text = "ID: #908787"
                tvVendorId.text = "ID: #908787"

                layoutDriver.apply {
                    layoutName.tvItemTitle.text = name
                    layoutNumber.tvItemTitle.text = number
                    layoutEmail.tvItemTitle.text = emailAddress
                    rlAddress.gone()
                    viewLine.visible()
                }

                layoutDriverSecond.apply {
                    cardImg.invisible()
                    layoutName.tvItemTitle.text = licenseNumber
                    layoutNumber.tvItemTitle.text = age
                    rlEmail.invisible()
                    rlAddress.gone()
                }

                layoutVehicle.apply {
                    layoutName.tvItemTitle.text = name
                    layoutNumber.tvItemTitle.text = model
                    layoutEmail.tvItemTitle.text = registrationNo
                    rlAddress.gone()
                    viewLine.visible()
                }

                layoutVehicleSecond.apply {
                    cardImg.invisible()
                    layoutName.tvItemTitle.text = loadCapacity
                    layoutNumber.tvItemTitle.text = manuYear
                    rlEmail.invisible()
                    rlAddress.gone()
                }

                layoutCustomer.apply {
                    layoutName.tvItemTitle.text = name
                    layoutNumber.tvItemTitle.text = number
                    layoutEmail.tvItemTitle.text = emailAddress
                    layoutAddress.tvItemTitle.text = address
                }

                layoutVendor.apply {
                    layoutName.tvItemTitle.text = name
                    layoutNumber.tvItemTitle.text = number
                    layoutEmail.tvItemTitle.text = emailAddress
                    layoutAddress.tvItemTitle.text = address
                }

                layoutRejectTitle.apply {
                    tvDriverName.text = driverName
                    tvVehicleName.text = vehicle
                    tvDate.text = date
                    tvTime.text = time
                }
            }
        }
    }

    /**
     * View created
     */
    private fun viewCreated() {
        viewModel.apply {

            isFragmentLoad = true
        }
    }

    /**
     * Set listener
     */
    private fun setListener() {
        binding.apply {
            viewModel.apply {
                layoutHomeHeader.apply {
                    ivBack.setSafeOnClickListener {
                        onBackPress()
                    }
                }
            }
        }
    }
}