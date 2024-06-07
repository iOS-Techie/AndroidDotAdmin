package com.nyotek.dot.admin.common.component

import android.content.Context
import com.nyotek.dot.admin.common.NSDataStorePreferences
import com.nyotek.dot.admin.common.NSThemeHelper
import com.nyotek.dot.admin.common.utils.ColorResources
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataStoreRepository(@ApplicationContext context: Context): NSDataStorePreferences {
        return NSDataStorePreferences(context)
    }

    @Provides
    @Singleton
    fun provideNSThemeHelper(): NSThemeHelper {
        return NSThemeHelper
    }

    @Provides
    @Singleton
    fun provideColorResources(): ColorResources {
        return ColorResources(provideNSThemeHelper())
    }
}