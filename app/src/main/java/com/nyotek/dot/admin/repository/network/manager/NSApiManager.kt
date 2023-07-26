package com.nyotek.dot.admin.repository.network.manager

import android.os.Handler
import android.os.Looper
import com.google.gson.GsonBuilder
import com.nyotek.dot.admin.BuildConfig
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSUserManager
import com.nyotek.dot.admin.common.utils.getLocalLanguage
import com.nyotek.dot.admin.repository.network.callbacks.NSRetrofitCallback
import com.nyotek.dot.admin.repository.network.requests.*
import com.nyotek.dot.admin.repository.network.responses.*
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * The manager class through which we can access the web service.
 */
class NSApiManager {
    companion object {
        private val errorCode = NSApplication.getInstance()
        private const val KEY_CONTENT_TYPE = "content-type"
        private const val KEY_ACCEPT = "Accept"
        private const val ACCEPT_VALUE = "*/*"
        private const val KEY_LOCALE = "X-Locale"
        private const val KEY_SERVICE_ID = "X-SERVICE-ID"
        private const val KEY_APP_ID = "X-APP-ID"
        private const val KEY_TIME_ZONE = "x-client-tz"
        private const val KEY_BuildVersion = "X-BuildVersion"
        private const val MULTIPART_JSON = "multipart/form-data"
        private const val APPLICATION_JSON = "application/json"
        private const val AUTHORISATION_KEY = "Authorization"
        private const val BEARER = "Bearer "
        private const val TIMEOUT: Long = 30
        private const val ENDPOINT_3020 = "3020/"
        private const val ENDPOINT_3110 = "3110/"
        private const val ENDPOINT_4554 = "4554/"
        private const val ENDPOINT_3100 = "3100/"

        val unAuthorisedClient: RTApiInterface by lazy {
            buildRetrofit(unAuthorisedOkHttpClient).create(
                RTApiInterface::class.java
            )
        }

        val authorisedClient: RTApiInterface by lazy {
            buildRetrofit(authorizedOkHttpClient).create(
                RTApiInterface::class.java
            )
        }

        val unAuthorised3020Client: RTApiInterface by lazy {
            buildRetrofit(unAuthorisedOkHttpClient).create(
                RTApiInterface::class.java
            )
        }

        val authorised3010Client: RTApiInterface by lazy {
            buildRetrofit(authorizedOkHttpClient).create(
                RTApiInterface::class.java
            )
        }

        val authorised3020Client: RTApiInterface by lazy {
            buildRetrofit(authorizedOkHttpClient).create(
                RTApiInterface::class.java
            )
        }

        val authorised4554Client: RTApiInterface by lazy {
            buildRetrofit(authorizedOkHttpClient).create(
                RTApiInterface::class.java
            )
        }

        val authorised3100Client: RTApiInterface by lazy {
            buildRetrofit(authorizedOkHttpClient).create(
                RTApiInterface::class.java
            )
        }

        val authorisedLocationClient: RTApiInterface by lazy {
            buildRetrofitLocation(authorizedOkHttpClient).create(
                RTApiInterface::class.java
            )
        }

        val authorisedFleetClient: RTApiInterface by lazy {
            buildRetrofitFleet(authorizedOkHttpClient).create(
                RTApiInterface::class.java
            )
        }

        /**
         * OkHttpClient for the Authorised user
         */
        private val authorizedOkHttpClient by lazy { generateOkHttpClient(
            isAuthorizedClient = true,
            isMultiPart = false
        ) }

        /**
         * OkHttpClient for the Authorised user
         */
        private val authorizedLocationOkHttpClient by lazy { generateOkHttpClient(
            isAuthorizedClient = true,
            isMultiPart = false
        ) }

        /**
         * OkHttpClient for the unAuthorised user
         */
        private val unAuthorisedOkHttpClient by lazy { generateOkHttpClient(
            isAuthorizedClient = false,
            isMultiPart = false
        ) }

        /**
         * To provide a http client to send requests to authenticated API
         *
         * @param isAuthorizedClient Whether the client is needed for making authenticated or un authenticated API
         *
         * @return The http client
         */
        private fun generateOkHttpClient(
            isAuthorizedClient: Boolean,
            isMultiPart: Boolean
        ): OkHttpClient =
            OkHttpClient().newBuilder().apply {
                readTimeout(TIMEOUT, TimeUnit.SECONDS)
                connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                addInterceptor { chain ->
                    chain.proceed(
                        getRequest(
                            chain.request(), isAuthorizedClient, isMultiPart
                        )
                    )
                }
                if (isAuthorizedClient) {
                    addInterceptor(RTAuthorizationInterceptor())
                }
                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                }
            }.build()

