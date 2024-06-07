package com.nyotek.dot.admin.base

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSDataStorePreferences
import com.nyotek.dot.admin.common.NSThemeHelper
import com.nyotek.dot.admin.common.extension.isValidList
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.common.utils.ConnectionChecker
import com.nyotek.dot.admin.data.Repository
import com.nyotek.dot.admin.models.requests.NSLanguageRequest
import com.nyotek.dot.admin.models.requests.NSRefreshTokenRequest
import com.nyotek.dot.admin.models.responses.ActiveInActiveFilter
import com.nyotek.dot.admin.models.responses.CapabilitiesDataItem
import com.nyotek.dot.admin.models.responses.ErrorModel
import com.nyotek.dot.admin.models.responses.FleetData
import com.nyotek.dot.admin.models.responses.FleetListResponse
import com.nyotek.dot.admin.models.responses.LanguageSelectModel
import com.nyotek.dot.admin.models.responses.NSCapabilitiesResponse
import com.nyotek.dot.admin.models.responses.NSErrorResponse
import com.nyotek.dot.admin.models.responses.NSGetServiceListResponse
import com.nyotek.dot.admin.models.responses.NSLocalLanguageResponse
import com.nyotek.dot.admin.models.responses.NSUserResponse
import com.nyotek.dot.admin.models.responses.RegionDataItem
import com.nyotek.dot.admin.models.responses.RegionResponse
import com.nyotek.dot.admin.models.responses.VendorDetailResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

abstract class BaseViewModel(private val repository: Repository, private val dataStorePreference: NSDataStorePreferences, val colorResources: ColorResources, private val application: Application) : AndroidViewModel(application) {
    protected val _networkStatus = MutableLiveData<Boolean>()
    val networkStatus: LiveData<Boolean> = _networkStatus

    protected val _sessionTimeOut = MutableLiveData<Boolean>()
    val sessionTimeOut: LiveData<Boolean> = _sessionTimeOut

    protected val _error = MutableLiveData<ErrorModel?>()
    val error: LiveData<ErrorModel?> = _error

    protected val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    protected val _refresh = MutableLiveData<Boolean>()
    val refresh: LiveData<Boolean> = _refresh

    var capabilitiesList: MutableLiveData<MutableList<CapabilitiesDataItem>> = MutableLiveData()

    suspend fun performApiCalls(
        vararg apiCalls: suspend () -> Response<*>?,
        onSuccess: (List<Any?>, Boolean) -> Unit
    ) {
        if (ConnectionChecker.isConnectionAvailable(application.applicationContext)) {
            viewModelScope.launch {
                try {
                    val deferredResults = apiCalls.map { async { it.invoke() } }
                    val responses = deferredResults.map { it.await() }

                    val successfulResponses = mutableListOf<Any?>()
                    var failedResponse: Response<*>? = null // Pair of error code and message

                    for (response in responses) {
                        if (response?.isSuccessful == true) {
                            successfulResponses.add(response.body())
                        } else {
                            failedResponse = response
                        }
                    }

                    if (successfulResponses.isValidList() || failedResponse == null) {
                        onSuccess(successfulResponses, true)
                        if (failedResponse != null) {
                            getApiErrorMessage(onSuccess, failedResponse, { isTokenRefresh ->
                                if (isTokenRefresh) {
                                    viewModelScope.launch {
                                        tokenRefreshCall(apiCalls = apiCalls, onSuccess = onSuccess)
                                    }
                                }
                            }) { isSessionTimeOut ->
                                if (isSessionTimeOut) {
                                    onSuccess(arrayListOf(), false)
                                    _loading.value = false
                                    _sessionTimeOut.value = true
                                    _refresh.value = false
                                }
                            }
                        }
                    } else {
                        // Handle failed responses
                        getApiErrorMessage(onSuccess, failedResponse, { isTokenRefresh ->
                            if (isTokenRefresh) {
                                viewModelScope.launch {
                                    tokenRefreshCall(apiCalls = apiCalls, onSuccess = onSuccess)
                                }
                            }
                        }) { isSessionTimeOut ->
                            if (isSessionTimeOut) {
                                onSuccess(arrayListOf(), false)
                                _loading.value = false
                                _sessionTimeOut.value = true
                                _refresh.value = false
                            }
                        }
                    }

                } catch (e: Exception) {
                    _loading.value = false
                    val failedResponses = ErrorModel(e.hashCode(), e.localizedMessage)
                    _error.value = failedResponses
                    _refresh.value = false
                    onSuccess(arrayListOf(), false)
                }
            }
        } else {
            _loading.value = false
            _networkStatus.value = false
            _refresh.value = false
            onSuccess(arrayListOf(), false)
        }
    }

