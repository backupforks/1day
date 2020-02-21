package org.baole.oned.util

import java.util.*

object DateUtil {
    fun day2key(timestamp: Long): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp

        return String.format("%04d_%02d_%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
    }

    fun day2key(): String = day2key(System.currentTimeMillis())
}