package org.baole.oned.story.monthlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.Query
import org.baole.oned.databinding.StoryListFragmentBinding
import org.baole.oned.model.Story
import org.baole.oned.story.StoryFragment
import org.baole.oned.story.list.StoryListAdapter
import org.baole.oned.util.DateUtil
import org.baole.oned.util.FirestoreUtil
import java.util.*


class StoryListFragment : StoryFragment() {
    private lateinit var mAdapter: StoryListAdapter
    private lateinit var mBinding: StoryListFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = StoryListFragmentBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupViewModel()
    }

    override fun setupQuery() {
        val ts = arguments?.getLong(MonthListFragment.DAY, 0) ?: 0
        val date = Calendar.getInstance()
        date.timeInMillis = ts

        val start = Calendar.getInstance()
        val end = Calendar.getInstance()
        start.set(Calendar.YEAR, date.get(Calendar.YEAR))
        end.set(Calendar.YEAR, date.get(Calendar.YEAR))

        start.set(Calendar.MONTH, date.get(Calendar.MONTH))

        start.set(Calendar.DAY_OF_MONTH, 1)
        end.set(Calendar.DAY_OF_MONTH, 1)

        start.set(Calendar.HOUR_OF_DAY, 0)
        end.set(Calendar.DAY_OF_MONTH, 0)
        start.set(Calendar.MINUTE, 0)
        end.set(Calendar.MINUTE, 0)
        start.set(Calendar.SECOND, 0)
        end.set(Calendar.SECOND, 0)

        end.set(Calendar.MONTH, start.get(Calendar.MONTH) + 1)

        Log.d(TAG, "query ${DateUtil.day2key(start.timeInMillis)}-> ${DateUtil.day2key(end.timeInMillis)}")

        mQuery = FirestoreUtil.story(mFirestore, mFirebaseUser)
                .orderBy(Story.FIELD_DAY, Query.Direction.DESCENDING)
                .whereGreaterThanOrEqualTo(Story.FIELD_DAY, DateUtil.day2key(start.timeInMillis))
                .whereLessThanOrEqualTo(Story.FIELD_DAY, DateUtil.day2key(end.timeInMillis))
    }

    private fun setupRecyclerView() {
        mAdapter = StoryListAdapter(this, mViewModel)
        mBinding.recyclerStories.layoutManager = LinearLayoutManager(context)
        mBinding.recyclerStories.adapter = mAdapter
    }

    private fun setupViewModel() {
        mViewModel.setQuery(mQuery, ITEM_PER_PAGE)
        mViewModel.mCheckToday = arguments?.getBoolean(MonthListFragment.IS_TODAY, false) ?: false
        mViewModel.mStoryLiveData.observe(viewLifecycleOwner, Observer {
            mAdapter.setStories(it.adapterData)
//            main()?.showEmptyView(!it.hasData)
        })
    }

    override fun onStart() {
        super.onStart()
        mViewModel.startListening()
    }

    override fun onStop() {
        super.onStop()
        mViewModel.stopListening()
    }

    companion object {
        private const val ITEM_PER_PAGE = 50L
        private val TAG = StoryListFragment::class.java.simpleName
    }
}