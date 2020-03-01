
package org.baole.oned.story

import android.util.Log

import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*

import org.baole.oned.model.Story

import java.util.ArrayList

/**
 * RecyclerView adapter for displaying the results of a Firestore [Query].
 *
 * Note that this class forgoes some efficiency to gain simplicity. For example, the result of
 * [DocumentSnapshot.toObject] is not cached so the same object may be deserialized
 * many times as the user scrolls.
 */



abstract class StoryAdapter<VH : RecyclerView.ViewHolder>(private var mQuery: Query, var headerItemCount: Int = 0) : RecyclerView.Adapter<VH>() {
    private var mRegistration: ListenerRegistration? = null
    private var mStories = mutableListOf<StoryData>()
    private val mStoriesMap = mutableMapOf<String, StoryData>()
    private var hasNext = false
    private var startAt = 0
    private var mLastSnapshot: DocumentSnapshot? = null

    private val mSnapshots = ArrayList<DocumentSnapshot>()

    init {


    }

    fun startListening() {
        if (mRegistration == null) {
            mQuery.startAt(startAt)
            mQuery.limit(ITEM_PER_PAGE.toLong())
            mRegistration = mQuery.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                onSnapshot(querySnapshot, firebaseFirestoreException)
            }
        }
    }

    private fun onSnapshot(querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException?) {
        Log.d(TAG, "onSnapshot ${querySnapshot?.documents?.size}")
        hasNext = querySnapshot?.documents?.size ?: 0 >= ITEM_PER_PAGE
        val lastSize = itemCount
        var addedItemCount = 0
        querySnapshot?.documents?.forEach {
            val id = it.id
            Log.d(TAG, "onSnapshot add item $id")
            if (!mStoriesMap.containsKey(id)) {
                it.toObject(Story::class.java)?.let { story ->
                    val item = StoryData(it.id, story)
                    mStories.add(item)
                    mSnapshots.add(it)
                    mStoriesMap[id] = item
                    addedItemCount++
                }
            }

            mLastSnapshot = it
        }

        startAt += ITEM_PER_PAGE
        if (addedItemCount > 0) {
            notifyItemRangeInserted(lastSize, addedItemCount)
        }
    }

    fun loadNext() {
        Log.d(TAG, "loadNext $hasNext $startAt")
        if (hasNext) {
            mQuery.startAt(mStories.last().mStory.day).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                onSnapshot(querySnapshot, firebaseFirestoreException)
            }
        }
    }

    fun updateHeaderItemCount(count: Int) {
        this.headerItemCount = count
        this.notifyDataSetChanged()
    }

    fun stopListening() {
        mRegistration?.remove()
        mRegistration = null

        mSnapshots.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mStories.size + headerItemCount
    }

    protected fun getSnapshot(index: Int): DocumentSnapshot {
        return mSnapshots[index - headerItemCount]
    }


    protected fun getStory(position: Int): StoryData? {
        return mStories.getOrNull(position - headerItemCount)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < headerItemCount) {
            ITEM_TYPE_HEADER
        } else {
            ITEM_TYPE_DATA
        }
    }

    protected open fun onError(e: FirebaseFirestoreException) {}

    protected open fun onDataChanged() {}
    companion object {
        const val ITEM_PER_PAGE = 5
        const val ITEM_TYPE_HEADER = 0
        const val ITEM_TYPE_DATA = 1

        val TAG = StoryAdapter::class.java.simpleName
    }
}