    private suspend fun tokenRefreshCall(
        vararg apiCalls: suspend () -> Response<*>?,
        onSuccess: (List<Any?>, Boolean) -> Unit
    ) {
        val refreshToken = dataStorePreference.refreshToken
        if (refreshToken?.isNotEmpty() == true) {
            performApiCalls(
                { repository.remote.refreshToken(NSRefreshTokenRequest(refreshToken)) }
            ) { responses, isSuccess ->
                val failedResponses =
                    ErrorModel(-1, colorResources.getStringResource().notRefreshToken)
                if (isSuccess) {
                    val userResponse: NSUserResponse? = responses[0] as NSUserResponse?
                    userResponse?.data?.apply {
                        if (refreshToken.isEmpty()) {
                            _error.value = failedResponses
                            _loading.value = false
                            _refresh.value = false
                            onSuccess(arrayListOf(), false)
                        } else {
                            viewModelScope.launch {
                                dataStorePreference.apply {
                                    userData = userResponse
                                    authToken = accessToken
                                    dataStorePreference.refreshToken = refreshToken
                                    performApiCalls(
                                        apiCalls = apiCalls,
                                        onSuccess = onSuccess
                                    )
                                }

                            }
                        }
                    } ?: run {
                        _error.value = failedResponses
                        _loading.value = false
                        _refresh.value = false
                        onSuccess(arrayListOf(), false)
                    }
                } else {
                    _error.value = failedResponses
                    _loading.value = false
                    _refresh.value = false
                    onSuccess(arrayListOf(), false)
                }
            }
        }
    }

    private fun getApiErrorMessage(onSuccess: (List<Any?>, Boolean) -> Unit,
        rawErrorResponse: Response<*>,
        tokenRefreshCallback: (Boolean) -> Unit,
        sessionCallback: (Boolean) -> Unit
    ) {
        val stringResource = colorResources.getStringResource()
        val errorMessageList:MutableList<String> = mutableListOf()

        var errorMessage: String = stringResource.dataFailed
        var isSessionTimeOut = false
        val isErrorNotNull = rawErrorResponse.errorBody() != null
        var isTokenExpire = false
        if (isErrorNotNull) {
            var strError = rawErrorResponse.errorBody()!!.string()
            val isError = strError.isNotEmpty()
            if (isError) {
                if (strError.startsWith("{") && strError.contains(NSConstants.ERROR)) {
                    val errorModel = Gson().fromJson(strError, NSErrorResponse::class.java)
                    if (errorModel != null) {
                        strError = errorModel.error!!
                    }
                }
                if (strError.contains(NSConstants.SESSION_EXPIRED_ERROR) || errorMessage.contains(
                        NSConstants.SESSION_EXPIRED_ERROR
                    )) {
                    isSessionTimeOut = true
                } else {
                    errorMessage = strError
                }
            }
        }

        if (isSessionTimeOut) {
            sessionCallback.invoke(true)
        } else {
            when (val responseErrorCode = rawErrorResponse.code()) {
                in 400..429 -> {
                    if (responseErrorCode == 401) {
                        isTokenExpire = true
                        tokenRefreshCallback.invoke(true)
                    } else if (responseErrorCode != 404){
                        errorMessageList.clear()
                        if (errorMessage.isNotEmpty()) {
                            errorMessageList.add(errorMessage)
                        }
                    }
                }
                in 500..503 -> {
                    errorMessageList.clear()
                    if (errorMessage.isNotEmpty()) {
                        errorMessageList.add(errorMessage)
                    }
                }
                else -> {
                    errorMessageList.clear()
                    if (rawErrorResponse.body() != null && !rawErrorResponse.message()
                            .isNullOrEmpty()
                    ) {
                        if (errorMessage.isNotEmpty()) {
                            errorMessageList.add(errorMessage)
                        }
                    } else if (rawErrorResponse.body() == null && rawErrorResponse.errorBody() != null) {
                        if (errorMessage.isNotEmpty()) {
                            errorMessageList.add(errorMessage)
                        }
                    } else {
                        errorMessageList.add(stringResource.dataFailed)
                    }
                }
            }
        }

        if (!isTokenExpire && errorMessageList.isValidList()) {
            Handler(Looper.getMainLooper()).post {
                val failedResponses = ErrorModel(rawErrorResponse.code(), errorMessageList[0])
                onSuccess(arrayListOf(), false)
                _error.value = failedResponses
                _loading.value = false
                _refresh.value = false
            }
        } else if(!isTokenExpire && !errorMessageList.isValidList()){
            onSuccess(arrayListOf(), false)
            _loading.value = false
            _refresh.value = false
        }
    }

