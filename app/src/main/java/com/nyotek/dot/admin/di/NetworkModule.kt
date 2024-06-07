package com.nyotek.dot.admin.di

import com.nyotek.dot.admin.BuildConfig
import com.nyotek.dot.admin.common.DeviceDetailUtils
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSDataStorePreferences
import com.nyotek.dot.admin.common.NSThemeHelper
import com.nyotek.dot.admin.common.keys.HeaderKey
import com.nyotek.dot.admin.data.NSApiInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    @Named("base_url_main")
    fun provideBaseUrl(): String = if (BuildConfig.IS_BASE_URL_DEBUG) BuildConfig.BASE_URL_DEBUG else BuildConfig.BASE_URL_MAIN

    @Singleton
    @Provides
    @Named("base_url_location")
    fun provideLocationUrl(): String = BuildConfig.BASE_URL_LOCATION

    @Singleton
    @Provides
    @Named("base_url_fleet")
    fun provideFleetUrl(): String = BuildConfig.BASE_URL_FLEET

    @Singleton
    @Provides
    fun provideHttpClient(dataStoreRepository: NSDataStorePreferences): OkHttpClient {
        val trustAllCerts = arrayOf<TrustManager>(
            object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                }

                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                    return arrayOf()
                }
            }
        )

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())
        val sslSocketFactory = sslContext.socketFactory

        val httpClient =  OkHttpClient.Builder().apply {
            sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            hostnameVerifier { hostname, session -> true }
        }.readTimeout(HeaderKey.TIMEOUT, TimeUnit.SECONDS).connectTimeout(HeaderKey.TIMEOUT, TimeUnit.SECONDS)

        httpClient.addInterceptor { chain ->
            val request = chain.request().newBuilder().apply {
                header(HeaderKey.KEY_ACCEPT, HeaderKey.ACCEPT_VALUE)
                header(HeaderKey.KEY_BuildVersion, BuildConfig.VERSION_CODE.toString())
                header(HeaderKey.KEY_APP_ID, BuildConfig.THEME_APP_ID)
                header(HeaderKey.KEY_LOCALE, Locale.getDefault().language)
                header(HeaderKey.KEY_SELECTED_LANGUAGE, (dataStoreRepository.languageData?:"").lowercase())
                header(HeaderKey.KEY_TIME_ZONE, TimeZone.getDefault().id)
                header(HeaderKey.KEY_DEVICE_ID, DeviceDetailUtils.getDeviceId())
                val serviceIdForUrl = chain.request().url.toUrl().path

                if (!serviceIdForUrl.contains("employees/list_job_titles")) {
                    if (serviceIdForUrl.contains("wallets/admin/getWalletByUser")) {
                        header(HeaderKey.KEY_SERVICE_ID, NSThemeHelper.USER_DETAIL_SERVICE_ID)
                    } else {
                        header(HeaderKey.KEY_SERVICE_ID, NSThemeHelper.SERVICE_ID)
                    }
                }

                val authToken = dataStoreRepository.authToken

                if (authToken?.isNotEmpty() == true) {
                    header(HeaderKey.AUTHORISATION_KEY, authToken)
                }
            }.build()
            chain.proceed(request)
        }

        return httpClient.build()
    }

    @Singleton
    @Provides
    fun provideConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Singleton
    @Provides
    fun provideRetrofitBuilder(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit.Builder {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
    }

    @Singleton
    @Provides
    @Named("base_url_main")
    fun provideApiService(retrofitBuilder: Retrofit.Builder, @Named("base_url_main") baseUrl: String): NSApiInterface {
        return retrofitBuilder.baseUrl(baseUrl).build().create(NSApiInterface::class.java)
    }

    @Provides
    @Named("base_url_location")
    fun provideLocationService(retrofitBuilder: Retrofit.Builder, @Named("base_url_location") baseUrl: String): NSApiInterface {
        return retrofitBuilder.baseUrl(baseUrl).build().create(NSApiInterface::class.java)
    }

    @Provides
    @Named("base_url_fleet")
    fun provideFleetService(retrofitBuilder: Retrofit.Builder, @Named("base_url_fleet") baseUrl: String): NSApiInterface {
        return retrofitBuilder.baseUrl(baseUrl).build().create(NSApiInterface::class.java)
    }
}
