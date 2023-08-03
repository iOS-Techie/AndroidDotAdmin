package com.nyotek.dot.admin.ui.fleets.employee

import android.app.Application
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.NSSingleLiveEvent
import com.nyotek.dot.admin.common.NSViewModel
import com.nyotek.dot.admin.common.utils.isValidList
import com.nyotek.dot.admin.repository.NSEmployeeRepository
import com.nyotek.dot.admin.repository.NSFleetRepository
import com.nyotek.dot.admin.repository.network.requests.NSEmployeeEditRequest
import com.nyotek.dot.admin.repository.network.responses.EmployeeDataItem
import com.nyotek.dot.admin.repository.network.responses.FleetData
import com.nyotek.dot.admin.repository.network.responses.FleetDataItem
import com.nyotek.dot.admin.repository.network.responses.FleetLocationResponse
import com.nyotek.dot.admin.repository.network.responses.JobListDataItem
import com.nyotek.dot.admin.repository.network.responses.NSEmployeeResponse
import com.nyotek.dot.admin.repository.network.responses.NSListJobTitleResponse
import com.nyotek.dot.admin.repository.network.responses.NSUserDetail

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

    fun getVendorDetail() {
        if (!strVendorDetail.isNullOrEmpty()) {
            vendorModel = Gson().fromJson(strVendorDetail, FleetData::class.java)
            vendorId = vendorModel?.vendorId
            getJobTitleList(true, vendorModel?.serviceIds?: arrayListOf())
        }
    }

    fun getJobTitleListFromString(strJob: String?) {
        if (strJob?.isNotEmpty() == true) {
            val listType = object : TypeToken<MutableList<JobListDataItem>>() {}.type
            jobTitleList = Gson().fromJson(strJob, listType)
        }
    }

    fun getJobTitleList(isShowProgress: Boolean, serviceIdList: MutableList<String> = arrayListOf()) {
        if (isShowProgress) {
            showProgress()
        }
        if (serviceIdList.isValidList()) {
            val serviceId = serviceIdList[0]
            callCommonApi({ obj ->
                NSEmployeeRepository.getJobTitle(serviceId, obj)
            }, { data, isSuccess ->
                if (data is NSListJobTitleResponse) {
                    if (isSuccess) {
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
                    } else {
                        hideProgress()
                    }
                } else {
                    hideProgress()
                }
            })
        } else {
            if (jobTitleList.isValidList()) {
                NSApplication.getInstance().setJobRoleType(jobTitleMap)
                getEmployeeList(vendorId)
            } else {
                hideProgress()
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
            callCommonApi({ obj ->
                NSEmployeeRepository.getEmployeeList(vendorId, obj)
            }, { data, isSuccess ->
                hideProgress()
                if (isSuccess) {
                    if (data is NSEmployeeResponse) {
                        data.employeeList.sortByDescending { it.userId }
                        val list = data.employeeList.filter { !it.isDeleted }
                        isEmployeeListAvailable.postValue(list as MutableList<EmployeeDataItem>?)
                    }
                }
            })
        } else {
            hideProgress()
        }
    }

    /**
     * Employee Enable Disable
     *
     */
    fun employeeEnableDisable(vendorId: String, userId: String, isEnable: Boolean) {
        callCommonApi({ obj ->
            NSEmployeeRepository.enableDisableEmployee(vendorId, userId, isEnable, obj)
        }, { _, _ ->

        })
    }

    /**
     * employee delete
     *
     */
    fun employeeDelete(vendorId: String, userId: String) {
        showProgress()
        callCommonApi({ obj ->
            NSEmployeeRepository.deleteEmployee(vendorId, userId, obj)
        }, { _, isSuccess ->
            if (isSuccess) {
                getEmployeeList(vendorId)
            } else {
                hideProgress()
            }
        })
    }

    /**
     * Employee Edit
     *
     */
    fun employeeEdit(request: NSEmployeeEditRequest) {
        showProgress()
        callCommonApi({ obj ->
            NSEmployeeRepository.editEmployee(request, obj)
        }, { _, isSuccess ->
            if (isSuccess) {
                getEmployeeList(vendorId)
            } else {
                hideProgress()
            }
        })
    }

    /**
     * add employees
     *
     */
    fun employeeAdd(vendorId: String, userId: String, titleId: String) {
        showProgress()
        callCommonApi({ obj ->
            NSEmployeeRepository.addEmployee(vendorId, userId, titleId, obj)
        }, { _, isSuccess ->
            if (isSuccess) {
                getEmployeeList(vendorId)
            } else {
                hideProgress()
            }
        })
    }

    fun getDriverLocation(driverId: String?) {
        if (driverId?.isNotEmpty() == true) {
            showProgress()
            callCommonApi({ obj ->
                NSFleetRepository.getDriverLocations(driverId, obj)
            }, { data, isSuccess ->
                hideProgress()
                if (isSuccess) {
                    if (data is FleetLocationResponse) {
                        isDriverLocationAvailable.value = data.fleetDataItem
                    }
                }
            })
        }
    }

    override fun apiResponse(data: Any) {

    }
}