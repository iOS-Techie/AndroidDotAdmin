package com.nyotek.dot.admin.common

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import com.nyotek.dot.admin.common.utils.isValidList
import com.nyotek.dot.admin.repository.NSCapabilitiesRepository
import com.nyotek.dot.admin.repository.NSFleetRepository
import com.nyotek.dot.admin.repository.NSLanguageRepository
import com.nyotek.dot.admin.repository.network.callbacks.NSGenericViewModelCallback
import com.nyotek.dot.admin.repository.network.responses.ActiveInActiveFilter
import com.nyotek.dot.admin.repository.network.responses.CapabilitiesDataItem
import com.nyotek.dot.admin.repository.network.responses.FleetData
import com.nyotek.dot.admin.repository.network.responses.FleetListResponse
import com.nyotek.dot.admin.repository.network.responses.LanguageSelectModel
import com.nyotek.dot.admin.repository.network.responses.NSCapabilitiesResponse
import com.nyotek.dot.admin.repository.network.responses.NSErrorResponse
import com.nyotek.dot.admin.repository.network.responses.NSLocalLanguageResponse
import org.greenrobot.eventbus.EventBus

/**
 * The base class for all view models which holds methods and members common to all view models
 */
abstract class NSViewModel(mApplication: Application) : AndroidViewModel(mApplication),
    NSGenericViewModelCallback {
    var isProgressShowing = NSSingleLiveEvent<Boolean>()
    val validationErrorId by lazy { NSSingleLiveEvent<String>() }
    val failureErrorMessage: NSSingleLiveEvent<String?> = NSSingleLiveEvent()
    val apiErrors: NSSingleLiveEvent<List<Any>> = NSSingleLiveEvent()
    val noNetworkAlert: NSSingleLiveEvent<Boolean> = NSSingleLiveEvent()
    var isSwipeRefresh = NSSingleLiveEvent<Boolean>()
    var stringResource = NSApplication.getInstance().getStringModel()
    var isClProgressVisible = NSSingleLiveEvent<Boolean>()
    var isFleetTypesAvailable = NSSingleLiveEvent<MutableList<ActiveInActiveFilter>>()

    /**
     * To handle the API failure error and communicate back to UI
     *
     * @param errorMessage The error message to show
     */
    protected fun handleFailure(errorMessage: String?) {
        isProgressShowing.value = false
        failureErrorMessage.value = errorMessage
    }

    /**
     * To handle api error message
     *
     * @param apiErrorList The errorList contains string resource id and string
     */
    protected fun handleError(apiErrorList: List<Any>) {
        isProgressShowing.value = false
        apiErrors.value = apiErrorList
    }

    /**
     * To handle no network
     */
    protected open fun handleNoNetwork() {
        isProgressShowing.value = false
        noNetworkAlert.value = true
    }

    fun setFilterTypes() {
        val activeInActiveFilterList: MutableList<ActiveInActiveFilter> = arrayListOf()
        activeInActiveFilterList.add(
            ActiveInActiveFilter(
                stringResource.all,
                NSConstants.ALL,
                true
            )
        )
        activeInActiveFilterList.add(
            ActiveInActiveFilter(
                stringResource.active,
                NSConstants.ACTIVE,
                false
            )
        )
        activeInActiveFilterList.add(
            ActiveInActiveFilter(
                stringResource.inActive,
                NSConstants.IN_ACTIVE,
                false
            )
        )
        isFleetTypesAvailable.value = activeInActiveFilterList
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

    override fun <T> onSuccess(data: T) {
        isClProgressVisible.value = true
        if (data != null) {
            apiResponse(data)
        } else {
            isProgressShowing.postValue(false)
        }
    }

    override fun onError(errors: List<Any>) {
        EventBus.getDefault().post(NSRefreshEvent())
        handleError(errors)
        isClProgressVisible.value = false
        apiResponse(NSErrorResponse())
    }

    override fun onFailure(failureMessage: String?) {
        EventBus.getDefault().post(NSRefreshEvent())
        handleFailure(failureMessage)
        isClProgressVisible.value = false
        apiResponse(NSErrorResponse())
    }

    override fun <T> onNoNetwork(localData: T) {
        EventBus.getDefault().post(NSRefreshEvent())
        handleNoNetwork()
        isClProgressVisible.value = false
        apiResponse(NSErrorResponse())
    }

    abstract fun apiResponse(data: Any)

    fun showError(message: String) {
        Handler(Looper.getMainLooper()).post {
            val listError: MutableList<String> = arrayListOf()
            listError.add(message)
            handleError(listError)
        }
    }

    fun callCommonApi(
        callback: ((NSGenericViewModelCallback) -> Unit),
        response: ((Any?, Boolean) -> Unit),
        isShowError: Boolean = true
    ) {
        val obj = object : NSGenericViewModelCallback {
            override fun <T> onSuccess(data: T) {
                response.invoke(data, data != null)
            }

            override fun onError(errors: List<Any>) {
                response.invoke(null, false)
                if (isShowError) {
                    handleError(errors)
                }
            }

            override fun onFailure(failureMessage: String?) {
                response.invoke(null, false)
                if (isShowError) {
                    handleFailure(failureMessage)
                }
            }

            override fun <T> onNoNetwork(localData: T) {
                response.invoke(null, false)
                if (isShowError) {
                    handleNoNetwork()
                }
            }
        }
        callback.invoke(obj)
    }

    fun getLocalLanguages(
        serviceId: String = "",
        isSelect: Boolean = false,
        isShowError: Boolean = true,
        callback: ((MutableList<LanguageSelectModel>) -> Unit)? = null
    ) {
        callCommonApi({ obj ->
            NSLanguageRepository.localLanguages(serviceId, obj)
        }, { data, isSuccess ->
            if (!isSuccess) {
                callback?.invoke(arrayListOf())
            } else if (data is NSLocalLanguageResponse) {
                if (isSelect) {
                    if (data.data.isValidList()) {
                        data.data[0].isSelected = true
                    }
                }
                NSApplication.getInstance().setFleetLanguageList(data.data)
                callback?.invoke(data.data)
            }
        }, isShowError)
    }

    fun getCapabilitiesList(
        isShowError: Boolean = true,
        isCapabilityCheck: Boolean = false,
        callback: ((MutableList<CapabilitiesDataItem>) -> Unit)? = null
    ) {
        val list = NSApplication.getInstance().getCapabilityList()
        val capabilityAvailable = list.isValidList()

        if (!capabilityAvailable || !isCapabilityCheck) {
            getCapabilitiesApi(isShowError) {
                callback?.invoke(it)
            }
        } else {
            callback?.invoke(list)
        }
    }

    private fun getCapabilitiesApi(
        isShowError: Boolean = true,
        callback: ((MutableList<CapabilitiesDataItem>) -> Unit)? = null
    ) {
        callCommonApi({ obj ->
            NSCapabilitiesRepository.getCapabilities(obj)
        }, { data, isSuccess ->

            if (!isSuccess) {
                callback?.invoke(arrayListOf())
            } else if (data is NSCapabilitiesResponse) {
                data.data.sortBy { it.id }
                NSApplication.getInstance().setCapabilityList(data.data)
                callback?.invoke(data.data)
            }

        }, isShowError)
    }

    fun getFleetListApi(
        isShowError: Boolean = true,
        callback: ((MutableList<FleetData>) -> Unit)? = null
    ) {
        callCommonApi({ obj ->
            NSFleetRepository.getFleetList(obj)
        }, { data, isSuccess ->

            if (!isSuccess) {
                callback?.invoke(arrayListOf())
            } else if (data is FleetListResponse) {
                (data.data as MutableList<FleetData>).sortByDescending { it.vendorId }
                callback?.invoke(data.data)
            }

        }, isShowError)
    }

    fun showProgress() {
        isProgressShowing.value = true
    }

    fun hideProgress() {
        isProgressShowing.value = false
    }
}