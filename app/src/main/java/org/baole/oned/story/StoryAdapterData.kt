package org.baole.oned.story

import org.baole.oned.model.Story

open class StoryAdapterData(val mType: Int) {

    companion object {
        const val ITEM_TYPE_HEADER = 0
        const val ITEM_TYPE_DATA = 1
    }

    open fun areItemsTheSame(other: StoryAdapterData): Boolean = true
}

class StoryAdapterItem(val mDocumentId: String, val mStory: Story): StoryAdapterData(ITEM_TYPE_DATA) {
    var timestamp: Long = 0

    override fun areItemsTheSame(other: StoryAdapterData): Boolean {
        return if (other is StoryAdapterItem) {
            mDocumentId === other.mDocumentId
        } else {
            false
        }
    }
}