package ru.skillbranch.skillarticles.data.local

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import ru.skillbranch.skillarticles.App
import ru.skillbranch.skillarticles.data.delegates.PrefDelegate
import ru.skillbranch.skillarticles.data.models.AppSettings

object PrefManager {

    internal val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext())
    }

    private var isAuthPref by PrefDelegate(false)
    private var isDarkModePref by PrefDelegate(false)
    private var isBigTextPref by PrefDelegate(false)

    private val appSettings = MutableLiveData(
        AppSettings(isDarkModePref ?: false, isBigTextPref ?: false)
    )

    private val isAuth = MutableLiveData(isAuthPref ?: false)

    fun clearAll() {
        preferences.edit().clear().apply()
    }

    fun getAppSettings(): LiveData<AppSettings> = appSettings

    fun isAuth(): LiveData<Boolean> = isAuth

    fun setAppSettings(copy: AppSettings) {
        appSettings.value = copy
        isDarkModePref = copy.isDarkMode
        isBigTextPref = copy.isBigText
    }

    fun setAuth(auth: Boolean) {
        isAuth.value = auth
        isAuthPref = auth
    }
}