    fun getFilterSelectedTypes(list: MutableList<ActiveInActiveFilter>): MutableList<String> {
        val selectedFilterList: MutableList<String> = arrayListOf()
        for (data in list) {
            if (data.isActive && data.key != NSConstants.ALL) {
                selectedFilterList.add(data.key)
            }
        }
        return selectedFilterList
    }

    fun showError(message: String) {
        val failedResponses = ErrorModel(-1, message)
        _error.value = failedResponses
    }

    fun showProgress() {
        _loading.value = true
    }

    fun hideProgress() {
        _loading.value = false
    }


    fun getCapabilities(isShowProgress: Boolean, isApiDataCheck: Boolean = false, callback: ((MutableList<CapabilitiesDataItem>) -> Unit)? = null) = viewModelScope.launch {
        getCapabilitiesApi(isShowProgress, isApiDataCheck, callback)
    }

    private suspend fun getCapabilitiesApi(isShowProgress: Boolean, isApiDataCheck: Boolean = false, callback: ((MutableList<CapabilitiesDataItem>) -> Unit)? = null) {
        if (isShowProgress) showProgress()
        val helper = colorResources.themeHelper
        val capabilities = helper.getCapabilities()
        if (!isApiDataCheck && capabilities.isNotEmpty()) {
            capabilitiesList.postValue(capabilities)
            callback?.invoke(capabilities)
        } else {
            performApiCalls(
                { repository.remote.getCapabilities() }
            ) { responses, isSuccess ->
                if (isSuccess) {
                    val response = responses[0] as NSCapabilitiesResponse?
                    helper.setCapabilities(response?.data ?: arrayListOf())
                    capabilitiesList.postValue(helper.getCapabilities())
                    callback?.invoke(helper.getCapabilities())
                } else {
                    callback?.invoke(helper.getCapabilities())
                }
            }
        }
    }


    fun getFleets(callback: ((MutableList<FleetData>) -> Unit)? = null) = viewModelScope.launch {
        getFleetsApi(callback)
    }

    private suspend fun getFleetsApi(callback: ((MutableList<FleetData>) -> Unit)? = null) {
        performApiCalls(
            { repository.remote.getFleetList() }
        ) { responses, isSuccess ->
            if (isSuccess) {
                val response = responses[0] as FleetListResponse?
                val list: MutableList<FleetData> = arrayListOf()
                list.addAll(response?.data ?: arrayListOf())
                viewModelScope.launch {
                    val sortedList = withContext(Dispatchers.Default) {
                        list.apply {
                            sortByDescending { it.vendorId }
                        }
                    }
                    callback?.invoke(sortedList)
                }
            } else {
                callback?.invoke(arrayListOf())
            }
        }
    }

    fun getBaseServiceList(isSwipeRefresh: Boolean, callback: (NSGetServiceListResponse?) -> Unit) = viewModelScope.launch {
        getServiceListApi(isSwipeRefresh, callback)
    }

    private suspend fun getServiceListApi(isSwipeRefresh: Boolean, callback: (NSGetServiceListResponse?) -> Unit) {
        val helper = colorResources.themeHelper
        val serviceResponse = helper.getServiceResponse()
        if (serviceResponse != null && isSwipeRefresh) {
            callback.invoke(serviceResponse)
        } else {
            performApiCalls(
                { repository.remote.getServiceList() }
            ) { responses, isSuccess ->
                if (isSuccess) {
                    val response = responses[0] as NSGetServiceListResponse?
                    helper.setServiceResponse(response)
                    callback.invoke(helper.getServiceResponse())
                } else {
                    callback.invoke(helper.getServiceResponse())
                }
            }
        }
    }

