package org.baole.oned.util

import android.content.Context
import android.content.SharedPreferences
import android.text.format.DateUtils
import androidx.preference.PreferenceManager

class AppState(private val mPref: SharedPreferences) {
    companion object {
        lateinit var ins: AppState
        fun get(context: Context) : AppState{
            if (!::ins.isInitialized) {
                ins = AppState(PreferenceManager.getDefaultSharedPreferences(context))
            }

            return ins
        }


        const val PREF_LAST_STORY_TS = "last_story_ts"
        const val PREF_VIEW_MODE = "view_mode"

        const val VIEW_MODE_LIST = 0
        const val VIEW_MODE_PAGER = 1
    }

    fun setViewMode(mode: Int) {
        mPref.edit().putInt(PREF_VIEW_MODE, mode).apply()
    }

    fun getViewMode(): Int = mPref.getInt(PREF_VIEW_MODE, VIEW_MODE_LIST)

    fun getLastStoryTimestamp(): Long = mPref.getLong(PREF_LAST_STORY_TS, 0)
    fun markLastStoryTimestamp(ts: Long) {
        if (DateUtils.isToday(ts)) {
            mPref.edit().putLong(PREF_LAST_STORY_TS, ts).apply()
        }
    }
}