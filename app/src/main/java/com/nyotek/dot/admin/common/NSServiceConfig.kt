package com.nyotek.dot.admin.common

import android.app.Activity
import android.view.Gravity
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nyotek.dot.admin.base.BaseViewModel
import com.nyotek.dot.admin.common.extension.visible
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.databinding.LayoutCommonTextBinding
import com.nyotek.dot.admin.databinding.LayoutCommonTextviewBinding
import com.nyotek.dot.admin.databinding.LayoutTagTextBinding
import com.nyotek.dot.admin.ui.tabs.fleets.detail.NSFleetServiceListRecycleAdapter


/**
 * The language class that handles tasks that are common throughout the application services
 */
object NSServiceConfig {

    fun setFleetDetail(
        activity: Activity, layoutName: LayoutCommonTextBinding, layoutUrl: LayoutCommonTextBinding,
        layoutAddress: LayoutCommonTextviewBinding?,
        layoutSlogan: LayoutCommonTextBinding,
        layoutTags: LayoutTagTextBinding,
        tvFill: TextView,
        tvFit: TextView,
        tvEditTitle: TextView,
        rlBrandLogo: RelativeLayout,
        rvServiceList: RecyclerView,
        colorResources: ColorResources, baseViewModel: BaseViewModel
    ): NSFleetServiceListRecycleAdapter {
        val stringResource = colorResources.getStringResource()
        stringResource.apply {
            layoutName.tvCommonTitle.text = name
            layoutUrl.tvCommonTitle.text = url
            layoutAddress?.tvCommonTitle?.text = address
            layoutSlogan.tvCommonTitle.text = slogan
            layoutTags.tvCommonTitle.text = tags
            layoutTags.edtValue.hint = enterTag
            layoutName.rvLanguageTitle.visible()
            layoutSlogan.rvLanguageTitle.visible()
            layoutTags.edtValue.gravity = Gravity.START
            tvFill.text = fill
            tvFit.text = fit
            tvEditTitle.text = edit

            colorResources.setCardBackground(
                rlBrandLogo,
                10f,
                2,
                colorResources.getBackgroundColor(),
                colorResources.getBorderColor()
            )
        }

        return setServiceListAdapter(
            activity,
            rvServiceList,
            layoutName,
            layoutSlogan,
            colorResources, baseViewModel
        )
    }

    private fun setServiceListAdapter(
        activity: Activity,
        rvServiceList: RecyclerView,
        layoutName: LayoutCommonTextBinding,
        layoutSlogan: LayoutCommonTextBinding,
        colorResources: ColorResources, baseViewModel: BaseViewModel
    ): NSFleetServiceListRecycleAdapter {
        rvServiceList.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        val serviceHorizontalAdapter =
            NSFleetServiceListRecycleAdapter(activity, colorResources = colorResources) { serviceId, isChecked ->
                if (isChecked) {
                    baseViewModel.getLocalLanguages(serviceId) {
                        layoutName.rvLanguageTitle.refreshAdapter()
                        layoutSlogan.rvLanguageTitle.refreshAdapter()
                    }
                } else {
                    colorResources.themeHelper.removeMapLocalLanguage(serviceId)
                    layoutName.rvLanguageTitle.refreshAdapter()
                    layoutSlogan.rvLanguageTitle.refreshAdapter()
                }
            }
        rvServiceList.adapter = serviceHorizontalAdapter
        rvServiceList.isNestedScrollingEnabled = false
        return serviceHorizontalAdapter
    }
}