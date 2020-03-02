
package org.baole.oned.story

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query


/**
 * RecyclerView adapter for displaying the results of a Firestore [Query].
 *
 * Note that this class forgoes some efficiency to gain simplicity. For example, the result of
 * [DocumentSnapshot.toObject] is not cached so the same object may be deserialized
 * many times as the user scrolls.
 */



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

    protected open fun onError(e: FirebaseFirestoreException) {}

    protected open fun onDataChanged() {}
    companion object {
        const val ITEM_PER_PAGE = 5
        val TAG = StoryAdapter::class.java.simpleName
    }
}
