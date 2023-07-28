package com.nyotek.dot.admin.repository.network.responses

import com.google.gson.annotations.SerializedName

data class StringResourceResponse(

	@field:SerializedName("apps")
	val apps: String = "",

	@field:SerializedName("type_your_email_phone")
	val typeYourEmailPhone: String = "",

	@field:SerializedName("email_phone_number")
	val emailPhoneNumber: String = "",

	@field:SerializedName("type_your_password")
	val typeYourPassword: String = "",

	@field:SerializedName("must_be_8_char")
	val mustBe8Char: String = "",

	@field:SerializedName("password_reset")
	val passwordReset: String = "",

	@field:SerializedName("enter_your_email_we_send_reset")
	val enterYourEmailWeSendReset: String = "",

	@field:SerializedName("send_me_the_link")
	val sendMeTheLink: String = "",

	@field:SerializedName("welcome_back")
	val welcomeBack: String = "",

	@field:SerializedName("lets_build_something")
	val letsBuildSomething: String = "",

	@field:SerializedName("cancel")
	val cancel: String = "",

	@field:SerializedName("employees")
	val employees : String = "",

	@field:SerializedName("vehicle")
	val vehicle : String = "",

	@field:SerializedName("vehicle_details")
	val vehicleDetails : String = "",

	@field:SerializedName("created_date")
	val createdDate: String = "",

	@field:SerializedName("hubz")
	val hubz: String = "",

	@field:SerializedName("yamin")
	val yamin: String = "",

	@field:SerializedName("branch_list")
	val branchList: String = "",

	@field:SerializedName("create_branch")
	val createBranch: String = "",

	@field:SerializedName("create_vehicle")
	val createVehicle: String = "",

	@field:SerializedName("update_vehicle")
	val updateVehicle: String = "",

	@field:SerializedName("branches")
	val branches: String = "",

	@field:SerializedName("edit_branch")
	val editBranch: String = "",

	@field:SerializedName("copy")
	val copy: String = "",

	@field:SerializedName("german")
	val german: String = "",

	@field:SerializedName("create")
	val create: String = "",

	@field:SerializedName("update")
	val update: String = "",

	@field:SerializedName("update_driver")
	val updateDriver: String = "",

	@field:SerializedName("send_invite")
	val sendInvite: String = "",

	@field:SerializedName("something_went_wrong")
	val somethingWentWrong: String = "",

	@field:SerializedName("no_file_selected")
	val noFileSelected: String = "",

	@field:SerializedName("setting")
	val setting: String = "",

	@field:SerializedName("in_active")
	val inActive: String = "",

	@field:SerializedName("all")
	val all: String = "",

	@field:SerializedName("password")
	val password: String = "",

	@field:SerializedName("edit")
	val edit: String = "",

	@field:SerializedName("sign_up")
	val signUp: String = "",

	@field:SerializedName("profile")
	val profile: String = "",

	@field:SerializedName("active")
	val active: String = "",

	@field:SerializedName("fill")
	val fill: String = "",

	@field:SerializedName("fit")
	val fit: String = "",

	@field:SerializedName("enable")
	val enable: String = "",

	@field:SerializedName("disable")
	val disable: String = "",

	@field:SerializedName("hide_title")
	val hideTitle: String = "",

	@field:SerializedName("json")
	val json: String = "",

	@field:SerializedName("copy_success")
	val copySuccess: String = "",

	@field:SerializedName("order_id")
	val orderId: String = "",

	@field:SerializedName("logout_message")
	val logoutMessage: String = "",

	@field:SerializedName("driver_management")
	val driverManagement: String = "",

	@field:SerializedName("dashboard")
	val dashboard: String = "",

	@field:SerializedName("search")
	val search: String = "",

	@field:SerializedName("vendor")
	val vendor: String = "",

	@field:SerializedName("no_network_available")
	val noNetworkAvailable: String = "",

	@field:SerializedName("close")
	val close: String = "",

	@field:SerializedName("email")
	val email: String = "",

	@field:SerializedName("address")
	val address: String = "",

	@field:SerializedName("select_language")
	val selectLanguage: String = "",

	@field:SerializedName("add_address")
	val addAddress: String = "",

	@field:SerializedName("edit_driver")
	val editDriver: String = "",

	@field:SerializedName("arabic_l")
	val arabicL: String = "",

	@field:SerializedName("call")
	val call: String = "",

	@field:SerializedName("notification_setting_title")
	val notificationSettingTitle: String = "",

	@field:SerializedName("email_address")
	val emailAddress: String = "",

	@field:SerializedName("add_deduct")
	val addDeduct: String = "",

	@field:SerializedName("new_")
	val new: String = "",

	@field:SerializedName("edit_vendor")
	val editVendor: String = "",

	@field:SerializedName("networkUnreachable_title")
	val networkUnreachableTitle: String = "",

	@field:SerializedName("view_more")
	val viewMore: String = "",

	@field:SerializedName("view")
	val view: String = "",

	@field:SerializedName("log_in")
	val logIn: String = "",

	@field:SerializedName("add")
	val add: String = "",

	@field:SerializedName("deduct")
	val deduct: String = "",

	@field:SerializedName("month")
	val month: String = "",

	@field:SerializedName("success")
	val success: String = "",

	@field:SerializedName("name")
	val name: String = "",

	@field:SerializedName("vehicle_registration_no")
	val vehicleRegistrationNo: String = "",

	@field:SerializedName("enter_your_vehicle_registration_no")
	val enterYourVehicleRegistrationNo: String = "",

	@field:SerializedName("model_can_not_empty")
	val modelCanNotEmpty: String = "",

	@field:SerializedName("manufacturer_can_not_empty")
	val manufacturerCanNotEmpty: String = "",

	@field:SerializedName("registration_number_can_not_empty")
	val registrationNumberCanNotEmpty: String = "",

	@field:SerializedName("manufacturer_year_can_not_empty")
	val manufacturerYearCanNotEmpty: String = "",

	@field:SerializedName("load_capacity_can_not_empty")
	val loadCapacityCanNotEmpty: String = "",

	@field:SerializedName("logo_can_not_empty")
	val logoCanNotEmpty: String = "",

	@field:SerializedName("please_select_capability")
	val pleaseSelectCapability: String = "",

	@field:SerializedName("select_manufacturer_year")
	val selectManufactureYear: String = "",

	@field:SerializedName("select_model")
	val selectModel: String = "",

	@field:SerializedName("select_fleet")
	val selectFleet: String = "",

	@field:SerializedName("select_manufacturer")
	val selectManufacturer: String = "",

	@field:SerializedName("select_load_capacity")
	val selectLoadCapacity: String = "",

	@field:SerializedName("select_capability")
	val selectCapability: String = "",

	@field:SerializedName("select_driver")
	val selectDriver: String = "",

	@field:SerializedName("model")
	val model: String = "",

	@field:SerializedName("select_all")
	val selectAll: String = "",

	@field:SerializedName("load_capacity")
	val loadCapacity: String = "",

	@field:SerializedName("additional_note")
	val additionalNote: String = "",

	@field:SerializedName("manufacturer")
	val manufacturer: String = "",

	@field:SerializedName("manufacturer_year")
	val manufacturerYear: String = "",

	@field:SerializedName("background_images")
	val backgroundImages: String = "",

	@field:SerializedName("brand_logo")
	val brandLogo: String = "",

	@field:SerializedName("icon_size_title")
	val iconSizeTitle: String = "",

	@field:SerializedName("select_theme")
	val selectTheme: String = "",

	@field:SerializedName("select_platform")
	val selectPlatForm: String = "",

	@field:SerializedName("description")
	val description: String = "",

	@field:SerializedName("invalid_password_title")
	val invalidPasswordTitle: String = "",

	@field:SerializedName("phone_number")
	val phoneNumber: String = "",

	@field:SerializedName("next")
	val next: String = "",

	@field:SerializedName("back")
	val back: String = "",

	@field:SerializedName("industry_type")
	val industryType: String = "",

	@field:SerializedName("invalid_email_title")
	val invalidEmailTitle: String = "",

	@field:SerializedName("create_fleet")
	val createFleet: String = "",

	@field:SerializedName("fleets")
	val fleets: String = "",

	@field:SerializedName("fleet")
	val fleet: String = "",

	@field:SerializedName("fleet_management")
	val fleetManagement: String = "",

	@field:SerializedName("total_price")
	val totalPrice: String = "",

	@field:SerializedName("primary_color")
	val primaryColor: String = "",

	@field:SerializedName("primary_light_color")
	val primaryLightColor: String = "",

	@field:SerializedName("secondary_color")
	val secondaryColor: String = "",

	@field:SerializedName("secondary_dark_color")
	val secondaryDarkColor: String = "",

	@field:SerializedName("background_color")
	val backgroundColor: String = "",

	@field:SerializedName("tab_secondary_color")
	val tabSecondaryColor: String = "",

	@field:SerializedName("category_segment_color")
	val categorySegmentColor: String = "",

	@field:SerializedName("success_color")
	val successColor: String = "",

	@field:SerializedName("error_color")
	val errorColor: String = "",

	@field:SerializedName("user_details")
	val userDetails: String = "",

	@field:SerializedName("select_app")
	val selectApp: String = "",

	@field:SerializedName("theme_not_empty")
	val themeCanNotBeEmpty: String = "",

	@field:SerializedName("yes")
	val yes: String = "",

	@field:SerializedName("mobile")
	val mobile: String = "",

	@field:SerializedName("english_l")
	val englishL: String = "",

	@field:SerializedName("selected_item")
	val selectedItem: String = "",

	@field:SerializedName("username")
	val username: String = "",

	@field:SerializedName("select")
	val select: String = "",

	@field:SerializedName("logout")
	val logout: String = "",

	@field:SerializedName("no_item")
	val noItem: String = "",

	@field:SerializedName("fleet_detail")
	val fleetDetail: String = "",

	@field:SerializedName("block")
	val block: String = "",

	@field:SerializedName("field_cannot_be_empty")
	val fieldCannotBeEmpty: String = "",

	@field:SerializedName("driver")
	val driver: String = "",

	@field:SerializedName("services")
	val services: String = "",

	@field:SerializedName("service_management")
	val serviceManagement: String = "",

	@field:SerializedName("submit")
	val submit: String = "",

	@field:SerializedName("theme_created_successfully")
	val themeCreatedSuccessfully: String = "",

	@field:SerializedName("theme_edited_successfully")
	val themeEditedSuccessfully: String = "",

	@field:SerializedName("apply")
	val apply: String = "",

	@field:SerializedName("employee_role")
	val employeeRole: String = "",

	@field:SerializedName("edit_employee")
	val editEmployee: String = "",

	@field:SerializedName("enter_tag")
	val enterTag: String = "",

	@field:SerializedName("please_enter_name")
	val pleaseEnterName: String = "",

	@field:SerializedName("logo_can_not_be_empty")
	val logoCanNotBeEmpty: String = "",

	@field:SerializedName("please_enter_slogan")
	val pleaseEnterSlogan: String = "",

	@field:SerializedName("please_enter_description")
	val pleaseEnterDescription: String = "",

	@field:SerializedName("create_service")
	val createService: String = "",

	@field:SerializedName("create_theme")
	val createTheme: String = "",

	@field:SerializedName("apply_theme")
	val applyTheme: String = "",

	@field:SerializedName("edit_theme")
	val editTheme: String = "",

	@field:SerializedName("create_app")
	val createApp: String = "",

	@field:SerializedName("customer")
	val customer: String = "",

	@field:SerializedName("orders")
	val orders: String = "",

	@field:SerializedName("themes")
	val themes: String = "",

	@field:SerializedName("branch")
	val branch: String = "",

	@field:SerializedName("employee")
	val employee: String = "",

	@field:SerializedName("employee_list")
	val employeeList: String = "",

	@field:SerializedName("invite_employee")
	val inviteEmployee: String = "",

	@field:SerializedName("add_employee")
	val addEmployee : String = "",

	@field:SerializedName("please_select_employee_role")
	val pleaseSelectEmployeeRole  : String = "",

	@field:SerializedName("please_select_user")
	val pleaseSelectUser  : String = "",

	@field:SerializedName("select_employee_role")
	val selectEmployeeRole  : String = "",

	@field:SerializedName("select_vehicle")
	val selectVehicle  : String = "",

	@field:SerializedName("theme")
	val theme: String = "",

	@field:SerializedName("color_picker")
	val colorPicker: String = "",

	@field:SerializedName("updated_successfully")
	val updatedSuccessfully: String = "",

	@field:SerializedName("app_created_successfully")
	val appCreatedSuccessfully: String = "",

	@field:SerializedName("imported_successfully")
	val importedSuccessfully: String = "",

	@field:SerializedName("theme_applied_successfully")
	val themeAppliedSuccessfully: String = "",

	@field:SerializedName("preview")
	val preview: String = "",

	@field:SerializedName("update_theme")
	val updateTheme: String = "",

	@field:SerializedName("modify")
	val modify: String = "",

	@field:SerializedName("language")
	val language: String = "",

	@field:SerializedName("lat_short")
	val latShort: String = "",

	@field:SerializedName("long_short")
	val longShort: String = "",

	@field:SerializedName("browse")
	val browse: String = "",

	@field:SerializedName("status")
	val status: String = "",

	@field:SerializedName("save")
	val save: String = "",

	@field:SerializedName("import")
	val import: String = "",

	@field:SerializedName("contact_us")
	val contactUs: String = "",

	@field:SerializedName("you_have_account")
	val youHaveAccount: String = "",

	@field:SerializedName("driver_detail")
	val driverDetail: String = "",

	@field:SerializedName("order_delivered_details")
	val orderDeliveredDetails: String = "",

	@field:SerializedName("wallet_id_not_found")
	val walletIdNotFound: String = "",

	@field:SerializedName("last_name")
	val lastName: String = "",

	@field:SerializedName("not_yet_registered")
	val notYetRegistered: String = "",

	@field:SerializedName("url")
	val url: String = "",

	@field:SerializedName("notification_title")
	val notificationTitle: String = "",

	@field:SerializedName("user_management")
	val userManagement: String = "",

	@field:SerializedName("enter_your_credentials")
	val enterYourCredentials: String = "",

	@field:SerializedName("logo")
	val logo: String = "",

	@field:SerializedName("day")
	val day: String = "",

	@field:SerializedName("accepted")
	val accepted: String = "",

	@field:SerializedName("done")
	val done: String = "",

	@field:SerializedName("please_select_address")
	val pleaseSelectAddress: String = "",

	@field:SerializedName("app_can_not_empty")
	val appCanNotEmpty: String = "",

	@field:SerializedName("not_refresh_token")
	val notRefreshToken: String = "",

	@field:SerializedName("slogan")
	val slogan: String = "",

	@field:SerializedName("tags")
	val tags: String = "",

	@field:SerializedName("no")
	val no: String = "",

	@field:SerializedName("week")
	val week: String = "",

	@field:SerializedName("delete")
	val delete: String = "",

	@field:SerializedName("total")
	val total: String = "",

	@field:SerializedName("national_id_number")
	val nationalIdNumber: String = "",

	@field:SerializedName("ok")
	val ok: String = "",

	@field:SerializedName("do_you_want_to_delete")
	val doYouWantToDelete: String = "",

	@field:SerializedName("value")
	val value: String = "",

	@field:SerializedName("select_service")
	val selectService: String = "",

	@field:SerializedName("service_cannot_be_empty")
	val serviceCannotBeEmpty: String = "",

	@field:SerializedName("amount")
	val amount: String = "",

	@field:SerializedName("register_driver")
	val registerDriver: String = "",

	@field:SerializedName("sar")
	val sar: String = "",

	@field:SerializedName("clear")
	val clear: String = "",

	@field:SerializedName("search_here")
	val searchHere: String = "",

	@field:SerializedName("search_user")
	val searchUser: String = "",

	@field:SerializedName("data_failed")
	val dataFailed: String = "",

	@field:SerializedName("user")
	val user: String = "",

	@field:SerializedName("capabilities")
	val capabilities: String = "",

	@field:SerializedName("capability")
	val capability: String = "",

	@field:SerializedName("create_capabilities")
	val createCapabilities: String = "",

	@field:SerializedName("update_capability")
	val updateCapabilities: String = "",

	@field:SerializedName("forgot_password")
	val forgotPassword: String = "",

	@field:SerializedName("select_address")
	val selectAddress: String = "",

	@field:SerializedName("standard_title")
	val standardTitle: String = "",

	@field:SerializedName("satellite_title")
	val satelliteTitle: String = "",

	@field:SerializedName("hybrid_title")
	val hybridTitle: String = "",

	@field:SerializedName("nick_name")
	val nickName: String = "",

	@field:SerializedName("city_title")
	val cityTitle: String = "",

	@field:SerializedName("postal_code")
	val postalCode: String = "",

	@field:SerializedName("state")
	val state: String = "",

	@field:SerializedName("country_title")
	val countryTitle: String = "",

	@field:SerializedName("add_new_key")
	val addnewkey: String = "",

	@field:SerializedName("key")
	val key: String = "",

	@field:SerializedName("create_local")
	val createLocal: String = "",

	@field:SerializedName("local")
	val local: String = "",

	@field:SerializedName("from_checkout")
	val fromCheckout: String = ""

)
