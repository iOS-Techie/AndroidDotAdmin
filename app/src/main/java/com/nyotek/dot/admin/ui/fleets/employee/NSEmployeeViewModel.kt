package com.nyotek.dot.admin.ui.fleets.employee

import android.app.Application
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.NSSingleLiveEvent
import com.nyotek.dot.admin.common.NSViewModel
import com.nyotek.dot.admin.common.utils.isValidList
import com.nyotek.dot.admin.repository.NSCapabilitiesRepository
import com.nyotek.dot.admin.repository.NSEmployeeRepository
import com.nyotek.dot.admin.repository.NSFleetRepository
import com.nyotek.dot.admin.repository.network.callbacks.NSGenericViewModelCallback
import com.nyotek.dot.admin.repository.network.requests.NSEmployeeEditRequest
import com.nyotek.dot.admin.repository.network.responses.EmployeeDataItem
import com.nyotek.dot.admin.repository.network.responses.JobListDataItem
import com.nyotek.dot.admin.repository.network.responses.NSEmployeeBlankDataResponse
import com.nyotek.dot.admin.repository.network.responses.NSEmployeeAddDeleteBlankDataResponse
import com.nyotek.dot.admin.repository.network.responses.NSEmployeeResponse
import com.nyotek.dot.admin.repository.network.responses.NSListJobTitleResponse
import com.nyotek.dot.admin.repository.network.responses.NSUserDetail
import com.nyotek.dot.admin.repository.network.responses.FleetData
import com.nyotek.dot.admin.repository.network.responses.FleetDataItem
import com.nyotek.dot.admin.repository.network.responses.FleetLocationResponse

class NSEmployeeViewModel(application: Application) : NSViewModel(application) {
    var employeeList: MutableList<EmployeeDataItem> = arrayListOf()
    var jobTitleList: MutableList<JobListDataItem> = arrayListOf()
    var jobTitleMap: HashMap<String, JobListDataItem> = hashMapOf()
    var isEmployeeListAvailable = NSSingleLiveEvent<MutableList<EmployeeDataItem>>()
    var isDriverLocationAvailable = NSSingleLiveEvent<FleetDataItem?>()
    var strVendorDetail: String? = null
    var vendorModel: FleetData? = null
    var vendorId: String? = null
    var employeeEditRequest: NSEmployeeEditRequest = NSEmployeeEditRequest()
    var searchUserList: MutableList<NSUserDetail> = arrayListOf()
    var strJobTitle: String? = null

    fun getVendorDetail() {
        if (!strVendorDetail.isNullOrEmpty()) {
            vendorModel = Gson().fromJson(strVendorDetail, FleetData::class.java)
            vendorId = vendorModel?.vendorId
            getJobTitleList(true, vendorModel?.serviceIds?: arrayListOf())
        }
    }

    fun getJobTitleListFromString() {
        if (strJobTitle?.isNotEmpty() == true) {
            val listType = object : TypeToken<MutableList<JobListDataItem>>() {}.type
            jobTitleList = Gson().fromJson(strJobTitle, listType)
        }
    }

    fun getJobTitleList(isShowProgress: Boolean, serviceIdList: MutableList<String> = arrayListOf()) {
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        if (serviceIdList.isValidList()) {
            val serviceId = serviceIdList[0]
            NSEmployeeRepository.getJobTitle(serviceId, object : NSGenericViewModelCallback {
                override fun <T> onSuccess(data: T) {
                    if (data is NSListJobTitleResponse) {
                        if (vendorModel?.serviceIds?.size == serviceIdList.size) {
                            jobTitleList.clear()
                        }

                        val tempJobTitleList: MutableList<JobListDataItem> = arrayListOf()
                        tempJobTitleList.addAll(data.jobTitleList)

                        for (jobData in tempJobTitleList) {
                            jobTitleList.add(jobData)
                            jobTitleMap[jobData.id!!] = jobData
                        }
                        serviceIdList.remove(serviceIdList[0])
                        getJobTitleList(isShowProgress, serviceIdList)
                    }
                }

                override fun onError(errors: List<Any>) {
                    handleError(errors)
                }

                override fun onFailure(failureMessage: String?) {
                    handleFailure(failureMessage)
                }

                override fun <T> onNoNetwork(localData: T) {
                    handleNoNetwork()
                }

            })
        } else {
            if (jobTitleList.isValidList()) {
                NSApplication.getInstance().setJobRoleType(jobTitleMap)
                getEmployeeList(vendorId)
            } else {
                isProgressShowing.value = false
            }
        }
    }

    fun getEmployeeWithRole(isShowProgress: Boolean, serviceList: MutableList<String>, vendorId: String?) {
        val jobMap = NSApplication.getInstance().getJobRolesTypes()
        if (jobMap.isEmpty()) {
            getJobTitleList(isShowProgress, serviceList)
        } else {
            jobTitleMap.clear()
            jobTitleMap.putAll(jobMap)
            getEmployeeList(vendorId)
        }
    }

    /**
     * Get employee list
     *
     */
    private fun getEmployeeList(vendorId: String?) {
        if (!vendorId.isNullOrEmpty()) {
            NSEmployeeRepository.getEmployeeList(vendorId, this)
        } else {
            isProgressShowing.value = false
        }
    }

    /**
     * Get user detail
     *
     * @param isShowProgress
     */
    fun employeeEnableDisable(vendorId: String, userId: String, isEnable: Boolean, isShowProgress: Boolean) {
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        NSEmployeeRepository.enableDisableEmployee(vendorId, userId, isEnable, this)
    }

    /**
     * employee delete
     *
     * @param isShowProgress
     */
    fun employeeDelete(vendorId: String, userId: String, isShowProgress: Boolean) {
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        NSEmployeeRepository.deleteEmployee(vendorId, userId, this)
    }

    /**
     * Get user detail
     *
     * @param isShowProgress
     */
    fun employeeEdit(isShowProgress: Boolean, request: NSEmployeeEditRequest) {
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        NSEmployeeRepository.editEmployee(request, this)
    }

    /**
     * add employees
     *
     * @param isShowProgress
     */
    fun employeeAdd(vendorId: String, userId: String, titleId: String, isShowProgress: Boolean) {
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        NSEmployeeRepository.addEmployee(vendorId, userId, titleId, this)
    }

    fun getDriverLocation(driverId: String?) {
        if (driverId?.isNotEmpty() == true) {
            isProgressShowing.value = true
            NSFleetRepository.getDriverLocations(driverId, this)
        }
    }

    override fun apiResponse(data: Any) {

        when (data) {
            is NSEmployeeResponse -> {
                isProgressShowing.value = false
                data.employeeList.sortByDescending { it.userId }
                val list = data.employeeList.filter { !it.isDeleted }
                isEmployeeListAvailable.postValue(list as MutableList<EmployeeDataItem>?)
            }
            is NSEmployeeBlankDataResponse -> {
                isProgressShowing.value = false
            }
            is NSEmployeeAddDeleteBlankDataResponse -> {
                getEmployeeList(vendorId)
            }
            is FleetLocationResponse -> {
                isProgressShowing.value = false
                isDriverLocationAvailable.value = data.fleetDataItem
            }
        }
    }
}