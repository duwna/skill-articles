package ru.skillbranch.skillarticles.data.delegates

import androidx.core.content.edit
import ru.skillbranch.skillarticles.data.local.PrefManager
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PrefDelegate<T>(private val defaultValue: T) : ReadWriteProperty<PrefManager, T?> {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: PrefManager, property: KProperty<*>): T? {
        return when (defaultValue) {
            is Boolean -> thisRef.preferences.getBoolean(property.name, defaultValue) as T?
            is String -> thisRef.preferences.getString(property.name, defaultValue) as T?
            is Float -> thisRef.preferences.getFloat(property.name, defaultValue) as T?
            is Int -> thisRef.preferences.getInt(property.name, defaultValue) as T?
            is Long -> thisRef.preferences.getLong(property.name, defaultValue) as T?
            else -> throw IllegalArgumentException("Only primitive types")
        }
    }

    override fun setValue(thisRef: PrefManager, property: KProperty<*>, value: T?) {
        when (value) {
            is Boolean -> thisRef.preferences.edit { putBoolean(property.name, value) }
            is String -> thisRef.preferences.edit { putString(property.name, value) }
            is Float -> thisRef.preferences.edit { putFloat(property.name, value) }
            is Int -> thisRef.preferences.edit { putInt(property.name, value) }
            is Long -> thisRef.preferences.edit { putLong(property.name, value) }
            else -> throw IllegalArgumentException("Only primitive types")
        }
    }

}