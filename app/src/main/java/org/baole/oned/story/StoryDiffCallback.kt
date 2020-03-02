package org.baole.oned.story

import androidx.recyclerview.widget.DiffUtil


class StoryDiffCallback(val newPersons: List<StoryAdapterData>, val oldPersons: List<StoryAdapterData>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldPersons.size
    }

    override fun getNewListSize(): Int {
        return newPersons.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldPersons[oldItemPosition].areItemsTheSame(newPersons[newItemPosition])
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldPersons[oldItemPosition] == newPersons[newItemPosition]
    }
}