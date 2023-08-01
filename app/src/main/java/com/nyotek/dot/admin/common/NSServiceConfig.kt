package com.nyotek.dot.admin.common

import android.app.Activity
import android.view.Gravity
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.common.utils.NSUtilities
import com.nyotek.dot.admin.common.utils.isValidList
import com.nyotek.dot.admin.common.utils.visible
import com.nyotek.dot.admin.databinding.LayoutCommonTextBinding
import com.nyotek.dot.admin.databinding.LayoutCommonTextviewBinding
import com.nyotek.dot.admin.databinding.LayoutTagTextBinding
import com.nyotek.dot.admin.ui.fleets.detail.NSFleetServiceListRecycleAdapter


/**
 * The language class that handles tasks that are common throughout the application languages
 */
object NSServiceConfig {

    fun setFleetDetail(activity: Activity, isDialog: Boolean, layoutName: LayoutCommonTextBinding,
                       layoutUrl: LayoutCommonTextBinding,
                       layoutAddress: LayoutCommonTextviewBinding?,
                       layoutSlogan: LayoutCommonTextBinding,
                       layoutTags: LayoutTagTextBinding,
                       tvFill: TextView,
                       tvFit: TextView,
                       tvEditTitle: TextView,
                       rlBrandLogo: RelativeLayout,
                       rvServiceList: RecyclerView): NSFleetServiceListRecycleAdapter {
        val stringResource = NSApplication.getInstance().getStringModel()
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

            ColorResources.setCardBackground(
                rlBrandLogo,
                10f,
                2,
                ColorResources.getBackgroundColor(),
                ColorResources.getBorderColor()
            )
        }

        return setServiceListAdapter(activity, isDialog, rvServiceList, layoutName, layoutSlogan)
    }

    private fun setServiceListAdapter(activity: Activity, isDialog: Boolean, rvServiceList: RecyclerView, layoutName: LayoutCommonTextBinding, layoutSlogan: LayoutCommonTextBinding): NSFleetServiceListRecycleAdapter {
        rvServiceList.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        val serviceHorizontalAdapter =
            NSFleetServiceListRecycleAdapter(activity, isDialog) { serviceId, isChecked ->
                if (isChecked) {
                    NSUtilities.localLanguageApiCall(serviceId) {
                        layoutName.rvLanguageTitle.refreshAdapter()
                        layoutSlogan.rvLanguageTitle.refreshAdapter()
                    }
                } else {
                    //NSLanguageRepository.clearApiCall()
                    NSApplication.getInstance().removeMapLocalLanguage(serviceId)
                    layoutName.rvLanguageTitle.refreshAdapter()
                    layoutSlogan.rvLanguageTitle.refreshAdapter()
                }
            }
        rvServiceList.adapter = serviceHorizontalAdapter
        rvServiceList.isNestedScrollingEnabled = false
        return serviceHorizontalAdapter
    }

    fun getListFromLocal(list: MutableList<String>, callback: () -> Unit) {
        if (list.isValidList()) {
            NSUtilities.localLanguageApiCall(list[0]) {
                val newList: MutableList<String> = arrayListOf()
                newList.addAll(list)
                newList.removeAt(0)
                getListFromLocal(newList, callback)
            }
        } else {
            callback.invoke()
        }
    }
}