package org.baole.oned.story.pager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestoreException
import org.baole.oned.R
import org.baole.oned.story.StoryFragment
import org.baole.oned.databinding.StoryPagerFragmentBinding
import org.baole.oned.story.list.StoryListFragment


class StoryPagerFragment : StoryFragment() {

    private lateinit var mAdapter: StoryPagerAdapter
    private lateinit var mBinding: StoryPagerFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = StoryPagerFragmentBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        setupViewModel()
    }

    private fun setupViewPager() {
        mAdapter = StoryPagerAdapter(this, mViewModel)
        mBinding.viewPager.adapter = mAdapter
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
        private val TAG = StoryPagerFragment::class.java.simpleName
    }
}