        /**
         * To builds the Request header
         *
         * @param request            request sent by user
         * @param isAuthorizedClient boolean to create appropriate header with or without authorisation token
         * @return Request with the header loaded
         * @throws IOException possibility of throwing IOException so handled
         */
        @Throws(IOException::class)
        fun getRequest(request: Request, isAuthorizedClient: Boolean, isMultiPart: Boolean): Request =
            request.newBuilder().apply {
                if (isMultiPart) {
                    header(KEY_CONTENT_TYPE, MULTIPART_JSON)
                } else {
                    header(KEY_CONTENT_TYPE, APPLICATION_JSON)
                }
                header(KEY_ACCEPT, ACCEPT_VALUE)
                header(KEY_BuildVersion, BuildConfig.VERSION_CODE.toString())
                if (!request.url.toUrl().path.contains("employees/list_job_titles")) {
                    if (request.url.toUrl().path.contains("wallets/admin/getWalletByUser")) {
                        header(KEY_SERVICE_ID, NSConstants.USER_DETAIL_SERVICE_ID)
                    } else {
                        header(KEY_SERVICE_ID, NSConstants.SERVICE_ID)
                    }
                }
                header(KEY_APP_ID, BuildConfig.THEME_APP_ID)
                header(KEY_LOCALE, getLocalLanguage())
                header(KEY_TIME_ZONE, TimeZone.getDefault().id)
                if (isAuthorizedClient) {
                    header(
                        AUTHORISATION_KEY, BEARER + NSUserManager.getAuthToken()
                    )
                }
            }.build()

        /**
         * To builds the retrofit client with baseUrl and Client sent
         *
         * @param okHttpClient Client with request and header details
         * @return Retrofit reference retrofit builder
         */
        private fun buildRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder().apply {
            val url = if (BuildConfig.IS_BASE_URL_DEBUG) BuildConfig.BASE_URL_DEBUG else BuildConfig.BASE_URL_MAIN
            baseUrl(url)
            client(okHttpClient)
            addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        }.build()

