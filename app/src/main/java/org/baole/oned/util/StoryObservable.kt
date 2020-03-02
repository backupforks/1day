package org.baole.oned.util

import org.baole.oned.story.StoryAdapterItem
import java.util.*


class StoryEditorEvent(val type: Int, val data: StoryAdapterItem) {
    companion object {
        const val TYPE_ADDED = 0
        const val TYPE_UPDATED = 1
        const val TYPE_DELETED = 2
    }
}

class StoryObservable: Observable() {
    private var data: StoryEditorEvent? = null

    fun getData(): StoryEditorEvent? = data
    fun setData(data: StoryEditorEvent) {
        this.data = data
        this.setChanged()
        notifyObservers()
    }
}