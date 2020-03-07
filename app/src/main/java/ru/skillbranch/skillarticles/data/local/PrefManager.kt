package ru.skillbranch.skillarticles.data.local

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager

class PrefManager(context: Context) {

    internal val preferences by lazy {
        PreferenceManager(context).sharedPreferences
    }

    fun clearAll() = preferences.edit { clear() }
}