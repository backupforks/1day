package org.baole.oned.story.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestoreException
import org.baole.oned.R
import org.baole.oned.StoryFragment
import org.baole.oned.databinding.StoryListFragmentBinding


class StoryListFragment : StoryFragment() {

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
        mAdapter = object : StoryAdapter(mQuery, {
            editStory()
        }, {
            editStory(it.id)
        }) {
            override fun onDataChanged() {
                main()?.showEmptyView(itemCount <= headerItemCount)
            }

            override fun onError(e: FirebaseFirestoreException) {
                Snackbar.make(mBinding.root, R.string.error_load_data, Snackbar.LENGTH_LONG).show()
            }
        }

        mBinding.recyclerStories.layoutManager = LinearLayoutManager(context)
        mBinding.recyclerStories.adapter = mAdapter
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
        private val TAG = StoryListFragment::class.java.simpleName
    }
}