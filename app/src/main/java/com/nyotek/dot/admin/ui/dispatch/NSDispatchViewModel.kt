package com.nyotek.dot.admin.ui.dispatch

import android.app.Application
import com.nyotek.dot.admin.common.NSDateTimeHelper
import com.nyotek.dot.admin.common.NSViewModel
import com.nyotek.dot.admin.common.utils.NSUtilities
import com.nyotek.dot.admin.repository.NSDispatchRepository
import com.nyotek.dot.admin.repository.NSServiceRepository
import com.nyotek.dot.admin.repository.NSVendorRepository
import com.nyotek.dot.admin.repository.network.requests.NSCreateCompanyRequest
import com.nyotek.dot.admin.repository.network.responses.ActiveInActiveFilter
import com.nyotek.dot.admin.repository.network.responses.AllDispatchListResponse
import com.nyotek.dot.admin.repository.network.responses.NSDispatchOrderListData
import com.nyotek.dot.admin.repository.network.responses.NSDispatchOrderListResponse
import com.nyotek.dot.admin.repository.network.responses.NSGetServiceListData
import com.nyotek.dot.admin.repository.network.responses.NSGetServiceListResponse
import com.nyotek.dot.admin.repository.network.responses.VendorDetailResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class NSDispatchViewModel(application: Application) : NSViewModel(application) {
    var createCompanyRequest: NSCreateCompanyRequest = NSCreateCompanyRequest()
    var urlToUpload: String = ""
    var filterList: MutableList<ActiveInActiveFilter> = arrayListOf()
    var selectedServiceId: String? = null
    var selectedServiceLogo: String? = null

    fun getDispatchList(isShowProgress: Boolean, isSwipeRefresh: Boolean, serviceId: String = "", callback: (AllDispatchListResponse) -> Unit) {
        if (isShowProgress) showProgress()
        callCommonApi({ obj ->
            NSDispatchListRepository.getDispatchList(isSwipeRefresh, serviceId, obj)
        }, { data, _ ->
            if (data is AllDispatchListResponse) {
                callback.invoke(data)
            } else {
                hideProgress()
            }
        })
    }

    fun getVendorInfo(vendorId: String, callback: ((VendorDetailResponse?) -> Unit)) {
        if (vendorId.isNotEmpty()) {
            callCommonApi({ obj ->
                NSVendorRepository.getVendorDetail(vendorId, obj)
            }, { data, _ ->
                if (data is VendorDetailResponse) {
                    callback.invoke(data)
                } else {
                    callback.invoke(null)
                }
            })
        } else {
            callback.invoke(null)
        }
    }

    suspend fun filterDataConcurrently(fleetList: MutableList<NSDispatchOrderListData>,
                                               filterTypes: List<String>
    ): MutableList<NSDispatchOrderListData> {
        return coroutineScope {
            // Use async to process each item in fleetList concurrently
            val filteredDataDeferred = fleetList.map { order ->
                async {
                    if (order.status.any { statusFilter ->
                            filterTypes.contains(NSUtilities.capitalizeFirstLetter(statusFilter.status.replace("_", " ")))
                        }) {
                        order
                    } else {
                        null
                    }
                }
            }

            // Wait for all async operations to complete and filter out null results
            val filteredData = filteredDataDeferred.awaitAll().filterNotNull().toMutableList()
            filteredData
        }
    }

    suspend fun filterDataConcurrently(fleetList: MutableList<NSDispatchOrderListData>, searchText: String): MutableList<NSDispatchOrderListData> {
        return coroutineScope {
            // Use async to process each item in fleetList concurrently
            val filteredDataDeferred = fleetList.map { order ->
                async {
                    val userNameMatch = async { order.userMetadata?.userName?.lowercase()?.contains(searchText.lowercase()) ?: false }
                    val userPhoneMatch = async { order.userMetadata?.userPhone?.lowercase()?.contains(searchText.lowercase()) ?: false }

                    // Wait for both async operations to complete and combine the results
                    val userNameMatches = userNameMatch.await()
                    val userPhoneMatches = userPhoneMatch.await()

                    // Return true if either username or user phone matches the search text
                    userNameMatches || userPhoneMatches
                }
            }

            // Wait for all async operations to complete and filter out items that match the search text
            val filteredData = fleetList.filterIndexed { index, _ -> filteredDataDeferred[index].await() }.toMutableList()
            filteredData
        }
    }

    override fun apiResponse(data: Any) {

    }
}