package org.baole.oned.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtil {
    const val KEY_FORMAT_PATTERN = "yyyy_MM_dd"

    fun day2key(timestamp: Long): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        return day2key(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
    }

    fun day2key( year: Int,  month: Int, day: Int): String {
        return String.format("%04d_%02d_%02d", year, month, day)
    }

    fun day2key(): String = day2key(System.currentTimeMillis())

    fun key2display(key: String): String {
        val time = SimpleDateFormat(KEY_FORMAT_PATTERN).parse(key).time
        return SimpleDateFormat("MMM dd").format(Date(time))
    }

    fun key2date(key: String): Date {
        return  SimpleDateFormat(KEY_FORMAT_PATTERN).parse(key)
    }
}