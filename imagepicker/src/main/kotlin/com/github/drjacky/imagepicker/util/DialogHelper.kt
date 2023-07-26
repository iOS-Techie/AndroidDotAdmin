package com.github.drjacky.imagepicker.util

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.github.drjacky.imagepicker.R
import com.github.drjacky.imagepicker.constant.ImageProvider
import com.github.drjacky.imagepicker.databinding.DialogChooseAppBinding
import com.github.drjacky.imagepicker.listener.ResultListener

internal object DialogHelper {

    /**
     * Show Image Provide Picker Dialog. This will streamline the code to pick/capture image
     *
     */
    fun showChooseAppDialog(context: Context, listener: ResultListener<ImageProvider>) {
        val layoutInflater = LayoutInflater.from(context)
        val customView = layoutInflater.inflate(R.layout.dialog_choose_app, null)
        val bind: DialogChooseAppBinding = DialogChooseAppBinding.bind(customView.rootView)
        val dialog = AlertDialog.Builder(context)
            .setTitle(R.string.title_choose_image_provider)
            .setView(customView)
            .setOnCancelListener {
                listener.onResult(null)
            }
            .setNegativeButton(R.string.action_cancel) { _, _ ->
                listener.onResult(null)
            }
            .show()

        // Handle Camera option click
        bind.lytCameraPick.setOnClickListener {
            listener.onResult(ImageProvider.CAMERA)
            dialog.dismiss()
        }

        // Handle Gallery option click
        bind.lytGalleryPick.setOnClickListener {
            listener.onResult(ImageProvider.GALLERY)
            dialog.dismiss()
        }
    }
}
