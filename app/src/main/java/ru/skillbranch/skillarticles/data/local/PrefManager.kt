package ru.skillbranch.skillarticles.data.local

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import ru.skillbranch.skillarticles.App
import ru.skillbranch.skillarticles.data.delegates.PrefDelegate
import ru.skillbranch.skillarticles.data.models.AppSettings

object PrefManager {
    internal val preferences : SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext())
    }

    var storedBoolean by PrefDelegate(false)
    var storedString by PrefDelegate("test")
    var storedInt by PrefDelegate(Int.MAX_VALUE)
    var storedLong by PrefDelegate(Long.MAX_VALUE)
    var storedFloat by PrefDelegate(100f)

    fun clearAll(){
        preferences.edit().clear().apply()
    }

    fun getAppSettings(): LiveData<AppSettings> {
        return MutableLiveData(AppSettings())
    }

    fun isAuth(): MutableLiveData<Boolean> {
        return MutableLiveData(false)
    }

    fun setAuth(auth: Boolean) {

    }
}