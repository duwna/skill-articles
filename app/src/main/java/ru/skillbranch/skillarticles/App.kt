package ru.skillbranch.skillarticles

import android.app.Application
import android.content.Context
import android.util.Log
import ru.skillbranch.skillarticles.data.remote.NetworkMonitor


class App : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: App? = null
        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        NetworkMonitor.registerNetworkMonitor(applicationContext)

    }
}


fun Any.log(msg: Any?, tag: String = this::class.java.simpleName) {
    Log.e(tag, msg.toString())
}