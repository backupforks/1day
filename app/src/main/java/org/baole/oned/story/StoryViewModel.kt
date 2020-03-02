/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.baole.oned.story

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.*
import org.baole.oned.OnedApp
import org.baole.oned.model.Story
import org.baole.oned.util.AppState
import org.baole.oned.util.StoryEditorEvent
import org.baole.oned.util.StoryObservable
import java.util.*

/**
 * ViewModel for Story list.
 */

class StoryData(val hasData: Boolean, val adapterData: List<StoryAdapterData>)

class StoryViewModel : ViewModel() {

    companion object {
        val TAG = StoryViewModel::class.java.simpleName
    }

    val mStoryLiveData = MutableLiveData<StoryData>()
    lateinit var mQuery: Query
    private var mLimit = 5L

    private var mRegistration: ListenerRegistration? = null
    private var mStories = mutableListOf<StoryAdapterItem>()
    private val mStoriesMap = mutableMapOf<String, StoryAdapterItem>()
    private var hasNext = false
    private var mLastSnapshot: DocumentSnapshot? = null

    private val mSnapshots = ArrayList<DocumentSnapshot>()
    private val mHeader = StoryAdapterData(StoryAdapterData.ITEM_TYPE_HEADER)

    init {
        OnedApp.sApp.mStoryObservable.addObserver { observer, arg ->
            Log.d(StoryAdapter.TAG, "addObserver $observer $arg")
            (observer as StoryObservable).getData()?.let {
                Log.d(StoryAdapter.TAG, "addObserver data=${it.data.mStory.content}")
                updateStory(it)
            }
        }
    }

    fun setQuery(query: Query, limit: Long) {
        this.mQuery = query
        this.mLimit = limit
        this.mQuery.limit(limit)
    }

    fun updateStory(data: StoryEditorEvent) {
        val newStories = mutableListOf<StoryAdapterItem>()
        when(data.type) {
            StoryEditorEvent.TYPE_ADDED -> {
                mStoriesMap[data.data.mDocumentId] = data.data
                newStories.add(data.data)
                newStories.addAll(mStories)
            }

            StoryEditorEvent.TYPE_UPDATED -> {
                mStoriesMap[data.data.mDocumentId] = data.data
                newStories.addAll(mStoriesMap.values)
            }

            StoryEditorEvent.TYPE_DELETED -> {
                mStoriesMap.remove(data.data.mDocumentId)
                newStories.addAll(mStoriesMap.values)
            }
        }

        newStories.sortByDescending { it.mStory.day }
        mStories = newStories
        updateData()
    }

    private fun updateData() {
        val newData = mutableListOf<StoryAdapterData>()
        val hasTodayData = AppState.get(OnedApp.sApp).hasTodayStory()
        Log.d(TAG, "hasTodayData=$hasTodayData")
        if(!hasTodayData) {
            newData.add(mHeader)
        }

        newData.addAll(mStories)
        mStoryLiveData.postValue(StoryData(mStories.isNotEmpty(), newData))
    }

    fun startListening() {
        if (mRegistration == null) {
            mRegistration = mQuery.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                onSnapshot(querySnapshot, firebaseFirestoreException)
            }
        }
    }

    private fun onSnapshot(querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException?) {
        Log.d(StoryAdapter.TAG, "onSnapshot ${querySnapshot?.documents?.size}")
        hasNext = querySnapshot?.documents?.size ?: 0 >= mLimit
        val lastSize = mStories.size
        var addedItemCount = 0
        querySnapshot?.documents?.forEach {
            val id = it.id
            Log.d(StoryAdapter.TAG, "onSnapshot add item $id")
            if (!mStoriesMap.containsKey(id)) {
                it.toObject(Story::class.java)?.let { story ->
                    val item = StoryAdapterItem(it.id, story)
                    mStories.add(item)
                    mSnapshots.add(it)
                    mStoriesMap[id] = item
                    addedItemCount++
                }
            }

            mLastSnapshot = it
        }

        if (addedItemCount > 0) {
            updateData()
        }
    }

    fun loadNext() {
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
    }
}