        /**
         * To builds the retrofit client with baseUrl and Client sent
         *
         * @param okHttpClient Client with request and header details
         * @return Retrofit reference retrofit builder
         */
        private fun buildRetrofitLocation(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder().apply {
            val url = BuildConfig.BASE_URL_LOCATION
            baseUrl(url)
            client(okHttpClient)
            addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        }.build()

        /**
         * To builds the retrofit client with baseUrl and Client sent
         *
         * @param okHttpClient Client with request and header details
         * @return Retrofit reference retrofit builder
         */
        private fun buildRetrofitFleet(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder().apply {
            val url = BuildConfig.BASE_URL_FLEET
            baseUrl(url)
            client(okHttpClient)
            addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        }.build()
    }

    /**
     * To check the availability of the network before making the API call and handle no network scenarios
     *
     * @param call     represents the API endpoint call defined in the RTApiInterface
     * @param callback represents the callback via which we communicate back with the caller
     * @param <T>      the type to accept
    </T> */
    private fun <T> request(response: retrofit2.Response<T>, callback: NSRetrofitCallback<T>) {
        if (NSApplication.isNetworkConnected()) {
            callback.onSuspendResponse(response)
        } else {
            Handler(Looper.getMainLooper()).post {
                callback.onNoNetwork()
            }
        }
    }

    /**
     * To cancel all the existing requests at once
     */
    fun cancelAllRequests() {
        authorizedOkHttpClient.dispatcher.cancelAll()
    }

    /**
     * An interceptor to handle the authentication issue. An authentication issue occurs when the API throws error code like 401.
     */
    class RTAuthorizationInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            return chain.proceed(request)
        }
    }

    private fun <T> isNetwork(callback: NSRetrofitCallback<T>): Boolean {
        return if (NSApplication.isNetworkConnected()) {
            true
        } else {
            Handler(Looper.getMainLooper()).post {
                callback.onNoNetwork()
            }
            false
        }
    }

    /**
     * To call the register/login using mobile API endpoint to authenticate the user
     *
     * @param loginRequest The request body
     * @param callback     The callback for the result
     */
    suspend fun loginWithEmailPassword(loginRequest: NSLoginRequest, callback: NSRetrofitCallback<NSUserResponse>) {
        if (isNetwork(callback)) {
            request(unAuthorised3020Client.loginWithEmailPassword(loginRequest), callback)
        }
    }

    /**
     * To call the refresh API endpoint to authenticate the user
     *
     * @param refreshTokenRequest The request body
     * @param callback     The callback for the result
     */
    suspend fun refreshToken(refreshTokenRequest: NSRefreshTokenRequest, callback: NSRetrofitCallback<NSUserResponse>) {
        if (isNetwork(callback)) {
            request(unAuthorised3020Client.refreshToken(refreshTokenRequest), callback)
        }
    }

    /**
     * To call the logout API endpoint
     *
     * @param callback     The callback for the result
     */
    suspend fun logout(callback: NSRetrofitCallback<NSLogoutResponse>) {
        if (isNetwork(callback)) {
            request(authorised3020Client.logout(), callback)
        }
    }

    /**
     * To call the user detail data API
     *
     * @param callback  The callback for the result
     */
    suspend fun getCapabilities(callback: NSRetrofitCallback<NSCapabilitiesResponse>) {
        if (isNetwork(callback)) {
            request(authorisedFleetClient.getCapabilities(), callback)
        }
    }

    /**
     * To call the user detail data API
     *
     * @param callback  The callback for the result
     */
    suspend fun getUserDetailData(callback: NSRetrofitCallback<NSUserDetailResponse>) {
        if (isNetwork(callback)) {
            request(authorised3020Client.userDetail(), callback)
        }
    }

    /*Service List*/

    /**
     * To call the create service data API
     *
     * @param callback  The callback for the result
     */
    suspend fun createService(nsCreateServiceRequest: NSCreateServiceRequest, callback: NSRetrofitCallback<NSCreateServiceResponse>) {
        if (isNetwork(callback)) {
            request(authorised3020Client.createService(nsCreateServiceRequest), callback)
        }
    }

    /**
     * To call the disable service data API
     *
     * @param callback  The callback for the result
     */
    suspend fun disableService(serviceRequest: NSServiceRequest, callback: NSRetrofitCallback<NSBlankDataResponse>) {
        if (isNetwork(callback)) {
            request(authorised3020Client.disableService(serviceRequest), callback)
        }
    }

    /**
     * To call the enable service data API
     *
     * @param callback  The callback for the result
     */
    suspend fun enableService(serviceRequest: NSServiceRequest, callback: NSRetrofitCallback<NSBlankDataResponse>) {
        if (isNetwork(callback)) {
            request(authorised3020Client.enableService(serviceRequest), callback)
        }
    }

    /*Themes*/
    /**
     * To call the get app theme data API
     *
     * @param nsAppThemeRequest App Theme request
     * @param callback  The callback for the result
     */
    suspend fun getAppTheme(nsAppThemeRequest: NSAppThemeRequest, callback: NSRetrofitCallback<NSGetThemeModel>) {
        if (isNetwork(callback)) {
            request(authorised3020Client.getAppTheme(nsAppThemeRequest), callback)
        }
    }

    /**
     * To call the upload file data API
     *
     * @param file upload image file
     * @param callback  The callback for the result
     */
    suspend fun uploadFileData(file: MultipartBody.Part, callback: NSRetrofitCallback<NSUploadFileResponse>) {
        if (isNetwork(callback)) {
            request(authorised3020Client.uploadFile(file), callback)
        }
    }

    /**
     * To call the update theme color by service data API
     *
     * @param callback  The callback for the result
     */
    suspend fun getFleetList(callback: NSRetrofitCallback<FleetListResponse>) {
        if (isNetwork(callback)) {
            request(authorised3020Client.getFleetList(), callback)
        }
    }

    /**
     * To call the disable service data API
     *
     * @param callback  The callback for the result
     */
    suspend fun disableFleet(vendorRequest: NSFleetRequest, callback: NSRetrofitCallback<NSFleetBlankDataResponse>) {
        if (isNetwork(callback)) {
            request(authorised3020Client.disableFleet(vendorRequest), callback)
        }
    }

    /**
     * To call the enable service data API
     *
     * @param callback  The callback for the result
     */
    suspend fun enableFleet(vendorRequest: NSFleetRequest, callback: NSRetrofitCallback<NSFleetBlankDataResponse>) {
        if (isNetwork(callback)) {
            request(authorised3020Client.enableFleet(vendorRequest), callback)
        }
    }

    /**
     * To call the enable service data API
     *
     * @param callback  The callback for the result
     */
    suspend fun getAddress(addressRequest: NSAddressRequest, callback: NSRetrofitCallback<GetAddressResponse>) {
        if (isNetwork(callback)) {
            request(authorised3020Client.getAddress(addressRequest), callback)
        }
    }

    /**
     * To call the createVendorAddress data API
     *
     * @param callback  The callback for the result
     */
    suspend fun createFleetAddress(request: NSCreateFleetAddressRequest, callback: NSRetrofitCallback<NSCreateFleetAddressResponse>) {
        if (isNetwork(callback)) {
            request(authorised3020Client.createFleetAddress(request), callback)
        }
    }

    /**
     * To call the enable service data API
     *
     * @param callback  The callback for the result
     */
    suspend fun updateFleetLogo(updateRequest: NSFleetLogoUpdateRequest, callback: NSRetrofitCallback<NSUpdateFleetLogoResponse>) {
        if (isNetwork(callback)) {
            request(authorised3020Client.updateFleetLogo(updateRequest), callback)
        }
    }

    /**
     * To call the enable service data API
     *
     * @param callback  The callback for the result
     */
    suspend fun updateFleetScaleLogo(updateRequest: NSFleetLogoScaleRequest, callback: NSRetrofitCallback<NSFleetBlankDataResponse>) {
        if (isNetwork(callback)) {
            request(authorised3020Client.updateFleetScale(updateRequest), callback)
        }
    }

    /**
     * To call the create_company data API
     *
     * @param callback  The callback for the result
     */
    suspend fun createFleet(request: NSCreateCompanyRequest, callback: NSRetrofitCallback<NSCreateCompanyResponse>) {
        if (isNetwork(callback)) {
            request(authorised3020Client.createFleet(request), callback)
        }
    }

    /**
     * To call the enable service data API
     *
     * @param callback  The callback for the result
     */
    suspend fun editAddress(editAddressRequest: NSEditAddressRequest, callback: NSRetrofitCallback<NSBlankDataResponse>) {
        if (isNetwork(callback)) {
            request(authorised3020Client.editAddress(editAddressRequest), callback)
        }
    }

    /**
     * To call the enable service data API
     *
     * @param callback  The callback for the result
     */
    suspend fun updateFleetName(updateRequest: NSFleetNameUpdateRequest, callback: NSRetrofitCallback<NSFleetBlankDataResponse>) {
        if (isNetwork(callback)) {
            request(authorised3020Client.updateFleetName(updateRequest), callback)
        }
    }

    /**
     * To call the enable service data API
     *
     * @param callback  The callback for the result
     */
    suspend fun updateFleetSlogan(updateRequest: NSFleetSloganUpdateRequest, callback: NSRetrofitCallback<NSFleetBlankDataResponse>) {
        if (isNetwork(callback)) {
            request(authorised3020Client.updateFleetSlogan(updateRequest), callback)
        }
    }

    /**
     * To call the enable service data API
     *
     * @param callback  The callback for the result
     */
    suspend fun updateFleetUrl(updateRequest: NSFleetUrlUpdateRequest, callback: NSRetrofitCallback<NSFleetBlankDataResponse>) {
        if (isNetwork(callback)) {
            request(authorised3020Client.updateFleetUrl(updateRequest), callback)
        }
    }

    /**
     * To call the enable service data API
     *
     * @param callback  The callback for the result
     */
    suspend fun updateFleetServiceIds(updateRequest: NSFleetServiceIdsUpdateRequest, callback: NSRetrofitCallback<NSFleetBlankDataResponse>) {
        if (isNetwork(callback)) {
            request(authorised3020Client.updateFleetServiceIds(updateRequest), callback)
        }
    }

    /**
     * To call the enable service data API
     *
     * @param callback  The callback for the result
     */
    suspend fun updateFleetTags(updateRequest: NSFleetUpdateTagsRequest, callback: NSRetrofitCallback<NSFleetBlankDataResponse>) {
        if (isNetwork(callback)) {
            request(authorised3020Client.updateFleetTags(updateRequest), callback)
        }
    }

    /**
     * To call the enable service data API
     *
     * @param callback  The callback for the result
     */
    suspend fun getFleetDetail(request: NSFleetRequest, callback: NSRetrofitCallback<FleetSingleResponse>) {
        if (isNetwork(callback)) {
            request(authorised3020Client.getFleetDetails(request), callback)
        }
    }

    suspend fun listLocalLanguages(languageRequest: NSLanguageRequest, callback: NSRetrofitCallback<NSLocalLanguageResponse>) {
        if (isNetwork(callback)) {
            request(authorised4554Client.listLocalLanguage(languageRequest), callback)
        }
    }

    /**
     * To call the get app LanguageString data API
     *
     * @param languageStringRequest App Theme request
     * @param callback  The callback for the result
     */
    suspend fun getLanguageString(languageStringRequest: NSLanguageStringRequest, callback: NSRetrofitCallback<NSLanguageStringResponse>) {
        if (isNetwork(callback)) {
            request(authorised4554Client.getLanguageString(languageStringRequest), callback)
        }
    }

    /**
     * To call the search username data API
     *
     * @param callback  The callback for the result
     */
    suspend fun searchUserName(searchRequest: NSSearchUserRequest, callback: NSRetrofitCallback<NSUserListResponse>) {
        if (isNetwork(callback)) {
            request(authorised3020Client.searchUserName(searchRequest), callback)
        }
    }

    /**
     * To call the search phone data API
     *
     * @param callback  The callback for the result
     */
    suspend fun searchPhone(searchRequest: NSSearchMobileRequest, callback: NSRetrofitCallback<NSUserListResponse>) {
        if (isNetwork(callback)) {
            request(authorised3020Client.searchPhone(searchRequest), callback)
        }
    }

    /**
     * To call the job title data API
     *
     * @param callback  The callback for the result
     */
    suspend fun getJobTitle(serviceId: String, callback: NSRetrofitCallback<NSListJobTitleResponse>) {
        if (isNetwork(callback)) {
            request(authorised3020Client.getListOfJobTitle(serviceId), callback)
        }
    }

    /**
     * To call the job title data API
     *
     * @param callback  The callback for the result
     */
    suspend fun getEmployeeList(employeeRequest: NSEmployeeListRequest, callback: NSRetrofitCallback<NSEmployeeResponse>) {
        if (isNetwork(callback)) {
            request(authorised3020Client.getListEmployees(employeeRequest), callback)
        }
    }

    /**
     * To call the enable employee service data API
     *
     * @param callback  The callback for the result
     */
    suspend fun enableEmployee(request: NSEmployeeRequest, callback: NSRetrofitCallback<NSEmployeeBlankDataResponse>) {
        if (isNetwork(callback)) {
            request(authorised3020Client.enableEmployee(request), callback)
        }
    }

    /**
     * To call the add employee service data API
     *
     * @param callback  The callback for the result
     */
    suspend fun addEmployee(request: NSAddEmployeeRequest, callback: NSRetrofitCallback<NSEmployeeAddDeleteBlankDataResponse>) {
        if (isNetwork(callback)) {
            request(authorised3020Client.addEmployee(request), callback)
        }
    }

    /**
     * To call the disable employee service data API
     *
     * @param callback  The callback for the result
     */
    suspend fun disableEmployee(request: NSEmployeeRequest, callback: NSRetrofitCallback<NSEmployeeBlankDataResponse>) {
        if (isNetwork(callback)) {
            request(authorised3020Client.disableEmployee(request), callback)
        }
    }

    /**
     * To call the disable employee service data API
     *
     * @param callback  The callback for the result
     */
    suspend fun employeeDelete(request: NSEmployeeRequest, callback: NSRetrofitCallback<NSEmployeeAddDeleteBlankDataResponse>) {
        if (isNetwork(callback)) {
            request(authorised3020Client.employeeDelete(request), callback)
        }
    }

    /**
     * To call the disable employee service data API
     *
     * @param callback  The callback for the result
     */
    suspend fun employeeEdit(request: NSEmployeeEditRequest, callback: NSRetrofitCallback<NSEmployeeAddDeleteBlankDataResponse>) {
        if (isNetwork(callback)) {
            request(authorised3020Client.employeeEdit(request), callback)
        }
    }

    /**
     * To call the disable employee service data API
     *
     * @param callback  The callback for the result
     */
    suspend fun getFleetLocation(fleetId: String, callback: NSRetrofitCallback<FleetLocationResponse>) {
        if (isNetwork(callback)) {
            request(authorisedLocationClient.getFleetLocation(fleetId), callback)
        }
    }

    /**
     * To call the disable Capabilities data API
     *
     * @param callback  The callback for the result
     */
    suspend fun disableCapabilities(request: NSCapabilitiesRequest, callback: NSRetrofitCallback<NSFleetBlankDataResponse>) {
        if (isNetwork(callback)) {
            request(authorisedFleetClient.disableCapabilities(request), callback)
        }
    }

    /**
     * To call the enable Capabilities data API
     *
     * @param callback  The callback for the result
     */
    suspend fun enableCapabilities(request: NSCapabilitiesRequest, callback: NSRetrofitCallback<NSFleetBlankDataResponse>) {
        if (isNetwork(callback)) {
            request(authorisedFleetClient.enableCapabilities(request), callback)
        }
    }

    /**
     * To call the capability delete service data API
     *
     * @param callback  The callback for the result
     */
    suspend fun capabilityDelete(id: String, callback: NSRetrofitCallback<NSCapabilitiesBlankDataResponse>) {
        if (isNetwork(callback)) {
            request(authorisedFleetClient.capabilityDelete(id), callback)
        }
    }

    /**
     * To call the enable service data API
     *
     * @param callback  The callback for the result
     */
    suspend fun createCapability(createBranchRequest: NSCreateCapabilityRequest, callback: NSRetrofitCallback<ResponseBody>) {
        if (isNetwork(callback)) {
            request(authorisedFleetClient.createCapability(createBranchRequest), callback)
        }
    }

    /**
     * To call the enable service data API
     *
     * @param callback  The callback for the result
     */
    suspend fun updateCapability(id: String, createBranchRequest: NSCreateCapabilityRequest, callback: NSRetrofitCallback<ResponseBody>) {
        if (isNetwork(callback)) {
            request(authorisedFleetClient.updateCapability(id, createBranchRequest), callback)
        }
    }

    /**
     * To call the enable service data API
     *
     * @param callback  The callback for the result
     */
    suspend fun updateServiceCapability(request: NSServiceCapabilitiesRequest, callback: NSRetrofitCallback<NSCapabilitiesBlankDataResponse>) {
        if (isNetwork(callback)) {
            request(authorisedFleetClient.updateServiceCapability(request), callback)
        }
    }

    /**
     * To call the update service fleets data API
     *
     * @param callback  The callback for the result
     */
    suspend fun updateServiceFleets(request: NSServiceFleetUpdateRequest, callback: NSRetrofitCallback<NSFleetBlankDataResponse>) {
        if (isNetwork(callback)) {
            request(authorisedFleetClient.updateServiceFleets(request), callback)
        }
    }

    /**
     * To call the enable service data API
     *
     * @param callback  The callback for the result
     */
    suspend fun getServiceCapability(id: String, callback: NSRetrofitCallback<NSServiceCapabilityResponse>) {
        if (isNetwork(callback)) {
            request(authorisedFleetClient.getServiceCapability(id), callback)
        }
    }

    /**
     * To call the service list data API
     *
     * @param callback  The callback for the result
     */
    suspend fun getServiceList(callback: NSRetrofitCallback<NSGetServiceListResponse>) {
        if (isNetwork(callback)) {
            request(authorised3020Client.getServiceList(), callback)
        }
    }

    /**
     * To call the enable service data API
     *
     * @param callback  The callback for the result
     */
    suspend fun createVehicle(request: NSVehicleRequest, callback: NSRetrofitCallback<ResponseBody>) {
        if (isNetwork(callback)) {
            request(authorisedFleetClient.createVehicle(request), callback)
        }
    }


    /**
     * To call the enable service data API
     *
     * @param callback  The callback for the result
     */
    suspend fun vehicleList(refId: String, callback: NSRetrofitCallback<NSVehicleResponse>) {
        if (isNetwork(callback)) {
            request(authorisedFleetClient.vehicleList(refId), callback)
        }
    }

    /**
     * To call the enable vehicle data API
     *
     * @param callback  The callback for the result
     */
    suspend fun enableVehicle(vendorRequest: NSVehicleEnableDisableRequest, callback: NSRetrofitCallback<NSVehicleBlankDataResponse>) {
        if (isNetwork(callback)) {
            request(authorisedFleetClient.enableVehicle(vendorRequest), callback)
        }
    }

    /**
     * To call the enable vehicle data API
     *
     * @param callback  The callback for the result
     */
    suspend fun disableVehicle(vendorRequest: NSVehicleEnableDisableRequest, callback: NSRetrofitCallback<NSVehicleBlankDataResponse>) {
        if (isNetwork(callback)) {
            request(authorisedFleetClient.disableVehicle(vendorRequest), callback)
        }
    }

    /**
     * To call the update vehicle image data API
     *
     * @param callback  The callback for the result
     */
    suspend fun updateVehicleImage(vendorRequest: NSVehicleUpdateImageRequest, callback: NSRetrofitCallback<NSVehicleBlankDataResponse>) {
        if (isNetwork(callback)) {
            request(authorisedFleetClient.vehicleUpdateImage(vendorRequest), callback)
        }
    }

    /**
     * To call the update vehicle notes data API
     *
     * @param callback  The callback for the result
     */
    suspend fun updateVehicleNotes(vendorRequest: NSVehicleNotesRequest, callback: NSRetrofitCallback<NSVehicleBlankDataResponse>) {
        if (isNetwork(callback)) {
            request(authorisedFleetClient.updateVehicleNotes(vendorRequest), callback)
        }
    }

    /**
     * To call the update vehicle capability data API
     *
     * @param callback  The callback for the result
     */
    suspend fun updateVehicleCapability(vendorRequest: NSUpdateCapabilitiesRequest, callback: NSRetrofitCallback<NSVehicleBlankDataResponse>) {
        if (isNetwork(callback)) {
            request(authorisedFleetClient.updateVehicleCapability(vendorRequest), callback)
        }
    }

    /**
     * To call the update vehicle detail data API
     *
     * @param callback  The callback for the result
     */
    suspend fun getVehicleDetail(id: String, callback: NSRetrofitCallback<NSVehicleDetailResponse>) {
        if (isNetwork(callback)) {
            request(authorisedFleetClient.getVehicleDetail(id), callback)
        }
    }

    /**
     * To call the assign vehicle detail data API
     *
     * @param callback  The callback for the result
     */
    suspend fun assignVehicle(request: NSAssignVehicleRequest, callback: NSRetrofitCallback<NSVehicleBlankDataResponse>) {
        if (isNetwork(callback)) {
            request(authorisedFleetClient.assignVehicle(request), callback)
        }
    }
}

