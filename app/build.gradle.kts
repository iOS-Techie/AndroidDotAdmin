plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.nyotek.dot.admin"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.nyotek.dot.admin"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    kapt {
        correctErrorTypes = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    flavorDimensions += "dotadmin"
    productFlavors {
        create("development") {
            dimension = "dotadmin"
            versionCode = 7
            versionName = "1.0"
            applicationId = "com.nyotek.dot.dev.admin"
            resValue("string", "app_admin_name", "DoT Admin")
            resValue("string", "app_small_name", "DoT")
            buildConfigField("String", "THEME_APP_ID", "\"01H4JPH7T5C3X2SSK6GMBA64TA\"")
            resValue("string", "google_maps_key", "AIzaSyBymt1KFpCEHBcIKpnNTn-RTQNn5w3L8gs")
            resValue("string", "google_address_place_key", "AIzaSyA0r_pAMfx2lue7adrySY73VCoI2raIDqs")
            buildConfigField("String", "BASE_URL_MAIN", "\"https://prod.hubz.nyotek.com/\"")
            buildConfigField("String", "BASE_URL_DEBUG", "\"https://dev.hubz.nyotek.com/\"")
            buildConfigField("String", "BASE_URL_THEME", "\"http://34.91.213.39:6080/\"")
            buildConfigField("String", "BASE_URL_LOCATION", "\"https://locations-dev.dot.nyotek.com\"")
            buildConfigField("String", "BASE_URL_FLEET", "\"https://fleets-dev.dot.nyotek.com\"")
            buildConfigField("Boolean", "IS_BASE_URL_DEBUG", "true")
            buildConfigField("String", "PREFERENCE", "\"DoTAdmin\"")
            buildConfigField("String", "MAIL_ID", "\"test@gmail.com\"")
            buildConfigField("String", "PHONE_NUMBER", "\"+12373629297\"")
            buildConfigField("String", "MAPBOX_ACCESS_TOKEN", "\"pk.eyJ1Ijoic2FtaXJtYWthZGlhIiwiYSI6ImNsamR1bXI5aDAxeDUzaHM0OXRsdTd0eGcifQ.GNB16ObUGEn47z78QL1FQg\"")
        }
        create("production") {
            dimension = "dotadmin"
            versionCode = 1
            versionName = "1.0"
            applicationId = "com.nyotek.dot.prod.admin"
            resValue("string", "app_admin_name", "DoT Admin")
            resValue("string", "app_small_name", "DoT")
            buildConfigField("String", "THEME_APP_ID", "\"01GEFEB5FQYJNDFVQJ56182R9S\"")
            resValue("string", "google_maps_key", "AIzaSyBymt1KFpCEHBcIKpnNTn-RTQNn5w3L8gs")
            resValue("string", "google_address_place_key", "AIzaSyA0r_pAMfx2lue7adrySY73VCoI2raIDqs")
            buildConfigField("String", "BASE_URL_MAIN", "\"https://prod.hubz.nyotek.com/\"")
            buildConfigField("String", "BASE_URL_DEBUG", "\"https://dev.hubz.nyotek.com/\"")
            buildConfigField("String", "BASE_URL_THEME", "\"http://34.91.213.39:6080/\"")
            buildConfigField("String", "BASE_URL_LOCATION", "\"http://34.141.193.1:8082/\"")
            buildConfigField("String", "BASE_URL_FLEET", "\"http://34.141.193.1:8091/\"")
            buildConfigField("Boolean", "IS_BASE_URL_DEBUG", "false")
            buildConfigField("String", "PREFERENCE", "\"DoTAdmin\"")
            buildConfigField("String", "MAIL_ID", "\"test@gmail.com\"")
            buildConfigField("String", "PHONE_NUMBER", "\"+12373629297\"")
            buildConfigField("String", "MAPBOX_ACCESS_TOKEN", "\"pk.eyJ1Ijoic2FtaXJtYWthZGlhIiwiYSI6ImNsamR1bXI5aDAxeDUzaHM0OXRsdTd0eGcifQ.GNB16ObUGEn47z78QL1FQg\"")
        }
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    implementation("com.intuit.sdp:sdp-android:1.1.0")
    implementation("io.coil-kt:coil:2.4.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Dagger - Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("com.github.franmontiel:LocaleChanger:1.1")


    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    implementation(platform("com.google.firebase:firebase-bom:31.2.3"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-config")

    implementation("com.google.android.libraries.places:places:3.4.0")
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("com.mapbox.maps:android:10.16.0")
    implementation("org.greenrobot:eventbus:3.3.1")
    implementation(project(":imagepicker"))
}