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
import com.nyotek.dot.admin.repository.NSVehicleRepository
import com.nyotek.dot.admin.repository.network.requests.NSAssignVehicleRequest
import com.nyotek.dot.admin.repository.network.requests.NSEmployeeEditRequest
import com.nyotek.dot.admin.repository.network.responses.EmployeeDataItem
import com.nyotek.dot.admin.repository.network.responses.FleetData
import com.nyotek.dot.admin.repository.network.responses.FleetDataItem
import com.nyotek.dot.admin.repository.network.responses.FleetLocationResponse
import com.nyotek.dot.admin.repository.network.responses.JobListDataItem
import com.nyotek.dot.admin.repository.network.responses.NSAssignVehicleDriverResponse
import com.nyotek.dot.admin.repository.network.responses.NSDriverVehicleDetailResponse
import com.nyotek.dot.admin.repository.network.responses.NSEmployeeResponse
import com.nyotek.dot.admin.repository.network.responses.NSListJobTitleResponse
import com.nyotek.dot.admin.repository.network.responses.NSUserDetail
import com.nyotek.dot.admin.repository.network.responses.VehicleData
import com.nyotek.dot.admin.repository.network.responses.VehicleDataItem

class NSEmployeeViewModel(application: Application) : NSViewModel(application) {
    var employeeList: MutableList<EmployeeDataItem> = arrayListOf()
    var jobTitleList: MutableList<JobListDataItem> = arrayListOf()
    var jobTitleMap: HashMap<String, JobListDataItem> = hashMapOf()
    var isEmployeeListAvailable = NSSingleLiveEvent<MutableList<EmployeeDataItem>>()
    var strVendorDetail: String? = null
    var vendorModel: FleetData? = null
    var vendorId: String? = null
    var employeeEditRequest: NSEmployeeEditRequest = NSEmployeeEditRequest()
    var searchUserList: MutableList<NSUserDetail> = arrayListOf()

    //Driver Detail
    var fleetDetail: String? = null
    var strVehicleDetail: String? = null
    var employeeDataItem: EmployeeDataItem? = null
    var fleetModel: FleetData? = null
    var vehicleDataList: MutableList<VehicleDataItem> = arrayListOf()
    var isMapReset: Boolean = false
    var isDetailScreenOpen: Boolean = false

    fun getVendorDetail() {
        if (!strVendorDetail.isNullOrEmpty()) {
            vendorModel = Gson().fromJson(strVendorDetail, FleetData::class.java)
            vendorId = vendorModel?.vendorId
            getJobTitleList(true, vendorModel?.serviceIds?: arrayListOf())
        }
    }

    fun getDriverDetail() {
        if (!strVehicleDetail.isNullOrEmpty()) {
            employeeDataItem = Gson().fromJson(strVehicleDetail, EmployeeDataItem::class.java)
            fleetModel = Gson().fromJson(fleetDetail, FleetData::class.java)
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
            isEmployeeListAvailable.postValue(arrayListOf())
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

    var driverDetailFleetData: FleetDataItem? = null
    fun getDriverLocation(driverId: String?, callback: (FleetDataItem?) -> Unit) {
        if (driverId?.isNotEmpty() == true) {
            showProgress()
            callCommonApi({ obj ->
                NSFleetRepository.getDriverLocations(driverId, obj)
            }, { data, isSuccess ->
                hideProgress()
                if (isSuccess) {
                    if (data is FleetLocationResponse) {
                        driverDetailFleetData = data.fleetDataItem
                        callback.invoke(data.fleetDataItem)
                    }
                }
            })
        }
    }

    fun assignVehicle(isForDelete: Boolean, driverId: String, vehicleId: String? = "", capabilities: MutableList<String>, callback: ((Boolean) -> Unit)) {
        if (isForDelete) showProgress()
        val request = NSAssignVehicleRequest(driverId, fleetModel?.vendorId, vehicleId, capabilities)
        callCommonApi({ obj ->
            NSVehicleRepository.assignVehicle(request, obj)
        }, { _, isSuccess ->
            if (isSuccess) {
                if (!isForDelete) {
                    hideProgress()
                }
                callback.invoke(true)
            } else {
                hideProgress()
            }
        })
    }

    fun getAssignVehicleDriver(driverId: String?, fleetId: String, isShowProgress: Boolean, callback: ((VehicleData?) -> Unit)) {
        if (driverId != null) {
            if (isShowProgress) showProgress()
            callCommonApi({ obj ->
                NSVehicleRepository.getAssignVehicleDriver(driverId, fleetId, obj)
            }, { data, isSuccess ->
                if (!isSuccess) {
                    hideProgress()
                }
                if (data is NSAssignVehicleDriverResponse) {
                    getDriverVehicleDetail(false, data.data?.vehicleId, callback)
                } else {
                    callback.invoke(VehicleData())
                    hideProgress()
                }
            })
        }
    }

    fun getDriverVehicleDetail(isForDelete: Boolean, vehicleId: String?, callback: ((VehicleData?) -> Unit)) {
        if (vehicleId != null) {
            if (!isForDelete) {
                showProgress()
            }
            callCommonApi({ obj ->
                NSVehicleRepository.getDriverVehicleDetail(vehicleId, obj)
            }, { data, _ ->
                hideProgress()
                if (data is NSDriverVehicleDetailResponse) {
                    callback.invoke(data.data)
                } else {
                    callback.invoke(VehicleData())
                }
            }, false)
        } else {
            callback.invoke(VehicleData())
            hideProgress()
        }
    }

    override fun apiResponse(data: Any) {

    }
}