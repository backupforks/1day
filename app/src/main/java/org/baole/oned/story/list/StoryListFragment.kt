package org.baole.oned.story.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import org.baole.oned.databinding.StoryListFragmentBinding
import org.baole.oned.story.StoryFragment


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

    private fun setupRecyclerView() {
        mAdapter = StoryListAdapter(this, mViewModel)
        mBinding.recyclerStories.layoutManager = LinearLayoutManager(context)
        mBinding.recyclerStories.adapter = mAdapter
    }

    private fun setupViewModel() {
        mViewModel.setQuery(mQuery, ITEM_PER_PAGE)
        mViewModel.mStoryLiveData.observe(viewLifecycleOwner, Observer {
            mAdapter.setStories(it.adapterData)
            main()?.showEmptyView(!it.hasData)
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
        private const val ITEM_PER_PAGE = 5L
        private val TAG = StoryListFragment::class.java.simpleName
    }
}