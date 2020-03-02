package org.baole.oned.util

import org.baole.oned.story.StoryData
import java.util.*

class StoryObservable: Observable() {
    private var data: StoryData? = null

    fun getData(): StoryData? = data
    fun setData(data: StoryData) {
        this.data = data
        this.setChanged()
        notifyObservers()
    }
}