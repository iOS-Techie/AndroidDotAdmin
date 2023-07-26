package com.nyotek.dot.admin.repository

import com.nyotek.dot.admin.repository.network.callbacks.NSGenericViewModelCallback
import com.nyotek.dot.admin.repository.network.callbacks.NSRetrofitCallback
import com.nyotek.dot.admin.repository.network.error.NSApiErrorHandler
import com.nyotek.dot.admin.repository.network.requests.NSAddEmployeeRequest
import com.nyotek.dot.admin.repository.network.requests.NSEmployeeEditRequest
import com.nyotek.dot.admin.repository.network.requests.NSEmployeeRequest
import com.nyotek.dot.admin.repository.network.requests.NSEmployeeListRequest
import com.nyotek.dot.admin.repository.network.responses.NSEmployeeBlankDataResponse
import com.nyotek.dot.admin.repository.network.responses.NSEmployeeAddDeleteBlankDataResponse
import com.nyotek.dot.admin.repository.network.responses.NSEmployeeResponse
import com.nyotek.dot.admin.repository.network.responses.NSListJobTitleResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

/**
 * Repository class to handle data operations related to employee
 */
object NSEmployeeRepository : BaseRepository() {

    /**
     * To get job title API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getJobTitle(serviceId: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            apiManager.getJobTitle(serviceId, object :
                NSRetrofitCallback<NSListJobTitleResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_JOB_TITLE
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body())
                    }
                }

                override fun onRefreshToken() {
                    getJobTitle(serviceId, viewModelCallback)
                }
            })
        }
    }

    /**
     * To get employee data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getEmployeeList(
        vendorId: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val employeeRequest = NSEmployeeListRequest(vendorId)
            apiManager.getEmployeeList(employeeRequest, object :
                NSRetrofitCallback<NSEmployeeResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_EMPLOYEE_LIST
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body())
                    }
                }

                override fun onRefreshToken() {
                    getEmployeeList(vendorId, viewModelCallback)
                }
            })
        }
    }

    /**
     * To get wallet transaction data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun enableDisableEmployee(
        vendorId: String,
        userId: String,
        isServiceEnable: Boolean,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val request = NSEmployeeRequest(vendorId, userId)
            val callback = object :
                NSRetrofitCallback<NSEmployeeBlankDataResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_EMPLOYEE_ENABLE_DISABLE
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?: NSEmployeeBlankDataResponse())
                    }
                }

                override fun onRefreshToken() {
                    enableDisableEmployee(vendorId, userId, isServiceEnable, viewModelCallback)
                }
            }

            if (isServiceEnable) {
                apiManager.enableEmployee(request, callback)
            } else {
                apiManager.disableEmployee(request, callback)
            }
        }
    }

    /**
     * To get employee data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun deleteEmployee(
        vendorId: String,
        userId: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val employeeRequest = NSEmployeeRequest(vendorId, userId)
            apiManager.employeeDelete(employeeRequest, object :
                NSRetrofitCallback<NSEmployeeAddDeleteBlankDataResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_EMPLOYEE_DELETE
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?:NSEmployeeAddDeleteBlankDataResponse())
                    }
                }

                override fun onRefreshToken() {
                    deleteEmployee(vendorId, userId, viewModelCallback)
                }
            })
        }
    }

    /**
     * To get employee data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun addEmployee(
        vendorId: String,
        userId: String,
        titleId: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val employeeRequest = NSAddEmployeeRequest(vendorId, userId, titleId)
            apiManager.addEmployee(employeeRequest, object :
                NSRetrofitCallback<NSEmployeeAddDeleteBlankDataResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_EMPLOYEE_ADD
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?:NSEmployeeAddDeleteBlankDataResponse())
                    }
                }

                override fun onRefreshToken() {
                    addEmployee(vendorId, userId, titleId, viewModelCallback)
                }
            })
        }
    }

    /**
     * To get employee data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun editEmployee(
        request: NSEmployeeEditRequest,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            apiManager.employeeEdit(request, object :
                NSRetrofitCallback<NSEmployeeAddDeleteBlankDataResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_EMPLOYEE_EDIT
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?:NSEmployeeAddDeleteBlankDataResponse())
                    }
                }

                override fun onRefreshToken() {
                    editEmployee(request, viewModelCallback)
                }
            })
        }
    }
}