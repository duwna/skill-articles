package ru.skillbranch.skillarticles

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.remote.NetworkMonitor
import ru.skillbranch.skillarticles.di.modules.NetworkUtilsModule
import ru.skillbranch.skillarticles.di.modules.PreferencesModule
import ru.skillbranch.skillarticles.di.modules.components.AppComponent
import ru.skillbranch.skillarticles.di.modules.components.DaggerAppComponent
import ru.skillbranch.skillarticles.example.ActivityComponent
import ru.skillbranch.skillarticles.example.ActivityModule
import ru.skillbranch.skillarticles.example.DaggerActivityComponent
import javax.inject.Inject


class App : Application() {

    @Inject
    lateinit var monitor: NetworkMonitor
    @Inject
    lateinit var prefs: PrefManager

    init {
        instance = this
    }

    companion object {
        lateinit var appComponent: AppComponent
        lateinit var activityComponent: ActivityComponent

        lateinit var tempMonitor: NetworkMonitor
        lateinit var tempPrefManager: PrefManager

        private var instance: App? = null
        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()

        appComponent = AppComponent.Factory.create(applicationContext)

        activityComponent = DaggerActivityComponent.builder()
            .appComponent(appComponent)
            .build()

        appComponent.inject(this)

        tempMonitor = monitor
        tempPrefManager = prefs

        monitor.registerNetworkMonitor()

        val mode = if (prefs.isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
        else AppCompatDelegate.MODE_NIGHT_NO

        AppCompatDelegate.setDefaultNightMode(mode)
    }
}


fun Any.log(vararg msg: Any?, tag: String = this::class.java.simpleName) {
    Log.e(tag, msg.toList().toString())
}