package ru.skillbranch.skillarticles.extensions

import java.text.SimpleDateFormat
import java.util.*

const val SECOND = 1000L
const val MINUTE = 60 * SECOND
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR


fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy"): String {

    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.shortFormat(): String {
    val pattern = if (this.isSameDay(Date())) "HH:mm" else "dd.MM.yy"
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.isSameDay(date: Date): Boolean {
    val day1 = this.time / DAY
    val day2 = date.time / DAY
    return day1 == day2
}

fun Date.add(value: Int, units: TimeUnits = TimeUnits.SECOND): Date {

    var time = this.time

    time += when (units) {
        TimeUnits.SECOND -> value * SECOND
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
    }

    this.time = time
    return this
}

fun Date.humanizeDiff(date: Date = Date()): String {

    if (date.time > this.time) {

        return when (val newTime = date.time - this.time) {

            in 0..SECOND -> "только что"

            in SECOND..45 * SECOND -> "несколько секунд назад"

            in 45 * SECOND..75 * SECOND -> "минуту назад"

            in 75 * SECOND..45 * MINUTE -> "${newTime / MINUTE} минут" +
                    if (newTime / MINUTE !in 11L..14L) {
                        when ((newTime / MINUTE) % 10) {
                            1L -> "y"
                            in 2L..4L -> "ы"
                            else -> ""
                        }
                    } else {
                        ""
                    } + " назад"

            in 45 * MINUTE..75 * MINUTE -> "час назад"

            in 75 * MINUTE..22 * HOUR -> "${newTime / HOUR} час" +
                    if (newTime / HOUR !in 11L..14L) {
                        when ((newTime / HOUR) % 10) {
                            1L -> ""
                            in 2L..4L -> "а"
                            else -> "ов"
                        }
                    } else {
                        "ов"
                    } + " назад"

            in 22 * HOUR..26 * HOUR -> "день назад"

            in 26 * HOUR..360 * DAY -> "${newTime / DAY} д" +
                    if (newTime / DAY !in 11L..14L) {
                        when ((newTime / DAY) % 10) {
                            1L -> "ень"
                            in 2L..4L -> "ня"
                            else -> "ней"
                        }
                    } else {
                        "ней"
                    } + " назад"

            else -> "более года назад"
        }
    } else {

        return when (val newTime = this.time - date.time) {

            in 0..SECOND -> "только что"

            in SECOND..45 * SECOND -> "через несколько секунд"

            in 45 * SECOND..75 * SECOND -> "через минуту"

            in 75 * SECOND..45 * MINUTE -> "через ${newTime / MINUTE} минут" +
                    if (newTime / MINUTE !in 11L..14L) {
                        when ((newTime / MINUTE) % 10) {
                            1L -> "y"
                            in 2L..4L -> "ы"
                            else -> ""
                        }
                    } else {
                        ""
                    }

            in 45 * MINUTE..75 * MINUTE -> "через час"

            in 75 * MINUTE..22 * HOUR -> "через ${newTime / HOUR} час" +
                    if (newTime / HOUR !in 11L..14L) {
                        when ((newTime / HOUR) % 10) {
                            1L -> ""
                            in 2L..4L -> "а"
                            else -> "ов"
                        }
                    } else {
                        "ов"
                    }

            in 22 * HOUR..26 * HOUR -> "через день"

            in 26 * HOUR..360 * DAY -> "через ${newTime / DAY} д" +
                    if (newTime / DAY !in 11L..14L) {
                        when ((newTime / DAY) % 10) {
                            1L -> "ень"
                            in 2L..4L -> "ня"
                            else -> "ней"
                        }
                    } else {
                        "ней"
                    }

            else -> "более чем через год"
        }
    }

}


enum class TimeUnits {
    SECOND,
    MINUTE,
    HOUR,
    DAY;

    fun plural(value: Int = 1): String {

        return when (this) {
            SECOND -> "$value секунд" +
                    if (value !in 11..14) {
                        when ((value) % 10) {
                            1 -> "у"
                            in 2..4 -> "ы"
                            else -> ""
                        }
                    } else {
                        ""
                    }
            MINUTE -> "$value минут" +
                    if (value !in 11..14) {
                        when ((value) % 10) {
                            1 -> "у"
                            in 2..4 -> "ы"
                            else -> ""
                        }
                    } else {
                        ""
                    }
            HOUR -> "$value час" +
                    if (value !in 11..14) {
                        when ((value) % 10) {
                            1 -> ""
                            in 2..4 -> "а"
                            else -> "ов"
                        }
                    } else {
                        "ов"
                    }
            DAY -> "$value д" +
                    if (value !in 11..14) {
                        when ((value) % 10) {
                            1 -> "ень"
                            in 2..4 -> "ня"
                            else -> "ней"
                        }
                    } else {
                        "ней"
                    }
        }
    }
}

