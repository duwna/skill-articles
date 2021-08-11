package ru.skillbranch.skillarticles.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.skillbranch.skillarticles.data.local.PrefManager
import javax.inject.Singleton

@Module
object PreferencesModule {
    @Provides
    @Singleton
    fun providePrefManager(context: Context): PrefManager = PrefManager(context)
}