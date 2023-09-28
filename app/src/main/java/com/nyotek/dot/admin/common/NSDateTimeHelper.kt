package com.nyotek.dot.admin.common

import com.nyotek.dot.admin.repository.network.responses.StringResourceResponse
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * The date and time helper class that handles all date and time related operations
 */
object NSDateTimeHelper {
    private val TAG = NSDateTimeHelper::class.java.simpleName
    private const val DATE_FORMAT_FROM_API = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private const val DATE_FORMAT_ORDER_SHOW = "dd/MM/yyyy | hh:mm a"
    private const val DATE_FOR_SORTING = "yyyy-MM-dd'T'HH:mm:ss"
    private const val DATE_FORMAT_CURRENT_TIME = "dd-MM-yyyy"
    private const val DATE_FORMAT_USER = "dd/MM/yyyy"
    private const val DATE_FORMAT_SERVICE_HEADER = "dd/MM/yyyy"
    private const val DATE_FORMAT_FROM_API_TRANSACTION = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private const val DATE_FORMAT_USER_DETAIL = "MMM dd, yyyy hh:mm:ss a"

    /**
     * To convert the input date string to expected output pattern
     *
     * @param dateString date that need to convert to output pattern
     * @param inputPattern pattern of input date string
     * @param outputPattern output pattern of date string
     * @return date string
     */
    @Suppress("SameParameterValue")
    private fun getConvertedDate(
        dateString: String?, inputPattern: String, outputPattern: String): String {
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat(inputPattern, Locale.ENGLISH)
        format.timeZone = TimeZone.getTimeZone("UTC")
        val date: Date?
        try {
            date = dateString?.let { format.parse(it) }
            date?.let {
                calendar.time = it
            }
        } catch (exception: ParseException) {
            NSLog.e(TAG, "getConvertedDate : Caught Exception ", exception)
        }
        val newFormat = SimpleDateFormat(outputPattern, Locale.ENGLISH)
        format.timeZone = TimeZone.getTimeZone("UTC")
        return newFormat.format(calendar.time)
    }

    private fun getConvertedDateInDate(
        dateString: String?, inputPattern: String, outputPattern: String): Date {
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat(inputPattern, Locale.ENGLISH)
        format.timeZone = TimeZone.getTimeZone("UTC")
        var date: Date? = null
        try {
            date = dateString?.let { format.parse(it) }
            date?.let {
                calendar.time = it
            }
        } catch (exception: ParseException) {
            NSLog.e(TAG, "getConvertedDate : Caught Exception ", exception)
        }
        return date?:Date()
    }

    /**
     * To convert the input date string to expected output pattern
     *
     * @param dateString date that need to convert to output pattern
     * @return date string
     */
    fun getDateValue(
        dateString: String?): Date? {
        val format = SimpleDateFormat(DATE_FOR_SORTING, Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")
        val date: Date? = try {
            dateString?.let { format.parse(it) }
        } catch (exception: ParseException) {
            null
        }
        return date
    }

    /**
     * To get the Delivery time string for view
     *
     * @param dateString The date string
     */
    fun getServiceDateView(dateString: String?) =
        getConvertedDate(dateString, DATE_FORMAT_FROM_API, DATE_FORMAT_SERVICE_HEADER)

    /**
     * To get the Delivery time string for view
     *
     * @param dateString The date string
     */
    fun getOrderDateView(dateString: String?) =
        getConvertedDate(dateString, DATE_FORMAT_FROM_API, DATE_FORMAT_ORDER_SHOW)


    /**
     * To get the time string for view
     *
     * @param dateString The date string
     */
    fun getCommonDateView(dateString: String?) =
        getConvertedDateInDate(dateString, DATE_FORMAT_FROM_API, DATE_FORMAT_ORDER_SHOW)

    /**
     * To get the current dateTime string
     *
     */
    fun getCurrentDate(): Date {
        return Date()
    }


    fun getConvertedDate(date: Date): String {
        val sdf = SimpleDateFormat(DATE_FORMAT_CURRENT_TIME, Locale.getDefault())
        return sdf.format(date)
    }

    /**
     * To get the previous dateTime string
     *
     */
    fun getNextPreviousDate(date: Date, increaseDecrease: Int): Date {
        val c = Calendar.getInstance()
        c.time = date
        c.add(Calendar.DATE, increaseDecrease)
        return c.time
    }

    /**
     * To get the transaction time string for view
     *
     * @param dateString The date string
     */
    fun getDateTimeForView(dateString: String?) =
        getConvertedDate(dateString, DATE_FORMAT_FROM_API_TRANSACTION, DATE_FORMAT_ORDER_SHOW)

    /**
     * To get the transaction time string for view
     *
     * @param dateString The date string
     */
    fun getDateTimeForUserView(dateString: String?) =
        getConvertedDate(dateString, DATE_FORMAT_FROM_API_TRANSACTION, DATE_FORMAT_USER_DETAIL)

    /**
     * To get the date string for view
     *
     * @param dateString The date string
     */
    fun getDateForUser(dateString: String?) =
        getConvertedDate(dateString, DATE_FORMAT_FROM_API_TRANSACTION, DATE_FORMAT_USER)

    fun formatDateToNowOrDateTime(inputDateString: String): String {
        val stringResource = StringResourceResponse()
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
        val inputDate = inputFormat.parse(inputDateString)
        val currentTime = System.currentTimeMillis()
        val dateDifference = currentTime - inputDate.time
        val secondsDifference = dateDifference / 1000

        return when {
            secondsDifference < 60 -> stringResource.justNow // Less than a minute ago
            secondsDifference < 3600 -> "${secondsDifference / 60}m ago" // Less than an hour ago
            secondsDifference < 86400 -> "${secondsDifference / 3600}h ago" // Less than a day ago
            else -> SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(inputDate) // Default format for older dates
        }
    }
}