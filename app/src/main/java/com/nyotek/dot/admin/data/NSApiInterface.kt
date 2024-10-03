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
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface NSApiInterface {

    @GET("user/me")
    suspend fun userDetail(): retrofit2.Response<NSUserDetailResponse>

    @POST("login")
    suspend fun loginWithEmailPassword(@Body nsLoginRequest: NSLoginRequest): retrofit2.Response<NSUserResponse>

    @POST("logout")
    suspend fun logout(): retrofit2.Response<NSLogoutResponse>

    @POST("refresh_token")
    suspend fun refreshToken(@Body tokenRequest: NSRefreshTokenRequest): retrofit2.Response<NSUserResponse>
    
    @GET("bmu/bootstrapp")
    suspend fun getBootStrap(): retrofit2.Response<BootStrapModel>

    @GET("bmu/admin/list-services")
    suspend fun getServiceList(): retrofit2.Response<NSGetServiceListResponse>

    @POST("bmu/admin/create-service")
    suspend fun createService(@Body nsCreateServiceRequest: NSCreateServiceRequest): retrofit2.Response<NSCreateServiceResponse>

    @POST("/bmu/admin/disable-service")
    suspend fun disableService(@Body serviceRequest: NSServiceRequest): retrofit2.Response<NSBlankDataResponse>

    @POST("bmu/admin/enable-service")
    suspend fun enableService(@Body serviceRequest: NSServiceRequest): retrofit2.Response<NSBlankDataResponse>

    @Multipart
    @POST("files/upload/")
    suspend fun uploadFile(@Part myFile: MultipartBody.Part): retrofit2.Response<NSUploadFileResponse>

    @GET("social/me")
    suspend fun getSocialInfo(): retrofit2.Response<NSSocialResponse>

    @POST("social/me")
    suspend fun createSocialInfo(@Body request: CreateSocialRequest): retrofit2.Response<NSSocialResponse>

    @PATCH("social/me/profile_pic_url")
    suspend fun updateSocialProfileImage(@Body request: HashMap<String, String>): retrofit2.Response<NSErrorResponse>

    @PATCH("social/me/firstname")
    suspend fun updateFirstName(@Body request: HashMap<String, String>): retrofit2.Response<NSErrorResponse>

    @PATCH("social/me/lastname")
    suspend fun updateLastName(@Body request: HashMap<String, String>): retrofit2.Response<NSErrorResponse>

    @PATCH("social/me/dob")
    suspend fun updateDob(@Body request: HashMap<String, Int>): retrofit2.Response<NSErrorResponse>

    @PATCH("social/me/biography")
    suspend fun updateBiography(@Body request: HashMap<String, String>): retrofit2.Response<NSErrorResponse>

    @POST("user/me/locale")
    suspend fun setLocal(@Body languageRequest: NSLanguageLocaleRequest): retrofit2.Response<NSErrorResponse>

    /*Fleet List*/
    @GET("companies/admin/list_companies_by_service_id")
    suspend fun getFleetList(): retrofit2.Response<FleetListResponse>

    @PATCH("companies/admin/disable_vendor")
    suspend fun disableFleet(@Body vendorRequest: NSFleetRequest): retrofit2.Response<NSFleetBlankDataResponse>

    @PATCH("companies/admin/enable_vendor")
    suspend fun enableFleet(@Body vendorRequest: NSFleetRequest): retrofit2.Response<NSFleetBlankDataResponse>

    @POST("addresses/admin/get_address")
    suspend fun getAddress(@Body addressRequest: NSAddressRequest): retrofit2.Response<GetAddressResponse>

    @POST("addresses/admin/create_vendor_address")
    suspend fun createFleetAddress(@Body createVendorAddressRequest: NSCreateFleetAddressRequest): retrofit2.Response<NSCreateFleetAddressResponse>

    @PATCH("companies/admin/update_vendor_name")
    suspend fun updateFleetName(@Body vendorNameRequest: NSFleetNameUpdateRequest): retrofit2.Response<NSFleetBlankDataResponse>

    @PATCH("companies/admin/update_logo")
    suspend fun updateFleetLogo(@Body vendorLogoRequest: NSFleetLogoUpdateRequest): retrofit2.Response<NSUpdateFleetLogoResponse>

    @PATCH("companies/admin/update_logo_scale")
    suspend fun updateFleetScale(@Body vendorLogoRequest: NSFleetLogoScaleRequest): retrofit2.Response<NSFleetBlankDataResponse>

    @POST("companies/admin/create_company")
    suspend fun createFleet(@Body createCompanyRequest: NSCreateCompanyRequest): retrofit2.Response<NSCreateCompanyResponse>

    @PATCH("addresses/admin/edit_address")
    suspend fun editAddress(@Body editAddressRequest: NSEditAddressRequest): retrofit2.Response<NSBlankDataResponse>

    @PATCH("companies/admin/update_slogan")
    suspend fun updateFleetSlogan(@Body vendorSloganRequest: NSFleetSloganUpdateRequest): retrofit2.Response<NSFleetBlankDataResponse>

    @PATCH("companies/admin/update_url")
    suspend fun updateFleetUrl(@Body vendorUrlRequest: NSFleetUrlUpdateRequest): retrofit2.Response<NSFleetBlankDataResponse>

    @PATCH("companies/admin/update_vendor_service_ids")
    suspend fun updateFleetServiceIds(@Body vendorServiceIdsRequest: NSFleetServiceIdsUpdateRequest): retrofit2.Response<NSFleetBlankDataResponse>

    @PATCH("companies/admin/update_vendor_tags")
    suspend fun updateFleetTags(@Body vendorTagUpdateRequest: NSFleetUpdateTagsRequest): retrofit2.Response<NSFleetBlankDataResponse>

    @POST("companies/admin/vendor_details")
    suspend fun getFleetDetails(@Body vendorRequest: NSFleetRequest): retrofit2.Response<FleetSingleResponse>

    @POST("bmu/list-locales")
    suspend fun listLocalLanguage(@Body languageRequest: NSLanguageRequest): retrofit2.Response<NSLocalLanguageResponse>

    @POST("u/search_username")
    suspend fun searchUserName(@Body searchRequest: NSSearchUserRequest): retrofit2.Response<NSUserListResponse>

    @POST("u/search_phone")
    suspend fun searchPhone(@Body searchRequest: NSSearchMobileRequest): retrofit2.Response<NSUserListResponse>

    @GET("employees/list_job_titles")
    suspend fun getListOfJobTitle(@Header("X-SERVICE-ID") serviceId: String): retrofit2.Response<NSListJobTitleResponse>

    @POST("employees/list_employees")
    suspend fun getListEmployees(@Body employeeRequest: NSEmployeeListRequest): retrofit2.Response<NSEmployeeResponse>

    @PATCH("employees/disable_employee")
    suspend fun disableEmployee(@Body vendorRequest: NSEmployeeRequest): retrofit2.Response<NSEmployeeBlankDataResponse>

    @PATCH("employees/enable_employee")
    suspend fun enableEmployee(@Body vendorRequest: NSEmployeeRequest): retrofit2.Response<NSEmployeeBlankDataResponse>

    @POST("employees/add_employee")
    suspend fun addEmployee(@Body vendorRequest: NSAddEmployeeRequest): retrofit2.Response<NSEmployeeAddDeleteBlankDataResponse>

    @HTTP(method = "DELETE", path = "employees/remove_employee", hasBody = true)
    suspend fun employeeDelete(@Body vendorRequest: NSEmployeeRequest): retrofit2.Response<NSEmployeeAddDeleteBlankDataResponse>

    @PATCH("employees/update_employee_title")
    suspend fun employeeEdit(@Body employeeRequest: NSEmployeeEditRequest): retrofit2.Response<NSEmployeeAddDeleteBlankDataResponse>

    @GET("dispatches/d/{driver_id}")
    suspend fun dispatchDrivers(@Path("driver_id") driverId: String): retrofit2.Response<NSDispatchOrderListResponse>

    @GET("dispatches/s/{service_id}")
    suspend fun dispatchFromService(@Path("service_id") serviceId: String): retrofit2.Response<NSDispatchOrderListResponse>

    @GET("dispatch/{dispatch_id}")
    suspend fun dispatchDetail(@Path("dispatch_id") dispatchId: String): retrofit2.Response<DispatchDetailResponse>

    @POST("dispatch/{dispatch_id}/status")
    suspend fun updateDispatchOrderStatus(@Path("dispatch_id") orderId: String, @Body nsUpdateStatusRequest: NSUpdateStatusRequest): retrofit2.Response<NSBlankDataResponse>

    @GET("companies/vendor_info/{vendor_id}")
    suspend fun getVendorInfo(@Path("vendor_id") id: String): retrofit2.Response<VendorDetailResponse>

    @GET("companies/list_active_regions")
    suspend fun getRegions(): retrofit2.Response<RegionResponse>

    @POST("dispatch/assign/{dispatch_id}")
    suspend fun assignDriver(@Path("dispatch_id") id: String, @Body request: HashMap<String, String>): retrofit2.Response<NSErrorResponse>

    /*----------------------------------------------------------------------------------------------------------------------------------------------*/
    //Location Services
    /*----------------------------------------------------------------------------------------------------------------------------------------------*/

    @GET("location")
    suspend fun getFleetLocation(@Query("fleet_id") fieldId: String): retrofit2.Response<FleetLocationResponse>

    @POST("location/drivers")
    suspend fun getFleetDriverLocation(@Body request: NSFleetDriverRequest): retrofit2.Response<FleetLocationResponse>

    @GET("location/driver/{driver_id}")
    suspend fun getDriverLocation(@Path("driver_id") driverId: String): retrofit2.Response<FleetLocationResponse>

    @GET("location/history/ref/{dispatch_id}")
    suspend fun getLocationHistoryDispatch(@Path("dispatch_id") dispatchId: String): retrofit2.Response<FleetLocationResponse>

    @GET("dispatch/request/{dispatch_id}")
    suspend fun getDispatchRequestDetail(@Path("dispatch_id") id: String): retrofit2.Response<DispatchRequestListResponse>

    /*----------------------------------------------------------------------------------------------------------------------------------------------*/
    // Fleets Services
    /*----------------------------------------------------------------------------------------------------------------------------------------------*/

    @GET("fleets/capability/list")
    suspend fun getCapabilities(): retrofit2.Response<NSCapabilitiesResponse>

    @PATCH("fleets/capability/disable")
    suspend fun disableCapabilities(@Body request: NSCapabilitiesRequest): retrofit2.Response<NSFleetBlankDataResponse>

    @PATCH("fleets/capability/enable")
    suspend fun enableCapabilities(@Body request: NSCapabilitiesRequest): retrofit2.Response<NSFleetBlankDataResponse>

    @DELETE("fleets/capability/{capability_id}")
    suspend fun capabilityDelete(@Path("capability_id") id: String): retrofit2.Response<NSCapabilitiesBlankDataResponse>

    @POST("fleets/capability")
    suspend fun createCapability(@Body request: NSCreateCapabilityRequest): retrofit2.Response<ResponseBody>

    @PATCH("fleets/capability/{capability_id}/label")
    suspend fun updateCapability(@Path("capability_id") id: String, @Body request: NSCreateCapabilityRequest): retrofit2.Response<ResponseBody>

    @PATCH("fleets/servicemanagement/capability")
    suspend fun updateServiceCapability(@Body request: NSServiceCapabilitiesRequest): retrofit2.Response<NSCapabilitiesBlankDataResponse>

    @GET("fleets/servicemanagement/{service_id}")
    suspend fun getServiceCapability(@Path("service_id") id: String): retrofit2.Response<NSServiceCapabilityResponse>

    @POST("fleets/servicemanagement/fleet")
    suspend fun asignedServiceFleets(@Body request: NSServiceFleetUpdateRequest): retrofit2.Response<ResponseBody>
    
    @DELETE("fleets/servicemanagement/{service_id}/f/{fleet_id}")
    suspend fun deleteAssignedServiceFleets(@Path("service_id") serviceId: String, @Path("fleet_id") fleetId: String): retrofit2.Response<ResponseBody>

    @POST("/vehicle")
    suspend fun createVehicle(@Body request: NSVehicleRequest): retrofit2.Response<ResponseBody>

    @GET("vehicle/fleet/{fleet_id}")
    suspend fun vehicleList(@Path("fleet_id") id: String): retrofit2.Response<NSVehicleResponse>

    @GET("driver/{driver_id}/fleet/{fleet_id}/vehicle")
    suspend fun getAssignVehicleByDriver(@Path("driver_id") id: String, @Path("fleet_id") fleetId: String): retrofit2.Response<NSAssignVehicleDriverResponse>

    @PATCH("vehicle/admin/disable")
    suspend fun disableVehicle(@Body vendorRequest: NSVehicleEnableDisableRequest): retrofit2.Response<NSVehicleBlankDataResponse>

    @PATCH("vehicle/admin/enable")
    suspend fun enableVehicle(@Body vendorRequest: NSVehicleEnableDisableRequest): retrofit2.Response<NSVehicleBlankDataResponse>

    @PATCH("vehicle/image")
    suspend fun vehicleUpdateImage(@Body vendorRequest: NSVehicleUpdateImageRequest): retrofit2.Response<NSVehicleBlankDataResponse>

    @PATCH("vehicle/additional-note")
    suspend fun updateVehicleNotes(@Body vendorRequest: NSVehicleNotesRequest): retrofit2.Response<NSVehicleBlankDataResponse>

    @PATCH("vehicle/capability")
    suspend fun updateVehicleCapability(@Body request: NSUpdateCapabilitiesRequest): retrofit2.Response<NSVehicleBlankDataResponse>

    @GET("driver/vehicle/{vehicle_id}")
    suspend fun getVehicleDetail(@Path("vehicle_id") id: String): retrofit2.Response<NSVehicleDetailResponse>

    @GET("vehicle/{vehicle_id}")
    suspend fun getDriverVehicleDetail(@Path("vehicle_id") id: String): retrofit2.Response<NSDriverVehicleDetailResponse>

    @POST("driver/vehicle")
    suspend fun assignVehicle(@Body request: NSAssignVehicleRequest): retrofit2.Response<NSVehicleAssignBlankDataResponse>

    @POST("driver/vehicle")
    suspend fun deleteVehicle(@Body request: NSVehicleDeleteRequest): retrofit2.Response<NSVehicleAssignBlankDataResponse>

    @GET("document/list/{user_id}")
    suspend fun getDriverDocumentInfo(@Path("user_id") id: String): retrofit2.Response<NSDocumentListResponse>

    @GET("location/service/{service_id}")
    suspend fun getDriverList(@Path("service_id") id: String): retrofit2.Response<DriverListModel>
}