    suspend fun getTypesFilterSelectedAsync(list: MutableList<ActiveInActiveFilter>): MutableList<String> = coroutineScope {
        val selectedFilterListDeferred = async {
            getTypesFilterSelected(list)
        }
        selectedFilterListDeferred.await()
    }

    private fun getTypesFilterSelected(list: MutableList<ActiveInActiveFilter>): MutableList<String> {
        val selectedFilterList: MutableList<String> = arrayListOf()
        for (data in list) {
            if (data.isActive && data.key != NSConstants.ALL) {
                if (data.title?.isNotEmpty() == true) {
                    selectedFilterList.add(data.title ?: "")
                }
            }
        }
        return selectedFilterList
    }

    fun getVendorInfo(vendorId: String, callback: ((VendorDetailResponse?) -> Unit)) = viewModelScope.launch {
        val vendorInfo = colorResources.themeHelper.getVendorDetailResponse(vendorId)
        if (vendorInfo == null) {
            getVendorInfoApi(vendorId, callback)
        } else {
            callback.invoke(vendorInfo)
        }
    }

    private suspend fun getVendorInfoApi(vendorId: String, callback: ((VendorDetailResponse?) -> Unit)) {
        performApiCalls(
            { repository.remote.getVendorInfo(vendorId)
            }
        ) { responses, isSuccess ->
            if (isSuccess) {
                val vendorInfo = responses[0] as VendorDetailResponse?
                colorResources.themeHelper.setVendorDetailResponse(vendorInfo)
                callback.invoke(vendorInfo)
            } else {
                callback.invoke(VendorDetailResponse())
            }
        }
    }

    fun getFleetList(callback: ((MutableList<FleetData>) -> Unit)) = viewModelScope.launch {
        getFleetListApi(callback)
    }

    private suspend fun getFleetListApi(callback: ((MutableList<FleetData>) -> Unit)) {
        performApiCalls(
            { repository.remote.getFleetList() }
        ) { responses, isSuccess ->
            if (isSuccess) {
                val fleets = responses[0] as FleetListResponse?
                val list: MutableList<FleetData> = arrayListOf()
                list.addAll(fleets?.data ?: arrayListOf())
                viewModelScope.launch {
                    val sortedList = withContext(Dispatchers.Default) {
                        list.apply {
                            sortByDescending { it.vendorId }
                        }
                    }
                    callback.invoke(sortedList)
                }
            } else {
                callback.invoke(arrayListOf())
            }
        }
    }

    fun getLocalLanguages(serviceId: String = NSThemeHelper.SERVICE_ID, callback: (() -> Unit)? = null) = viewModelScope.launch {
        getLocalLanguage(serviceId, callback)
    }

    private suspend fun getLocalLanguage(serviceId: String = NSThemeHelper.SERVICE_ID, callback: (() -> Unit)? = null) {
        performApiCalls(
            { repository.remote.listLocalLanguage(NSLanguageRequest(serviceId)) }
        ) { responses, isSuccess ->
            if (isSuccess) {
                val localLanguage = responses[0] as NSLocalLanguageResponse?
                colorResources.themeHelper.setFleetLanguageList(
                    localLanguage?.data ?: arrayListOf(), true
                )

                val map: HashMap<String, MutableList<LanguageSelectModel>> = hashMapOf()
                map[serviceId] = localLanguage?.data ?: arrayListOf()
                colorResources.themeHelper.setMapLocalLanguage(map)
            }
            callback?.invoke()
        }
    }

    fun getRegion(callback: (MutableList<RegionDataItem>) -> Unit) = viewModelScope.launch {
        getRegionApi(callback)
    }

    private suspend fun getRegionApi(callback: (MutableList<RegionDataItem>) -> Unit) {
        performApiCalls(
            { repository.remote.getRegions() }
        ) { responses, isSuccess ->
            if (isSuccess) {
                val regionResponse = responses[0] as RegionResponse?
                callback.invoke(regionResponse?.regions ?: arrayListOf())
            } else {
                callback.invoke(arrayListOf())
            }
        }
    }
}