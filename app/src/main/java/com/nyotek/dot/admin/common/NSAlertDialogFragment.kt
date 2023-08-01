package com.nyotek.dot.admin.common

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.NSConstants.Companion.SESSION_EXPIRED
import com.nyotek.dot.admin.common.NSConstants.Companion.SESSION_EXPIRED_ERROR
import com.nyotek.dot.admin.common.callbacks.NSDialogClickCallback
import com.nyotek.dot.admin.common.utils.visible
import com.nyotek.dot.admin.databinding.LayoutCustomAlertDialogBinding
import org.greenrobot.eventbus.EventBus

/**
 * Dialog fragment responsible for showing the alert
 */
class NSAlertDialogFragment : DialogFragment() {
    private lateinit var mContext: Context

    companion object {
        private const val BUNDLE_KEY_TITLE = "title"
        private const val BUNDLE_KEY_MESSAGE = "message"
        private const val BUNDLE_KEY_POSITIVE_BUTTON_TEXT = "positiveButtonText"
        private const val BUNDLE_KEY_NEGATIVE_BUTTON_TEXT = "negativeButtonText"
        private const val BUNDLE_KEY_IS_CANCEL_NEEDED = "isCancelNeeded"
        private const val BUNDLE_KEY_ALERT_KEY = "alertKey"
        var stringResource = NSApplication.getInstance().getStringModel()
        private var callback: NSDialogClickCallback? = null

        fun newInstance(
            title: String?,
            message: String,
            isCancelNeeded: Boolean,
            negativeButtonText: String?,
            positiveButtonText: String?,
            alertKey: String?,
            dialogCallback: NSDialogClickCallback?
        ) = NSAlertDialogFragment().apply {
            callback = dialogCallback
            arguments = bundleOf(
                BUNDLE_KEY_TITLE to title,
                BUNDLE_KEY_MESSAGE to message,
                BUNDLE_KEY_IS_CANCEL_NEEDED to isCancelNeeded,
                BUNDLE_KEY_NEGATIVE_BUTTON_TEXT to negativeButtonText,
                BUNDLE_KEY_POSITIVE_BUTTON_TEXT to positiveButtonText,
                BUNDLE_KEY_ALERT_KEY to alertKey
            )
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val view: View =
            requireActivity().layoutInflater.inflate(R.layout.layout_custom_alert_dialog, null)
        builder.setView(view)
        val bind: LayoutCustomAlertDialogBinding = LayoutCustomAlertDialogBinding.bind(view)
        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val arguments = arguments
        arguments?.let {
            bind.apply {
                stringResource.apply {
                    tvTitle.text = success
                    tvSubTitle.text = contactUs
                    tvCancel.text = cancel.ifEmpty { requireActivity().resources.getString(R.string.cancel) }
                    tvOk.text = ok.ifEmpty { requireActivity().resources.getString(R.string.ok) }
                }
            }

            //title
            val title = arguments.getString(BUNDLE_KEY_TITLE, "")
            bind.tvTitle.text = title
            bind.tvTitle.visible()
            if (title.isNullOrEmpty()) {
                bind.tvTitle.text = requireActivity().resources.getString(R.string.app_admin_name)
            }

            var positiveButtonText = arguments.getString(BUNDLE_KEY_POSITIVE_BUTTON_TEXT, "")
            if (positiveButtonText.isNullOrEmpty()) {
                positiveButtonText = stringResource.ok.ifEmpty { requireActivity().resources.getString(R.string.ok) }
            }

            //supporting text
            val message = arguments.getString(BUNDLE_KEY_MESSAGE, "")
            if (message.equals(SESSION_EXPIRED)) {
                bind.tvSubTitle.text = SESSION_EXPIRED_ERROR
                positiveButtonText = stringResource.logout
            } else {
                bind.tvSubTitle.text = message
            }

            //positive button
            bind.tvOk.text = positiveButtonText
            bind.tvOk.setOnClickListener {
                dialog.dismiss()
                if (message.equals(SESSION_EXPIRED)) {
                    EventBus.getDefault().post(NSLogoutEvent())
                } else {
                    callback?.onDialog(false)
                }
            }

            val isCancelButtonNeeded = arguments.getBoolean(BUNDLE_KEY_IS_CANCEL_NEEDED, false)
            if (isCancelButtonNeeded) {
                var negativeButtonText = arguments.getString(BUNDLE_KEY_NEGATIVE_BUTTON_TEXT, "")
                if (negativeButtonText.isNullOrEmpty()) {
                    negativeButtonText = stringResource.cancel.ifEmpty { requireActivity().resources.getString(R.string.cancel) }
                }

                //negative button
                bind.viewLine2.visible()
                bind.tvCancel.visible()
                bind.tvCancel.text = negativeButtonText
                bind.tvCancel.setOnClickListener {
                    dialog.dismiss()
                    callback?.onDialog(true)
                }
            }
            isCancelable = false
        }
        return dialog
    }
}