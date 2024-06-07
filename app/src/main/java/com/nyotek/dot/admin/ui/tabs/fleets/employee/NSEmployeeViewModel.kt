package com.nyotek.dot.admin.ui.tabs.fleets.employee

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nyotek.dot.admin.base.BaseViewModel
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.NSLanguageConfig
import com.nyotek.dot.admin.common.NSSingleLiveEvent
import com.nyotek.dot.admin.common.extension.isValidList
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.data.Repository
import com.nyotek.dot.admin.models.requests.NSAddEmployeeRequest
import com.nyotek.dot.admin.models.requests.NSAssignVehicleRequest
import com.nyotek.dot.admin.models.requests.NSEmployeeEditRequest
import com.nyotek.dot.admin.models.requests.NSEmployeeListRequest
import com.nyotek.dot.admin.models.requests.NSEmployeeRequest
import com.nyotek.dot.admin.models.requests.NSFleetDriverRequest
import com.nyotek.dot.admin.models.requests.NSFleetRequest
import com.nyotek.dot.admin.models.responses.EmployeeDataItem
import com.nyotek.dot.admin.models.responses.FleetData
import com.nyotek.dot.admin.models.responses.FleetDataItem
import com.nyotek.dot.admin.models.responses.FleetLocationResponse
import com.nyotek.dot.admin.models.responses.JobListDataItem
import com.nyotek.dot.admin.models.responses.NSAssignVehicleDriverResponse
import com.nyotek.dot.admin.models.responses.NSDriverVehicleDetailResponse
import com.nyotek.dot.admin.models.responses.NSEmployeeResponse
import com.nyotek.dot.admin.models.responses.NSListJobTitleResponse
import com.nyotek.dot.admin.models.responses.NSUserDetail
import com.nyotek.dot.admin.models.responses.VehicleData
import com.nyotek.dot.admin.models.responses.VehicleDataItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NSEmployeeViewModel @Inject constructor(
    private val repository: Repository,
    val languageConfig: NSLanguageConfig,
    colorResources: ColorResources,
    application: Application
) : BaseViewModel(repository, languageConfig.dataStorePreference, colorResources, application) {
    var selectedPosition: Int = -1
    var employeeList: MutableList<EmployeeDataItem> = arrayListOf()
    var jobTitleList: MutableList<JobListDataItem> = arrayListOf()
    var jobTitleMap: HashMap<String, JobListDataItem> = hashMapOf()
    var isEmployeeListAvailable = NSSingleLiveEvent<MutableList<EmployeeDataItem>>()
    var isFleetDetailAvailable = NSSingleLiveEvent<FleetDataItem>()
    var vehicleDataObserve = NSSingleLiveEvent<VehicleData>()
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
    var gson: Gson = Gson()
    var driverDetailFleetData: FleetDataItem? = null

    fun getVendorDetail() {
        if (!strVendorDetail.isNullOrEmpty()) {
            vendorModel = gson.fromJson(strVendorDetail, FleetData::class.java)
            vendorId = vendorModel?.vendorId
            getJobTitleList(true, vendorModel?.serviceIds?: arrayListOf())
        }
    }

    fun getDriverDetail() {
        if (!strVehicleDetail.isNullOrEmpty()) {
            employeeDataItem = gson.fromJson(strVehicleDetail, EmployeeDataItem::class.java)
            fleetModel = gson.fromJson(fleetDetail, FleetData::class.java)
        }
    }

    fun getJobTitleListFromString(strJob: String?) {
        if (strJob?.isNotEmpty() == true) {
            val listType = object : TypeToken<MutableList<JobListDataItem>>() {}.type
            jobTitleList = gson.fromJson(strJob, listType)
        }
    }

    fun getJobTitleList(isShowProgress: Boolean, serviceIdList: MutableList<String> = arrayListOf()) = viewModelScope.launch {
        getJobTitleListApi(isShowProgress, serviceIdList)
    }

    private suspend fun getJobTitleListApi(isShowProgress: Boolean, serviceIdList: MutableList<String> = arrayListOf()) {
        if (isShowProgress) showProgress()

        if (serviceIdList.isValidList()) {
            val serviceId = serviceIdList[0]

            performApiCalls(
                { repository.remote.getListOfJobTitle(serviceId)}
            ) { response, isSuccess ->
                if (isSuccess) {
                    val data = response[0] as NSListJobTitleResponse?
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
                    } else {
                        hideProgress()
                    }
                } else {
                    hideProgress()
                }
            }
        } else {
            if (jobTitleList.isValidList()) {
                colorResources.themeHelper.setJobRoleType(jobTitleMap)
                getEmployeeList(vendorId)
            } else {
                hideProgress()
            }
        }
    }

    fun getEmployeeWithRole(isShowProgress: Boolean, serviceList: MutableList<String>, vendorId: String?) {
        val jobMap = colorResources.themeHelper.getJobRolesTypes()
        if (jobMap.isEmpty()) {
            getJobTitleList(isShowProgress, serviceList)
        } else {
            jobTitleMap.clear()
            jobTitleMap.putAll(jobMap)
            getEmployeeList(vendorId)
        }
    }

    fun getEmployeeList(vendorId: String?) = viewModelScope.launch {
        getEmployeeListApi(vendorId)
    }

    private suspend fun getEmployeeListApi(vendorId: String?) {
        if (!vendorId.isNullOrEmpty()) {
            performApiCalls(
                { repository.remote.getListEmployees(NSEmployeeListRequest(vendorId))}
            ) { response, isSuccess ->
                if (isSuccess) {
                    val data = response[0] as NSEmployeeResponse?
                    if (data is NSEmployeeResponse) {
                        data.employeeList.sortByDescending { it.userId }
                        val list = data.employeeList
                        isEmployeeListAvailable.postValue(list as MutableList<EmployeeDataItem>?)
                        getFleetDriverLocations(list.map { it.userId ?: "" })
                    } else {
                        isEmployeeListAvailable.postValue(arrayListOf())
                        hideProgress()
                    }
                } else {
                    isEmployeeListAvailable.postValue(arrayListOf())
                    hideProgress()
                }
            }
        } else {
            isEmployeeListAvailable.postValue(arrayListOf())
            hideProgress()
        }
    }

    fun getFleetDriverLocations(driverList: List<String>) = viewModelScope.launch {
        getFleetDriverLocationsApi(driverList)
    }

    private suspend fun getFleetDriverLocationsApi(driverList: List<String>) {
        performApiCalls(
            { repository.remote.getFleetDriverLocation(NSFleetDriverRequest(driverList))}
        ) { response, isSuccess ->
            hideProgress()
            if (isSuccess) {
                val data = response[0] as FleetLocationResponse?
                if (data is FleetLocationResponse) {
                    isFleetDetailAvailable.postValue(data.fleetDataItem ?: FleetDataItem())
                } else {
                    isFleetDetailAvailable.postValue(FleetDataItem())
                }
            } else {
                isFleetDetailAvailable.postValue(FleetDataItem())
            }
        }
    }

    fun employeeEnableDisable(vendorId: String, userId: String, isEnable: Boolean) = viewModelScope.launch {
        employeeEnableDisableApi(vendorId, userId, isEnable)
    }

    private suspend fun employeeEnableDisableApi(vendorId: String, userId: String, isEnable: Boolean) {
        performApiCalls(
            {
                if (isEnable) {
                    repository.remote.enableEmployee(NSEmployeeRequest(vendorId, userId))
                } else {
                    repository.remote.disableEmployee(NSEmployeeRequest(vendorId, userId))
                }
            }
        ) {_, _ ->}
    }

    fun employeeDelete(vendorId: String, userId: String) = viewModelScope.launch {
        employeeDeleteApi(vendorId, userId)
    }

    private suspend fun employeeDeleteApi(vendorId: String, userId: String) {
        showProgress()
        performApiCalls({ repository.remote.employeeDelete(NSEmployeeRequest(vendorId, userId)) }
        ) { _, isSuccess ->
            if (isSuccess) {
                getEmployeeList(vendorId)
            } else {
                hideProgress()
            }
        }
    }

    fun employeeEdit(request: NSEmployeeEditRequest) = viewModelScope.launch {
        employeeEditApi(request)
    }

    private suspend fun employeeEditApi(request: NSEmployeeEditRequest) {
        showProgress()
        performApiCalls({ repository.remote.employeeEdit(request) }
        ) { _, isSuccess ->
            if (isSuccess) {
                getEmployeeList(vendorId)
            } else {
                hideProgress()
            }
        }
    }

    fun employeeAdd(vendorId: String, userId: String, titleId: String) = viewModelScope.launch {
        employeeAddApi(vendorId, userId, titleId)
    }

    private suspend fun employeeAddApi(vendorId: String, userId: String, titleId: String) {
        showProgress()
        performApiCalls({ repository.remote.addEmployee(NSAddEmployeeRequest(vendorId, userId, titleId)) }
        ) { _, isSuccess ->
            if (isSuccess) {
                getEmployeeList(vendorId)
            } else {
                hideProgress()
            }
        }
    }

    fun getDriverLocation(driverId: String?, callback: (FleetDataItem?) -> Unit) = viewModelScope.launch {
        getDriverLocationApi(driverId, callback)
    }

    private suspend fun getDriverLocationApi(driverId: String?, callback: (FleetDataItem?) -> Unit) {
        if (driverId?.isNotEmpty() == true) {
            showProgress()
            performApiCalls({ repository.remote.getDriverLocation(driverId) }
            ) { response, isSuccess ->
                hideProgress()
                if (isSuccess) {
                    val data = response[0] as FleetLocationResponse?
                    if (data is FleetLocationResponse) {
                        driverDetailFleetData = data.fleetDataItem
                        callback.invoke(data.fleetDataItem)
                    }
                } else {
                    callback.invoke(FleetDataItem())
                }
            }
        }
    }

    fun assignVehicle(isForDelete: Boolean, driverId: String, vehicleId: String? = "", capabilities: MutableList<String>, callback: ((Boolean) -> Unit)) = viewModelScope.launch {
        assignVehicleApi(isForDelete, driverId, vehicleId, capabilities, callback)
    }

    private suspend fun assignVehicleApi(isForDelete: Boolean, driverId: String, vehicleId: String? = "", capabilities: MutableList<String>, callback: ((Boolean) -> Unit)) {
        if (isForDelete) showProgress()
        val request = NSAssignVehicleRequest(driverId, fleetModel?.vendorId, vehicleId, capabilities)
        performApiCalls({ repository.remote.assignVehicle(request) }
        ) { _, isSuccess ->
            if (isSuccess) {
                if (!isForDelete) {
                    hideProgress()
                }
                callback.invoke(true)
            } else {
                hideProgress()
            }
        }
    }

    fun getAssignVehicleDriver(driverId: String?, fleetId: String, isShowProgress: Boolean) = viewModelScope.launch {
        getAssignVehicleDriverApi(driverId, fleetId, isShowProgress)
    }

    private suspend fun getAssignVehicleDriverApi(driverId: String?, fleetId: String, isShowProgress: Boolean) {
        if (driverId != null) {
            if (isShowProgress) showProgress()
            performApiCalls({ repository.remote.getAssignVehicleByDriver(driverId, fleetId) }
            ) { response, isSuccess ->
                if (isSuccess) {
                    val data = response.first() as NSAssignVehicleDriverResponse?
                    if (data is NSAssignVehicleDriverResponse) {
                        getDriverVehicleDetail(false, data.data?.vehicleId)
                    } else {
                        vehicleDataObserve.postValue(VehicleData())
                        hideProgress()
                    }
                } else {
                    vehicleDataObserve.postValue(VehicleData())
                    hideProgress()
                }
            }
        }
    }

    fun getDriverVehicleDetail(isForDelete: Boolean, vehicleId: String?) = viewModelScope.launch {
        getDriverVehicleDetailApi(isForDelete, vehicleId)
    }

    private suspend fun getDriverVehicleDetailApi(isForDelete: Boolean, vehicleId: String?) {
        if (!vehicleId.isNullOrEmpty()) {
            if (!isForDelete) {
                showProgress()
            }
            performApiCalls({ repository.remote.getDriverVehicleDetail(vehicleId) }
            ) { response, isSuccess ->
                hideProgress()
                if (isSuccess) {
                    if (isForDelete) {
                        vehicleDataObserve.postValue(VehicleData())
                    } else {
                        val data = response.first() as NSDriverVehicleDetailResponse?
                        if (data is NSDriverVehicleDetailResponse) {
                            vehicleDataObserve.postValue(data.data ?: VehicleData())
                        } else {
                            vehicleDataObserve.postValue(VehicleData())
                        }
                    }
                } else {
                    vehicleDataObserve.postValue(VehicleData())
                }
            }
        } else {
            vehicleDataObserve.postValue(VehicleData())
            hideProgress()
        }
    }
}