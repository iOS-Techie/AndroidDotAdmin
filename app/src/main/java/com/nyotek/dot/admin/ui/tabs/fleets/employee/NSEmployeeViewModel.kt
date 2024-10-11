package com.nyotek.dot.admin.ui.tabs.fleets.employee

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nyotek.dot.admin.base.BaseViewModel
import com.nyotek.dot.admin.common.NSDataStorePreferences
import com.nyotek.dot.admin.common.NSLanguageConfig
import com.nyotek.dot.admin.common.NSSingleLiveEvent
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.data.Repository
import com.nyotek.dot.admin.models.requests.NSAddEmployeeRequest
import com.nyotek.dot.admin.models.requests.NSAssignVehicleRequest
import com.nyotek.dot.admin.models.requests.NSEmployeeEditRequest
import com.nyotek.dot.admin.models.requests.NSEmployeeListRequest
import com.nyotek.dot.admin.models.requests.NSEmployeeRequest
import com.nyotek.dot.admin.models.requests.NSFleetDriverRequest
import com.nyotek.dot.admin.models.requests.NSSearchEmployeeData
import com.nyotek.dot.admin.models.requests.NSSearchEmployeeRequest
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
import com.nyotek.dot.admin.models.responses.NSUserDetailResponse
import com.nyotek.dot.admin.models.responses.SearchEmployeeData
import com.nyotek.dot.admin.models.responses.SearchEmployeeResponse
import com.nyotek.dot.admin.models.responses.VehicleData
import com.nyotek.dot.admin.models.responses.VehicleDataItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NSEmployeeViewModel @Inject constructor(
    private val repository: Repository,
    val languageConfig: NSLanguageConfig,
    val dataStoreRepository: NSDataStorePreferences,
    colorResources: ColorResources,
    application: Application
) : BaseViewModel(repository, languageConfig.dataStorePreference, colorResources, application) {
    var selectedPosition: Int = -1
    var employeeList: MutableList<EmployeeDataItem> = arrayListOf()
    var jobTitleList: MutableList<JobListDataItem> = arrayListOf()
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
            getJobTitleList(true)
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

    fun getJobTitleList(isShowProgress: Boolean) = viewModelScope.launch {
        getJobTitleListApi(isShowProgress)
    }

    private suspend fun getJobTitleListApi(isShowProgress: Boolean) {
        if (isShowProgress) showProgress()
        performApiCalls(
            { repository.remote.getListOfRoles()}
        ) { response, isSuccess ->
            if (isSuccess) {
                val data = response[0] as NSListJobTitleResponse?
                if (data is NSListJobTitleResponse) {
                    jobTitleList.clear()
                    jobTitleList.addAll(data.jobTitleList?: arrayListOf())
                    colorResources.themeHelper.setJobRoleTypes(jobTitleList)
                    getEmployeeList(vendorId)
                } else {
                    hideProgress()
                }
            } else {
                hideProgress()
            }
        }
    }

    fun getEmployeeWithRole(isShowProgress: Boolean, vendorId: String?) {
        val jobRoleList = colorResources.themeHelper.getJobRolesTypes()
        if (jobRoleList.isEmpty()) {
            getJobTitleList(isShowProgress)
        } else {
            getEmployeeList(vendorId)
        }
    }

    private fun getEmployeeList(vendorId: String?) = viewModelScope.launch {
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

    private fun getFleetDriverLocations(driverList: List<String>) = viewModelScope.launch {
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

    fun employeeAdd(vendorId: String, userIdList: MutableList<String>, titleId: String, callback: (Boolean) -> Unit) = viewModelScope.launch {
        employeeAddApi(vendorId, userIdList, titleId, callback)
    }

    private suspend fun employeeAddApi(vendorId: String, userIdList: MutableList<String>, titleId: String, callback: (Boolean) -> Unit) {
        showProgress()
        val list: MutableList<NSSearchEmployeeData> = arrayListOf()
        
        for (ids in userIdList) {
            if (list.find { it.mobile == ids } == null) {
                list.add(NSSearchEmployeeData(ids))
            }
        }
        val searchRequest = NSSearchEmployeeRequest(list, titleId)
        
        performApiCalls({
            repository.remote.searchEmployee(vendorId, searchRequest)
        }) { responses, isSuccess ->
            if (isSuccess) {
                val model = responses[0] as SearchEmployeeResponse?
                if (model != null) {
                    if ((model.data?.size?:0) >= userIdList.size) {
                        employeeAddApiSetup(vendorId, model.data?: arrayListOf(), titleId, callback)
                    } else {
                        callback.invoke(true)
                    }
                } else {
                    callback.invoke(true)
                    hideProgress()
                }
            } else {
                callback.invoke(false)
                hideProgress()
            }
        }
    }
    
    private fun employeeAddApiSetup(vendorId: String, userIdList: MutableList<SearchEmployeeData>, titleId: String, callback: (Boolean) -> Unit) = viewModelScope.launch {
        employeeAddSetupApi(vendorId, userIdList, titleId, callback)
    }
    
    private suspend fun employeeAddSetupApi(vendorId: String, userIdList: MutableList<SearchEmployeeData>, titleId: String, callback: (Boolean) -> Unit) {
        showProgress()
        val results = mutableListOf<Boolean>()
        
        for (data in userIdList) {
            if (!data.data?.id.isNullOrEmpty()) {
                performApiCalls({
                    repository.remote.addEmployee(
                        NSAddEmployeeRequest(
                            vendorId,
                            data.data?.id,
                            titleId
                        )
                    )
                }) { _, isSuccess ->
                    results.add(isSuccess)
                    handleResults(results.size, userIdList.size, callback)
                }
            } else {
                results.add(false)
                handleResults(results.size, userIdList.size, callback)
            }
        }
    }
    
    private fun handleResults(resultSize: Int, idSize: Int, callback: (Boolean) -> Unit) {
        if (resultSize >= idSize) {
            getEmployeeList(vendorId)
        } else {
            callback.invoke(false)
            hideProgress()
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
    
    fun getUserDetail(userId: String, callback: (NSUserDetailResponse?) -> Unit) = viewModelScope.launch {
        getUserDetailApi(userId, callback)
    }
    
    private suspend fun getUserDetailApi(userId: String, callback: (NSUserDetailResponse?) -> Unit) {
        if (colorResources.themeHelper.getUserDetail(userId) != null) {
            callback.invoke(colorResources.themeHelper.getUserDetail(userId))
        } else {
            performApiCalls({ repository.remote.getUserDetail(userId) },
                isShowError = false) { responses, isSuccess ->
                if (isSuccess) {
                    val userDetail = responses[0] as NSUserDetailResponse?
                    callback.invoke(userDetail)
                } else {
                    callback.invoke(null)
                }
            }
        }
    }
}