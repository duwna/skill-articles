package ru.skillbranch.skillarticles.data.providers.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.util.*

object DateAdapter {
    @FromJson
    fun fromJson(timestamp: Long) = Date(timestamp)

    @ToJson
    fun toJson(date: Date) = date.time
}