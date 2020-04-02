package org.baole.oned.story

import androidx.recyclerview.widget.DiffUtil


class StoryDiffCallback(val mNewData: List<StoryAdapterData>, val mOldData: List<StoryAdapterData>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return mOldData.size
    }

    override fun getNewListSize(): Int {
        return mNewData.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldData[oldItemPosition].areItemsTheSame(mNewData[newItemPosition])
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldData[oldItemPosition] == mNewData[newItemPosition]
    }
}