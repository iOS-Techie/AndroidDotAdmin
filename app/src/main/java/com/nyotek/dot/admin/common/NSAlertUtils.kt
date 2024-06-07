package com.nyotek.dot.admin.common

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

/**
 * The class which performs all alert related operations
 */
class NSAlertUtils {
    companion object {
        private val TAG: String = NSAlertUtils::class.java.simpleName
        private const val ALERT_DIALOG_TAG = "alert_dialog"

        /**
         * To show alert dialog with given title and message along with their buttons
         *
         * @param title              The title to be displayed in the alert
         * @param message            The message to be displayed in the alert
         * @param isCancelNeeded     boolean to specify whether negative(Cancel) button is needed or not
         * @param negativeButtonText The text for the negative button
         * @param positiveButtonText The text for the positive button
         * @param fragmentActivity   The instance of the fragmentActivity needed to remove the existing dialog & to show this dialog
         */
        fun showAlertDialog(
            fragmentActivity: FragmentActivity,
            message: String,
            title: String? = null,
            isCancelNeeded: Boolean = false,
            positiveButtonText: String? = null,
            negativeButtonText: String? = null,
            shouldRemoveExistingDialog: Boolean = true,
            alertTag: String = ALERT_DIALOG_TAG,
            callback: ((Boolean) -> Unit),
            sessionCallback: ((Boolean) -> Unit)
        ) {
            try {
                if (shouldRemoveExistingDialog) {
                    removeExistingDialog(fragmentActivity)
                }
                val alertDialogFragment: NSAlertDialogFragment = NSAlertDialogFragment.newInstance(
                    title, message, isCancelNeeded, negativeButtonText, positiveButtonText, callback, sessionCallback
                )
                alertDialogFragment.show(fragmentActivity.supportFragmentManager, alertTag)
            } catch (exception: Exception) {
                NSLog.e(TAG, "showAlertDialog: Caught exception: ", exception)
            }
        }

        /**
         * To remove the existing dialog shown
         *
         * @param fragmentActivity The instance of the fragmentActivity needed to get the fragmentManager
         */
        private fun removeExistingDialog(fragmentActivity: FragmentActivity) {
            try {
                val fragmentManager: FragmentManager = fragmentActivity.supportFragmentManager
                val fragment: Fragment? = fragmentManager.findFragmentByTag(ALERT_DIALOG_TAG)
                fragment?.let { fragmentManager.beginTransaction().remove(it).commit() }
            } catch (exception: Exception) {
                NSLog.e(TAG, "removeExistingDialog: Caught exception: ", exception)
            }
        }
    }
}