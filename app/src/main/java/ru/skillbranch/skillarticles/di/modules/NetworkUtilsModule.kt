package ru.skillbranch.skillarticles.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.skillbranch.skillarticles.data.remote.NetworkMonitor

@Module
class NetworkUtilsModule(val context: Context) {
    @Provides
    fun provideNetworkMonitor(): NetworkMonitor = NetworkMonitor(context)
}