/**
 * The interface defining the API endpoints
 */
interface RTApiInterface {

    @GET("user/me")
    suspend fun userDetail(): retrofit2.Response<NSUserDetailResponse>

    @POST("bmu/get-strings")
    suspend fun getLanguageString(@Body languageStringRequest: NSLanguageStringRequest): retrofit2.Response<NSLanguageStringResponse>

    @POST("login")
    suspend fun loginWithEmailPassword(@Body nsLoginRequest: NSLoginRequest): retrofit2.Response<NSUserResponse>

    @POST("logout")
    suspend fun logout(): retrofit2.Response<NSLogoutResponse>

    @POST("refresh_token")
    suspend fun refreshToken(@Body tokenRequest: NSRefreshTokenRequest): retrofit2.Response<NSUserResponse>

    /*BMU*/
    @POST("bmu/getapptheme")
    suspend fun getAppTheme(@Body nsAppThemeRequest: NSAppThemeRequest): retrofit2.Response<NSGetThemeModel>

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

    @GET("api/location")
    suspend fun getFleetLocation(@Query("fleet_id") fieldId: String): retrofit2.Response<FleetLocationResponse>

    @GET("/capability/list")
    suspend fun getCapabilities(): retrofit2.Response<NSCapabilitiesResponse>

    @PATCH("capability/admin/disable")
    suspend fun disableCapabilities(@Body request: NSCapabilitiesRequest): retrofit2.Response<NSFleetBlankDataResponse>

