package ru.skillbranch.skillarticles.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.preference.PreferenceManager
import com.squareup.moshi.Moshi
import ru.skillbranch.skillarticles.data.delegates.PrefDelegate
import ru.skillbranch.skillarticles.data.delegates.PrefLiveDelegate
import ru.skillbranch.skillarticles.data.delegates.PrefLiveObjDelegate
import ru.skillbranch.skillarticles.data.delegates.PrefObjDelegate
import ru.skillbranch.skillarticles.data.models.AppSettings
import ru.skillbranch.skillarticles.data.models.User

class PrefManager(context: Context, moshi: Moshi) {

    internal val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    var isDarkMode by PrefDelegate(false)
    var isBigText by PrefDelegate(false)
    var accessToken by PrefDelegate("")
    var refreshToken by PrefDelegate("")
    var profile: User? by PrefObjDelegate(moshi.adapter(User::class.java))

    val isAuthLive: LiveData<Boolean> by lazy {
        val token by PrefLiveDelegate("accessToken", "", preferences)
        token.map { it.isNotEmpty() }
    }

    val profileLive by PrefLiveObjDelegate("profile", moshi.adapter(User::class.java), preferences)

    val appSettings = MediatorLiveData<AppSettings>().apply {
        val isDarkModeLive: LiveData<Boolean> by PrefLiveDelegate("isDarkMode", false, preferences)
        val isBigTextLive: LiveData<Boolean> by PrefLiveDelegate("isBigText", false, preferences)
        value = AppSettings()

        addSource(isDarkModeLive) {
            value = value!!.copy(isDarkMode = it)
        }

        addSource(isBigTextLive) {
            value = value!!.copy(isBigText = it)
        }
    }.distinctUntilChanged()

    fun clearAll() {
        preferences.edit().clear().apply()
    }

    fun replaceAvatarUrl(url: String) {
        profile = profile!!.copy(avatar = url)
    }

    fun replaceAvatarUrl(url: String) {
        profile = profile!!.copy(avatar = url)
    }
}