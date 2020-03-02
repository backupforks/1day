package org.baole.oned

import androidx.multidex.MultiDexApplication
import io.noties.markwon.Markwon
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.movement.MovementMethodPlugin
import org.baole.oned.util.StoryObservable

class OnedApp : MultiDexApplication() {

    companion object {
        lateinit var sApp: OnedApp
    }

    val mStoryObservable: StoryObservable = StoryObservable()
    lateinit var mMarkwon: Markwon

    override fun onCreate() {
        super.onCreate()
        sApp = this
        mMarkwon = Markwon
                .builder(this)
                .usePlugin(StrikethroughPlugin.create())
                .usePlugin(MovementMethodPlugin.create())
                .build()

    }
}