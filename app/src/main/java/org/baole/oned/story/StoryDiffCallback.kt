package org.baole.oned.story

import androidx.recyclerview.widget.DiffUtil


class StoryDiffCallback(val newPersons: List<StoryData>, val oldPersons: List<StoryData>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldPersons.size
    }

    override fun getNewListSize(): Int {
        return newPersons.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldPersons[oldItemPosition].mDocumentId === newPersons[newItemPosition].mDocumentId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldPersons[oldItemPosition] == newPersons[newItemPosition]
    }
}