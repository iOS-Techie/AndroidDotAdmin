package com.nyotek.dot.admin.repository.network.callbacks

/**
 * The callback for communication between the view model and repository
 */
interface NSGenericViewModelCallback {
    /**
     * This method will be called when the web service call is success.
     *
     * @param data The data retrieved from the web service
     * @param <T>  The type of data
    </T> */
    fun <T> onSuccess(data: T)

    /**
     * This method will be called when there is an error thrown by the web service.
     *
     * @param errors The errors from which the error message to display to the user will be constructed
     */
    fun onError(errors: List<Any>)

    /**
     * This method will be called when there is a failure in the web service call.
     */
    fun onFailure(failureMessage: String?)

    /**
     * This method will be called when there is no network while making the web service call.
     *
     * @param localData The data stored in the local database
     * @param <T>       The type of data
    </T> */
    fun <T> onNoNetwork(localData: T)
}