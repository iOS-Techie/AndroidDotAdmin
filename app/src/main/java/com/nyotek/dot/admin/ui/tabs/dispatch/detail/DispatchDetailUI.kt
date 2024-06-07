package com.nyotek.dot.admin.ui.tabs.dispatch.detail

import android.content.Context
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.NSDateTimeHelper
import com.nyotek.dot.admin.common.extension.gone
import com.nyotek.dot.admin.common.extension.invisible
import com.nyotek.dot.admin.common.extension.setCoilCircle
import com.nyotek.dot.admin.common.extension.visible
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.databinding.LayoutDispatchRequestSentBinding
import com.nyotek.dot.admin.databinding.LayoutOrderStatusBinding
import com.nyotek.dot.admin.databinding.NsFragmentDispatchDetailBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DispatchDetailUI @Inject constructor(private val context: Context, private val binding: NsFragmentDispatchDetailBinding, private val colorResources: ColorResources) {

    init {
        binding.apply {
            setTextViews()
        }
    }

    private fun setTextViews() {
        binding.apply {
            colorResources.apply {
                getStringResource().apply {

                    tvStatus.text = orderStatus
                    tvOrderCancel.text = cancelOrder
                    tvOrderDetailTitle.text = orderDetails
                    tvDriverDetailTitle.text = driverDetail
                    tvVehicleDetailTitleView.text = vehicleDetails
                    tvCustomerDetailTitle.text = customerDetails
                    tvVendorDetailTitle.text = vendorDetails
                    tvRejectedDetailTitle.text = rejectedTripDriverList
                    tvDispatchRequestSent.text = dispatchRequestSent
                    val assignDriver = "$assign $driver"
                    tvAssignDriver.text = assignDriver
                    tvUpdateStatus.text = update
                    tvUpdateDriver.text = update
                    tvTitleTrack.text = track
                    binding.mapFragmentEmployee.removeAllViews()
                    setBackgroundTint(binding.spinnerAssignDriver, getPrimaryColor())

                    layoutOrder.apply {
                        layoutName.tvItemTitle.text = shortDispatchId
                        layoutNumber.tvItemTitle.text = dispatchId
                        layoutEmail.tvItemTitle.text = shortOrderId
                        rlAddress.gone()
                        rlName.visible()
                        rlNumber.visible()
                        rlEmail.visible()
                        viewLine.visible()
                    }

                    layoutDriver.apply {
                        val padding = context.resources.getDimension(R.dimen.icon_padding).toInt()
                        ivIcon.setBackgroundResource(R.drawable.icon_bg)
                        ivIcon.setCoilCircle(R.drawable.ic_driver_handle)
                        ivIcon.setPadding(padding, padding,padding,padding)
                        layoutName.tvItemTitle.text = name
                        layoutNumber.tvItemTitle.text = number
                        layoutEmail.tvItemTitle.text = emailAddress
                        rlAddress.gone()
                        rlName.visible()
                        viewLine.visible()
                    }

                    layoutDriverSecond.apply {
                        cardImg.invisible()
                        rlName.visible()
                        layoutName.tvItemTitle.text = licenseNumber
                        layoutNumber.tvItemTitle.text = age
                        rlNumber.gone()
                        rlEmail.invisible()
                        rlAddress.gone()
                    }

                    layoutVehicle.apply {
                        rlName.visible()
                        rlNumber.visible()
                        layoutName.tvItemTitle.text = name
                        layoutNumber.tvItemTitle.text = model
                        layoutEmail.tvItemTitle.text = loadCapacity
                        rlAddress.gone()
                        viewLine.visible()
                    }

                    layoutVehicleSecond.apply {
                        cardImg.invisible()
                        rlName.visible()
                        rlNumber.visible()
                        layoutName.tvItemTitle.text = registrationNo
                        layoutNumber.tvItemTitle.text = year
                        rlAddress.gone()
                    }

                    layoutCustomer.apply {
                        rlName.visible()
                        rlNumber.visible()
                        rlAddress.visible()
                        layoutName.tvItemTitle.text = name
                        layoutNumber.tvItemTitle.text = number
                        layoutAddress.tvItemTitle.text = address
                    }

                    layoutVendor.apply {
                        rlName.visible()
                        rlAddress.visible()
                        layoutName.tvItemTitle.text = name
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
    }

    fun setOrderStatusUI(bind: LayoutOrderStatusBinding, isSelected: Boolean) {
        bind.apply {
            colorResources.apply {
                view2.setBackgroundResource(R.drawable.status_bg)

                if (isSelected) {
                    tvStatusTitle.setTextColor(getPrimaryColor())
                    setBackground(view1, getPrimaryColor())
                    setBackground(view2, getPrimaryColor())
                } else {
                    setBackground(view1, getTabSecondaryColor())
                    setBackground(view2, getTabSecondaryColor())
                    tvStatusTitle.alpha = 0.5f
                }
            }
        }
    }

    fun setDispatchRequestSentUI(bind: LayoutDispatchRequestSentBinding) {
        bind.apply {
            colorResources.apply {
                getStringResource().apply {
                    layoutTop.apply {
                        rlName.visible()
                        rlNumber.visible()
                        rlEmail.visible()

                        layoutName.tvItemTitle.text = driverName
                        layoutNumber.tvItemTitle.text = distanceKm
                        layoutEmail.tvItemTitle.text = status
                    }

                    layoutBottom.apply {
                        rlName.visible()
                        rlNumber.visible()
                        rlEmail.visible()

                        layoutName.tvItemTitle.text = date
                        layoutNumber.tvItemTitle.text = notificationCreated
                        layoutEmail.tvItemTitle.text = notificationExpired
                    }
                }
            }
        }
    }
}