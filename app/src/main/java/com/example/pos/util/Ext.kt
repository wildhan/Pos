package com.example.pos.util

import android.net.Uri
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


const val DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ"
const val DEFAULT_DATE_FORMAT = "dd MMMM yyyy"

fun String.parseDate(fromFormat: String = DATE_TIME_FORMAT): Long {
    val sdf = SimpleDateFormat(fromFormat, Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.parse(this)?.time ?: 0L
}

fun currentDate(): String {
    val sdf = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
    val currentDate = sdf.format(Date())
    return currentDate
}

fun Long.formatDate(toFormat: String = DATE_TIME_FORMAT): String {
    val sdf = SimpleDateFormat(toFormat, Locale.getDefault())
    sdf.timeZone = TimeZone.getDefault()
    return sdf.format(Date(this))
}

fun String.parseFormatDate(
    fromFormat: String = DATE_TIME_FORMAT,
    toFormat: String = DEFAULT_DATE_FORMAT
): String {
    return this.parseDate(fromFormat).formatDate(toFormat)
}

fun String.toUri(): Uri {
    return Uri.parse(this)
}

fun Long.toRpFormat(): String {
    return "Rp${String.format(Locale("in","ID"), "%,d", this)}"
}
