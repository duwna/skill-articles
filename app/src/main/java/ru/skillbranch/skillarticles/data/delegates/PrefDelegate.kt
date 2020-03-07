package ru.skillbranch.skillarticles.data.delegates

import androidx.core.content.edit
import ru.skillbranch.skillarticles.data.local.PrefManager
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PrefDelegate<T>(private val defaultValue: T) {

    private var storedValue: T? = null

    @Suppress("UNCHECKED_CAST")
    operator fun provideDelegate(
        thisRef: PrefManager,
        property: KProperty<*>
    ): ReadWriteProperty<PrefManager, T?> {

        val key = property.name

        return object : ReadWriteProperty<PrefManager, T?> {
            override fun getValue(thisRef: PrefManager, property: KProperty<*>): T? {
                if (storedValue == null) storedValue = when (defaultValue) {
                    is Boolean -> thisRef.preferences.getBoolean(key, defaultValue) as T
                    is String -> thisRef.preferences.getString(key, defaultValue) as T
                    is Float -> thisRef.preferences.getFloat(key, defaultValue) as T
                    is Int -> thisRef.preferences.getInt(key, defaultValue) as T
                    is Long -> thisRef.preferences.getLong(key, defaultValue) as T
                    else -> error("Only primitive types can be stored in Preferences")
                }
                return storedValue
            }

            override fun setValue(thisRef: PrefManager, property: KProperty<*>, value: T?) {
                storedValue = value
                when (value) {
                    is Boolean -> thisRef.preferences.edit { putBoolean(key, value) }
                    is String -> thisRef.preferences.edit { putString(key, value) }
                    is Float -> thisRef.preferences.edit { putFloat(key, value) }
                    is Int -> thisRef.preferences.edit { putInt(key, value) }
                    is Long -> thisRef.preferences.edit { putLong(key, value) }
                    else -> error("Only primitive types can be stored in Preferences")
                }
            }
        }
    }
}