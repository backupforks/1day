package org.baole.oned

import androidx.multidex.MultiDexApplication
import org.baole.oned.util.StoryObservable

class OnedApp : MultiDexApplication() {

    companion object {
        lateinit var sApp: OnedApp
    }

    val mStoryObservable: StoryObservable = StoryObservable()


    override fun onCreate() {
        super.onCreate()
        sApp = this
    }
}