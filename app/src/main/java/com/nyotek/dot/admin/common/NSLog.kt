package com.nyotek.dot.admin.common

import android.util.Log

/**
 * The class to print the logs in the logcat
 */
class NSLog {
    companion object {
        /**
         * Verbose level logcat message (only output for debug builds)
         *
         * @param tag     tag
         * @param message message
         */
        fun v(tag: String?, message: String) {
            Log.v(tag, message)
        }

        /**
         * Debug level logcat message (only output for debug builds)
         *
         * @param tag     tag
         * @param message message
         */
        fun d(tag: String?, message: String) {
            Log.d(tag, message)
        }

        /**
         * Information level logcat message (always output)
         *
         * @param tag     tag
         * @param message message
         */
        fun i(tag: String?, message: String) {
            Log.i(tag, message)
        }

        /**
         * Warning level logcat message (always output)
         *
         * @param tag     tag
         * @param message message
         */
        fun w(tag: String?, message: String) {
            Log.w(tag, message)
        }

        /**
         * Error level logcat message (always output)
         *
         * @param tag     tag
         * @param message message
         */
        fun e(tag: String?, message: String) {
            Log.e(tag, message)
        }

        /**
         * Error level logcat message (always output)
         *
         * @param tag     tag
         * @param message message
         * @param throwable throwable to get the error message
         */
        fun e(tag: String?, message: String, throwable: Throwable) {
            val exceptionDetails = throwable.javaClass.simpleName + " - " + throwable.message
            Log.e(tag, exceptionDetails)
            Log.e(tag, message)
        }
    }
}