package org.baole.oned.story

import org.baole.oned.model.Story

open class StoryAdapterData(val type: Int) {

    companion object {
        const val ITEM_TYPE_HEADER = 0
        const val ITEM_TYPE_DATA = 1
    }

    open fun areItemsTheSame(other: StoryAdapterData): Boolean = true
}

class StoryDataItem(val mDocumentId: String, val mStory: Story): StoryAdapterData(ITEM_TYPE_DATA) {
    var timestamp: Long = 0

    override fun areItemsTheSame(other: StoryAdapterData): Boolean {
        return if (other is StoryDataItem) {
            mDocumentId === other.mDocumentId
        } else {
            false
        }
    }
}