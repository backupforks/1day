package org.baole.oned.story.pager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestoreException
import org.baole.oned.R
import org.baole.oned.StoryFragment
import org.baole.oned.databinding.StoryPagerFragmentBinding


class StoryPagerFragment : StoryFragment() {

    private lateinit var mAdapter: StoryAdapter
    private lateinit var mBinding: StoryPagerFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = StoryPagerFragmentBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()
    }

    private fun initViewPager() {
        mAdapter = object : StoryAdapter(mQuery, {
            editStory()
        }, {
            editStory(it.id)
        }, if (hasTodayStory()) 0 else 1) {
            override fun onDataChanged() {
                main()?.showEmptyView(itemCount <= headerItemCount)
                val newHeaderItemCount = if (hasTodayStory()) 0 else 1

                Log.d(TAG, "onDataChanged $newHeaderItemCount")
                if (newHeaderItemCount != headerItemCount) {
                    updateHeaderItemCount(newHeaderItemCount)
                }
            }

            override fun onError(e: FirebaseFirestoreException) {
                Snackbar.make(mBinding.root, R.string.error_load_data, Snackbar.LENGTH_LONG).show()
            }
        }

        mBinding.viewPager.adapter = mAdapter
    }

    override fun onStart() {
        super.onStart()
        mAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        mAdapter.stopListening()
    }


    companion object {
        private val TAG = StoryPagerFragment::class.java.simpleName
    }
}