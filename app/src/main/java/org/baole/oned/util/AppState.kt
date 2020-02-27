package org.baole.oned.util

import android.content.Context
import android.content.SharedPreferences
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
    }

    fun getLastStoryTimestamp(): Long = mPref.getLong(PREF_LAST_STORY_TS, 0)
    fun markLastStoryTimestamp() {
        val now = System.currentTimeMillis()
        val lastStory = getLastStoryTimestamp();
        if (now > lastStory) {
            mPref.edit().putLong(PREF_LAST_STORY_TS, now).apply()
        }

    }
}