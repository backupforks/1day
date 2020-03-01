package org.baole.oned.story.ex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.firestore.Query
import org.baole.oned.story.StoryFragment
import org.baole.oned.databinding.StoryListFragmentBinding
import org.baole.oned.model.Story
import org.baole.oned.util.FirestoreUtil


class StoryBlockFragment : StoryFragment() {

    private lateinit var mAdapter: StoryAdapter
    private lateinit var mBinding: StoryListFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = StoryListFragmentBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }


    private fun initRecyclerView() {
        mQuery = FirestoreUtil.story(mFirestore, mFirebaseUser)
                .orderBy(Story.FIELD_DAY, Query.Direction.DESCENDING)

        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(20)
                .build()

        val options = FirestorePagingOptions.Builder<Story>()
                .setLifecycleOwner(this)
                .setQuery(mQuery, config, Story::class.java)
                .build()
        mAdapter = StoryAdapter(mQuery, {
            editStory()
        }, {
            editStory()
        }, options)

        mBinding.recyclerStories.layoutManager = LinearLayoutManager(context)
        mBinding.recyclerStories.adapter = mAdapter
    }

//    override fun onStart() {
//        super.onStart()
//        mAdapter.startListening()
//    }
//
//    override fun onStop() {
//        super.onStop()
//        mAdapter.stopListening()
//    }


    companion object {
        private val TAG = StoryBlockFragment::class.java.simpleName
    }
}