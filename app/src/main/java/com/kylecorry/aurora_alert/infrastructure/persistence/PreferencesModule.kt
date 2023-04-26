package com.kylecorry.aurora_alert.infrastructure.persistence

import android.content.Context
import com.kylecorry.andromeda.preferences.CachedPreferences
import com.kylecorry.andromeda.preferences.IPreferences
import com.kylecorry.andromeda.preferences.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object PreferencesModule {
    @Provides
    @Singleton
    fun providePreferences(@ApplicationContext appContext: Context): IPreferences {
        return CachedPreferences(Preferences(appContext))
    }
}