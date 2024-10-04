package com.nyotek.dot.admin.data

import com.nyotek.dot.admin.models.requests.CreateSocialRequest
import com.nyotek.dot.admin.models.requests.NSAddEmployeeRequest
import com.nyotek.dot.admin.models.requests.NSAddressRequest
import com.nyotek.dot.admin.models.requests.NSAssignVehicleRequest
import com.nyotek.dot.admin.models.requests.NSCapabilitiesRequest
import com.nyotek.dot.admin.models.requests.NSCreateCapabilityRequest
import com.nyotek.dot.admin.models.requests.NSCreateCompanyRequest
import com.nyotek.dot.admin.models.requests.NSCreateFleetAddressRequest
import com.nyotek.dot.admin.models.requests.NSCreateServiceRequest
import com.nyotek.dot.admin.models.requests.NSEditAddressRequest
import com.nyotek.dot.admin.models.requests.NSEmployeeEditRequest
import com.nyotek.dot.admin.models.requests.NSEmployeeListRequest
import com.nyotek.dot.admin.models.requests.NSEmployeeRequest
import com.nyotek.dot.admin.models.requests.NSFleetAddRemoveTagsRequest
import com.nyotek.dot.admin.models.requests.NSFleetDriverRequest
import com.nyotek.dot.admin.models.requests.NSFleetLogoScaleRequest
import com.nyotek.dot.admin.models.requests.NSFleetLogoUpdateRequest
import com.nyotek.dot.admin.models.requests.NSFleetNameUpdateRequest
import com.nyotek.dot.admin.models.requests.NSFleetRequest
import com.nyotek.dot.admin.models.requests.NSFleetServiceIdsUpdateRequest
import com.nyotek.dot.admin.models.requests.NSFleetSloganUpdateRequest
import com.nyotek.dot.admin.models.requests.NSFleetUpdateTagsRequest
import com.nyotek.dot.admin.models.requests.NSFleetUrlUpdateRequest
import com.nyotek.dot.admin.models.requests.NSLanguageLocaleRequest
import com.nyotek.dot.admin.models.requests.NSLanguageRequest
import com.nyotek.dot.admin.models.requests.NSLoginRequest
import com.nyotek.dot.admin.models.requests.NSRefreshTokenRequest
import com.nyotek.dot.admin.models.requests.NSSearchMobileRequest
import com.nyotek.dot.admin.models.requests.NSSearchUserRequest
import com.nyotek.dot.admin.models.requests.NSServiceCapabilitiesRequest
import com.nyotek.dot.admin.models.requests.NSServiceFleetUpdateRequest
import com.nyotek.dot.admin.models.requests.NSServiceRequest
import com.nyotek.dot.admin.models.requests.NSUpdateCapabilitiesRequest
import com.nyotek.dot.admin.models.requests.NSUpdateStatusRequest
import com.nyotek.dot.admin.models.requests.NSVehicleDeleteRequest
import com.nyotek.dot.admin.models.requests.NSVehicleEnableDisableRequest
import com.nyotek.dot.admin.models.requests.NSVehicleNotesRequest
import com.nyotek.dot.admin.models.requests.NSVehicleRequest
import com.nyotek.dot.admin.models.requests.NSVehicleUpdateImageRequest
import com.nyotek.dot.admin.models.responses.BootStrapModel
import com.nyotek.dot.admin.models.responses.DispatchDetailResponse
import com.nyotek.dot.admin.models.responses.DispatchRequestListResponse
import com.nyotek.dot.admin.models.responses.DriverListModel
import com.nyotek.dot.admin.models.responses.FleetListResponse
import com.nyotek.dot.admin.models.responses.FleetLocationResponse
import com.nyotek.dot.admin.models.responses.FleetSingleResponse
import com.nyotek.dot.admin.models.responses.GetAddressResponse
import com.nyotek.dot.admin.models.responses.NSAssignVehicleDriverResponse
import com.nyotek.dot.admin.models.responses.NSBlankDataResponse
import com.nyotek.dot.admin.models.responses.NSCapabilitiesBlankDataResponse
import com.nyotek.dot.admin.models.responses.NSCapabilitiesResponse
import com.nyotek.dot.admin.models.responses.NSCreateCompanyResponse
import com.nyotek.dot.admin.models.responses.NSCreateFleetAddressResponse
import com.nyotek.dot.admin.models.responses.NSCreateServiceResponse
import com.nyotek.dot.admin.models.responses.NSDispatchOrderListResponse
import com.nyotek.dot.admin.models.responses.NSDocumentListResponse
import com.nyotek.dot.admin.models.responses.NSDriverVehicleDetailResponse
import com.nyotek.dot.admin.models.responses.NSEmployeeAddDeleteBlankDataResponse
import com.nyotek.dot.admin.models.responses.NSEmployeeBlankDataResponse
import com.nyotek.dot.admin.models.responses.NSEmployeeResponse
import com.nyotek.dot.admin.models.responses.NSErrorResponse
import com.nyotek.dot.admin.models.responses.NSFleetBlankDataResponse
import com.nyotek.dot.admin.models.responses.NSGetServiceListResponse
import com.nyotek.dot.admin.models.responses.NSListJobTitleResponse
import com.nyotek.dot.admin.models.responses.NSLocalLanguageResponse
import com.nyotek.dot.admin.models.responses.NSLogoutResponse
import com.nyotek.dot.admin.models.responses.NSServiceCapabilityResponse
import com.nyotek.dot.admin.models.responses.NSSocialResponse
import com.nyotek.dot.admin.models.responses.NSUpdateFleetLogoResponse
import com.nyotek.dot.admin.models.responses.NSUploadFileResponse
import com.nyotek.dot.admin.models.responses.NSUserDetailResponse
import com.nyotek.dot.admin.models.responses.NSUserListResponse
import com.nyotek.dot.admin.models.responses.NSUserResponse
import com.nyotek.dot.admin.models.responses.NSVehicleAssignBlankDataResponse
import com.nyotek.dot.admin.models.responses.NSVehicleBlankDataResponse
import com.nyotek.dot.admin.models.responses.NSVehicleDetailResponse
import com.nyotek.dot.admin.models.responses.NSVehicleResponse
import com.nyotek.dot.admin.models.responses.RegionResponse
import com.nyotek.dot.admin.models.responses.VendorDetailResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named

