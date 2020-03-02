
package org.baole.oned.story

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class StoryAdapter<VH : RecyclerView.ViewHolder>(private val context: Context) : RecyclerView.Adapter<VH>() {
    private var mStories = listOf<StoryAdapterData>()

    fun setStories(stories: List<StoryAdapterData>) {
        val diffResult = DiffUtil.calculateDiff(StoryDiffCallback(stories, this.mStories))
        mStories = stories
        diffResult.dispatchUpdatesTo(this)

    }

    override fun getItemCount(): Int {
        return mStories.size
    }


    protected fun getStory(position: Int): StoryAdapterData {
        return mStories[position]
    }

    override fun getItemViewType(position: Int): Int {
        return mStories[position].type
    }

    companion object {
        val TAG = StoryAdapter::class.java.simpleName
    }
}