    @PATCH("capability/admin/enable")
    suspend fun enableCapabilities(@Body request: NSCapabilitiesRequest): retrofit2.Response<NSFleetBlankDataResponse>

    @DELETE("admin/capability/{capability_id}")
    suspend fun capabilityDelete(@Path("capability_id") id: String): retrofit2.Response<NSCapabilitiesBlankDataResponse>

    @POST("capability/admin")
    suspend fun createCapability(@Body request: NSCreateCapabilityRequest): retrofit2.Response<ResponseBody>

    @PUT("capability/{capability_id}/admin")
    suspend fun updateCapability(@Path("capability_id") id: String, @Body request: NSCreateCapabilityRequest): retrofit2.Response<ResponseBody>

    @GET("service/management/{service_id}")
    suspend fun getServiceCapability(@Path("service_id") id: String): retrofit2.Response<NSServiceCapabilityResponse>

    @PATCH("service/management/capability")
    suspend fun updateServiceCapability(@Body request: NSServiceCapabilitiesRequest): retrofit2.Response<NSCapabilitiesBlankDataResponse>

    @PATCH("service/management/fleets")
    suspend fun updateServiceFleets(@Body request: NSServiceFleetUpdateRequest): retrofit2.Response<NSFleetBlankDataResponse>

    @POST("/vehicle")
    suspend fun createVehicle(@Body request: NSVehicleRequest): retrofit2.Response<ResponseBody>

    @GET("vehicle/list/admin")
    suspend fun vehicleList(@Query("ref_id") id: String): retrofit2.Response<NSVehicleResponse>

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

    @POST("driver/vehicle")
    suspend fun assignVehicle(@Body request: NSAssignVehicleRequest): retrofit2.Response<NSVehicleBlankDataResponse>

}