class RemoteDataSource @Inject constructor(
    @Named("base_url_main") private val baseMainUrl: NSApiInterface,
    @Named("base_url_location") private val baseLocationUrl: NSApiInterface,
    @Named("base_url_fleet") private val baseFleetUrl: NSApiInterface
) {

    suspend fun userDetail(): Response<NSUserDetailResponse> {
        return baseMainUrl.userDetail()
    }

    suspend fun loginWithEmailPassword(nsLoginRequest: NSLoginRequest): Response<NSUserResponse> {
        return baseMainUrl.loginWithEmailPassword(nsLoginRequest)
    }

    suspend fun logout(): Response<NSLogoutResponse> {
        return baseMainUrl.logout()
    }

    suspend fun refreshToken(tokenRequest: NSRefreshTokenRequest): Response<NSUserResponse> {
        return baseMainUrl.refreshToken(tokenRequest)
    }
    
    suspend fun getBootStrap(): Response<BootStrapModel> {
        return baseMainUrl.getBootStrap()
    }

    suspend fun getServiceList(): Response<NSGetServiceListResponse> {
        return baseMainUrl.getServiceList()
    }

    suspend fun createService(nsCreateServiceRequest: NSCreateServiceRequest): Response<NSCreateServiceResponse> {
        return baseMainUrl.createService(nsCreateServiceRequest)
    }

    suspend fun disableService(serviceRequest: NSServiceRequest): Response<NSBlankDataResponse> {
        return baseMainUrl.disableService(serviceRequest)
    }

    suspend fun enableService(serviceRequest: NSServiceRequest): Response<NSBlankDataResponse> {
        return baseMainUrl.enableService(serviceRequest)
    }

    suspend fun uploadFile(myFile: MultipartBody.Part): Response<NSUploadFileResponse> {
        return baseMainUrl.uploadFile(myFile)
    }

    suspend fun getFleetList(): Response<FleetListResponse> {
        return baseMainUrl.getFleetList()
    }

    suspend fun disableFleet(vendorRequest: NSFleetRequest): Response<NSFleetBlankDataResponse> {
        return baseMainUrl.disableFleet(vendorRequest)
    }

    suspend fun enableFleet(vendorRequest: NSFleetRequest): Response<NSFleetBlankDataResponse> {
        return baseMainUrl.enableFleet(vendorRequest)
    }

    suspend fun getAddress(addressRequest: NSAddressRequest): Response<GetAddressResponse> {
        return baseMainUrl.getAddress(addressRequest)
    }

    suspend fun createFleetAddress(createVendorAddressRequest: NSCreateFleetAddressRequest): Response<NSCreateFleetAddressResponse> {
        return baseMainUrl.createFleetAddress(createVendorAddressRequest)
    }

    suspend fun updateFleetName(vendorNameRequest: NSFleetNameUpdateRequest): Response<NSFleetBlankDataResponse> {
        return baseMainUrl.updateFleetName(vendorNameRequest)
    }

    suspend fun updateFleetLogo(vendorLogoRequest: NSFleetLogoUpdateRequest): Response<NSUpdateFleetLogoResponse> {
        return baseMainUrl.updateFleetLogo(vendorLogoRequest)
    }

    suspend fun updateFleetScale(fleetLogoRequest: NSFleetLogoScaleRequest): Response<NSFleetBlankDataResponse> {
        return baseMainUrl.updateFleetScale(fleetLogoRequest)
    }

    suspend fun createFleet(createCompanyRequest: NSCreateCompanyRequest): Response<NSCreateCompanyResponse> {
        return baseMainUrl.createFleet(createCompanyRequest)
    }

    suspend fun editAddress(editAddressRequest: NSEditAddressRequest): Response<NSBlankDataResponse> {
        return baseMainUrl.editAddress(editAddressRequest)
    }

    suspend fun updateFleetSlogan(vendorSloganRequest: NSFleetSloganUpdateRequest): Response<NSFleetBlankDataResponse> {
        return baseMainUrl.updateFleetSlogan(vendorSloganRequest)
    }

    suspend fun updateFleetUrl(vendorUrlRequest: NSFleetUrlUpdateRequest): Response<NSFleetBlankDataResponse> {
        return baseMainUrl.updateFleetUrl(vendorUrlRequest)
    }

    suspend fun updateFleetServiceIds(vendorServiceIdsRequest: NSFleetServiceIdsUpdateRequest): Response<NSFleetBlankDataResponse> {
        return baseMainUrl.updateFleetServiceIds(vendorServiceIdsRequest)
    }

    suspend fun updateFleetTags(vendorTagUpdateRequest: NSFleetUpdateTagsRequest): Response<NSFleetBlankDataResponse> {
        return baseMainUrl.updateFleetTags(vendorTagUpdateRequest)
    }
    
    suspend fun addFleetTags(vendorTagUpdateRequest: NSFleetAddRemoveTagsRequest): Response<NSFleetBlankDataResponse> {
        return baseMainUrl.addFleetTags(vendorTagUpdateRequest)
    }
    
    suspend fun removeFleetTags(vendorTagUpdateRequest: NSFleetAddRemoveTagsRequest): Response<NSFleetBlankDataResponse> {
        return baseMainUrl.removeFleetTags(vendorTagUpdateRequest)
    }

    suspend fun getFleetDetails(vendorRequest: NSFleetRequest): Response<FleetSingleResponse> {
        return baseMainUrl.getFleetDetails(vendorRequest)
    }

    suspend fun listLocalLanguage(languageRequest: NSLanguageRequest): Response<NSLocalLanguageResponse> {
        return baseMainUrl.listLocalLanguage(languageRequest)
    }

    suspend fun searchUserName(searchRequest: NSSearchUserRequest): Response<NSUserListResponse> {
        return baseMainUrl.searchUserName(searchRequest)
    }

    suspend fun searchPhone(searchRequest: NSSearchMobileRequest): Response<NSUserListResponse> {
        return baseMainUrl.searchPhone(searchRequest)
    }

    suspend fun getListOfJobTitle(serviceId: String): Response<NSListJobTitleResponse> {
        return baseMainUrl.getListOfJobTitle(serviceId)
    }

    suspend fun getListEmployees(employeeRequest: NSEmployeeListRequest): Response<NSEmployeeResponse> {
        return baseMainUrl.getListEmployees(employeeRequest)
    }

    suspend fun disableEmployee(vendorRequest: NSEmployeeRequest): Response<NSEmployeeBlankDataResponse> {
        return baseMainUrl.disableEmployee(vendorRequest)
    }

    suspend fun enableEmployee(vendorRequest: NSEmployeeRequest): Response<NSEmployeeBlankDataResponse> {
        return baseMainUrl.enableEmployee(vendorRequest)
    }

    suspend fun addEmployee(vendorRequest: NSAddEmployeeRequest): Response<NSEmployeeAddDeleteBlankDataResponse> {
        return baseMainUrl.addEmployee(vendorRequest)
    }

    suspend fun employeeDelete(vendorRequest: NSEmployeeRequest): Response<NSEmployeeAddDeleteBlankDataResponse> {
        return baseMainUrl.employeeDelete(vendorRequest)
    }

    suspend fun employeeEdit(employeeRequest: NSEmployeeEditRequest): Response<NSEmployeeAddDeleteBlankDataResponse> {
        return baseMainUrl.employeeEdit(employeeRequest)
    }

    suspend fun dispatchDrivers(driverId: String): Response<NSDispatchOrderListResponse> {
        return baseMainUrl.dispatchDrivers(driverId)
    }

    suspend fun dispatchFromService(serviceId: String): Response<NSDispatchOrderListResponse> {
        return baseMainUrl.dispatchFromService(serviceId)
    }

    suspend fun dispatchDetail(dispatchId: String): Response<DispatchDetailResponse> {
        return baseMainUrl.dispatchDetail(dispatchId)
    }

    suspend fun updateDispatchOrderStatus(orderId: String, nsUpdateStatusRequest: NSUpdateStatusRequest): Response<NSBlankDataResponse> {
        return baseMainUrl.updateDispatchOrderStatus(orderId, nsUpdateStatusRequest)
    }

    suspend fun getVendorInfo(id: String): Response<VendorDetailResponse> {
        return baseMainUrl.getVendorInfo(id)
    }

    suspend fun getRegions(): Response<RegionResponse> {
        return baseMainUrl.getRegions()
    }

    suspend fun assignDriver(orderId: String, map: HashMap<String, String>): Response<NSErrorResponse> {
        return baseMainUrl.assignDriver(orderId, map)
    }

    suspend fun getSocialInfo(): Response<NSSocialResponse> {
        return baseMainUrl.getSocialInfo()
    }

    suspend fun createSocialInfo(request: CreateSocialRequest): Response<NSSocialResponse> {
        return baseMainUrl.createSocialInfo(request)
    }

    suspend fun updateSocialProfileImage(request: HashMap<String, String>): Response<NSErrorResponse> {
        return baseMainUrl.updateSocialProfileImage(request)
    }

    suspend fun updateFirstName(request: HashMap<String, String>): Response<NSErrorResponse> {
        return baseMainUrl.updateFirstName(request)
    }

    suspend fun updateLastName(request: HashMap<String, String>): Response<NSErrorResponse> {
        return baseMainUrl.updateLastName(request)
    }

    suspend fun updateDob(request: HashMap<String, Int>): Response<NSErrorResponse> {
        return baseMainUrl.updateDob(request)
    }

    suspend fun updateBiography(request: HashMap<String, String>): Response<NSErrorResponse> {
        return baseMainUrl.updateBiography(request)
    }

    suspend fun setLocal(languageRequest: NSLanguageLocaleRequest): Response<NSErrorResponse> {
        return baseMainUrl.setLocal(languageRequest)
    }

    /*----------------------------------------------------------------------------------------------------------------------------------------------*/
    //Location Services
    /*----------------------------------------------------------------------------------------------------------------------------------------------*/

    suspend fun getFleetLocation(fieldId: String): Response<FleetLocationResponse> {
        return baseLocationUrl.getFleetLocation(fieldId)
    }

    suspend fun getFleetDriverLocation(request: NSFleetDriverRequest): Response<FleetLocationResponse> {
        return baseLocationUrl.getFleetDriverLocation(request)
    }

    suspend fun getDriverLocation(driverId: String): Response<FleetLocationResponse> {
        return baseLocationUrl.getDriverLocation(driverId)
    }

    suspend fun getLocationHistoryDispatch(dispatchId: String): Response<FleetLocationResponse> {
        return baseLocationUrl.getLocationHistoryDispatch(dispatchId)
    }

    suspend fun getDispatchRequestDetail(id: String): Response<DispatchRequestListResponse> {
        return baseLocationUrl.getDispatchRequestDetail(id)
    }

    suspend fun getDriverList(id: String): Response<DriverListModel> {
        return baseLocationUrl.getDriverList(id)
    }

    /*----------------------------------------------------------------------------------------------------------------------------------------------*/
    // Fleets Services
    /*----------------------------------------------------------------------------------------------------------------------------------------------*/

    suspend fun getCapabilities(): Response<NSCapabilitiesResponse> {
        return baseFleetUrl.getCapabilities()
    }

    suspend fun disableCapabilities(request: NSCapabilitiesRequest): Response<NSFleetBlankDataResponse> {
        return baseFleetUrl.disableCapabilities(request)
    }

    suspend fun enableCapabilities(request: NSCapabilitiesRequest): Response<NSFleetBlankDataResponse> {
        return baseFleetUrl.enableCapabilities(request)
    }

    suspend fun capabilityDelete(id: String): Response<NSCapabilitiesBlankDataResponse> {
        return baseFleetUrl.capabilityDelete(id)
    }

    suspend fun createCapability(request: NSCreateCapabilityRequest): Response<ResponseBody> {
        return baseFleetUrl.createCapability(request)
    }

    suspend fun updateCapability(id: String, request: NSCreateCapabilityRequest): Response<ResponseBody> {
        return baseFleetUrl.updateCapability(id, request)
    }

    suspend fun updateServiceCapability(request: NSServiceCapabilitiesRequest): Response<NSCapabilitiesBlankDataResponse> {
        return baseFleetUrl.updateServiceCapability(request)
    }

    suspend fun getServiceCapability(id: String): Response<NSServiceCapabilityResponse> {
        return baseFleetUrl.getServiceCapability(id)
    }

    suspend fun assignedServiceFleets(request: NSServiceFleetUpdateRequest): Response<ResponseBody> {
        return baseFleetUrl.asignedServiceFleets(request)
    }
    
    suspend fun deleteAssignedServiceFleets(serviceId: String, fleetId: String): Response<ResponseBody> {
        return baseFleetUrl.deleteAssignedServiceFleets(serviceId, fleetId)
    }

    suspend fun createVehicle(request: NSVehicleRequest): Response<ResponseBody> {
        return baseFleetUrl.createVehicle(request)
    }

    suspend fun vehicleList(id: String): Response<NSVehicleResponse> {
        return baseFleetUrl.vehicleList(id)
    }

    suspend fun getAssignVehicleByDriver(id: String, fleetId: String): Response<NSAssignVehicleDriverResponse> {
        return baseFleetUrl.getAssignVehicleByDriver(id, fleetId)
    }

    suspend fun disableVehicle(vendorRequest: NSVehicleEnableDisableRequest): Response<NSVehicleBlankDataResponse> {
        return baseFleetUrl.disableVehicle(vendorRequest)
    }

    suspend fun enableVehicle(vendorRequest: NSVehicleEnableDisableRequest): Response<NSVehicleBlankDataResponse> {
        return baseFleetUrl.enableVehicle(vendorRequest)
    }

    suspend fun vehicleUpdateImage(vendorRequest: NSVehicleUpdateImageRequest): Response<NSVehicleBlankDataResponse> {
        return baseFleetUrl.vehicleUpdateImage(vendorRequest)
    }

    suspend fun updateVehicleNotes(vendorRequest: NSVehicleNotesRequest): Response<NSVehicleBlankDataResponse> {
        return baseFleetUrl.updateVehicleNotes(vendorRequest)
    }

    suspend fun updateVehicleCapability(request: NSUpdateCapabilitiesRequest): Response<NSVehicleBlankDataResponse> {
        return baseFleetUrl.updateVehicleCapability(request)
    }

    suspend fun getVehicleDetail(id: String): Response<NSVehicleDetailResponse> {
        return baseFleetUrl.getVehicleDetail(id)
    }

    suspend fun getDriverVehicleDetail(id: String): Response<NSDriverVehicleDetailResponse> {
        return baseFleetUrl.getDriverVehicleDetail(id)
    }

    suspend fun assignVehicle(request: NSAssignVehicleRequest): Response<NSVehicleAssignBlankDataResponse> {
        return baseFleetUrl.assignVehicle(request)
    }

    suspend fun deleteVehicle(request: NSVehicleDeleteRequest): Response<NSVehicleAssignBlankDataResponse> {
        return baseFleetUrl.deleteVehicle(request)
    }

    suspend fun getDriverDocumentInfo(id: String): Response<NSDocumentListResponse> {
        return baseFleetUrl.getDriverDocumentInfo(id)
    }

}