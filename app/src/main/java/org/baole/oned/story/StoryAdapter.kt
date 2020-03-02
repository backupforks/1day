
package org.baole.oned.story

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import org.baole.oned.model.Story
import org.baole.oned.util.AppState
import org.baole.oned.util.StoryObservable
import java.util.*


/**
 * RecyclerView adapter for displaying the results of a Firestore [Query].
 *
 * Note that this class forgoes some efficiency to gain simplicity. For example, the result of
 * [DocumentSnapshot.toObject] is not cached so the same object may be deserialized
 * many times as the user scrolls.
 */



abstract class StoryAdapter<VH : RecyclerView.ViewHolder>(private val context: Context, private var mQuery: Query) : RecyclerView.Adapter<VH>() {
    private var mRegistration: ListenerRegistration? = null
    private var mStories = mutableListOf<StoryData>()
    private val mStoriesMap = mutableMapOf<String, StoryData>()
    private var hasNext = false
    private var startAt = 0
    private var mLastSnapshot: DocumentSnapshot? = null

    private val mSnapshots = ArrayList<DocumentSnapshot>()
    private var hasTodayStory: Boolean
    private val mHeader = StoryData("1", Story("1", ""))

    init {
        hasTodayStory = AppState.get(context).hasTodayStory()
        if (!hasTodayStory) {
            mStories.add(mHeader)
        }

        AppState.get(context).mStoryObservable.addObserver { observer, arg ->
            Log.d(TAG, "addObserver $observer $arg")
            (observer as StoryObservable).getData()?.let {
                Log.d(TAG, "addObserver data=${it.mStory.content}")
                updateStory(it.mDocumentId, it.mStory)
            }
        }
    }


    fun updateStory(documentId: String, story: Story) {
        hasTodayStory = AppState.get(context).hasTodayStory()
        val data = StoryData(documentId, story)
        val newStories = mutableListOf<StoryData>()
        if (mStoriesMap.containsKey(documentId)) {
            mStoriesMap[documentId] = data
            newStories.addAll(mStoriesMap.values)
        } else {
            mStoriesMap[documentId] = data
            newStories.add(data)
            newStories.addAll(mStories)
        }

        newStories.sortByDescending { it.mStory.day }
        if (!hasTodayStory) newStories.add(0, mHeader)
        val diffResult = DiffUtil.calculateDiff(StoryDiffCallback(newStories, this.mStories))
        mStories = newStories
        diffResult.dispatchUpdatesTo(this)
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

    fun stopListening() {
        mRegistration?.remove()
        mRegistration = null

        mSnapshots.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mStories.size
    }


    protected fun getStory(position: Int): StoryData? {
        return mStories.getOrNull(position)
    }

    override fun getItemViewType(position: Int): Int {
        return if (!hasTodayStory && position == 0) {
            ITEM_TYPE_HEADER
        } else {
            ITEM_TYPE_DATA
        }
    }

    private fun getHeaderCount() = if (hasTodayStory) 0 else 1

    protected open fun onError(e: FirebaseFirestoreException) {}

    protected open fun onDataChanged() {}
    companion object {
        const val ITEM_PER_PAGE = 5
        const val ITEM_TYPE_HEADER = 0
        const val ITEM_TYPE_DATA = 1

        val TAG = StoryAdapter::class.java.simpleName
    }
}
