package org.baole.oned

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import org.baole.oned.adapter.StoryAdapter
import org.baole.oned.databinding.StoryListFragmentBinding
import org.baole.oned.model.Story
import org.baole.oned.util.DateUtil
import org.baole.oned.util.FirestoreUtil
import org.baole.oned.viewmodel.MainActivityViewModel


class StoryListFragment : StoryFragment() {

    private var mFirebaseUser: FirebaseUser? = null
    private lateinit var mAdapter: StoryAdapter
    private var mQuery: Query? = null
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var mViewModel: MainActivityViewModel
    private lateinit var binding: StoryListFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = StoryListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // View model
        mViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        // Initialize Firestore and the main RecyclerView
        initFirestore()
        initRecyclerView()
        initEmptyView()
    }

    private fun initEmptyView() {
        binding.signIn.setOnClickListener {
            (activity as MainActivity).backupStoriesAndSignIn()
        }

        binding.newStory.setOnClickListener {
            newStory()
        }
        if (mFirebaseUser != null) {
            binding.signIn.visibility = View.INVISIBLE
        }
    }

    private fun initFirestore() {
        mFirestore = FirebaseFirestore.getInstance()
        Log.d(TAG, "firestore: $mFirestore")

        mFirebaseUser = FirebaseAuth.getInstance().currentUser
        mQuery = FirestoreUtil.story(mFirestore, mFirebaseUser)
                .orderBy(Story.FIELD_DAY, Query.Direction.DESCENDING)
                .limit(LIMIT.toLong())
    }


    private fun initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView")
        }

        mAdapter = object : StoryAdapter(mQuery!!, {
            newStory()
        }, {
            newStory(it.id)
        }) {
            override fun onDataChanged() {
                // Show/hide content if the query returns empty.
                if (itemCount <= 1) {
                    binding.recyclerStories.visibility = View.GONE
                    binding.viewEmpty.visibility = View.VISIBLE
                } else {
                    binding.recyclerStories.visibility = View.VISIBLE
                    binding.viewEmpty.visibility = View.GONE
                }
            }

            override fun onError(e: FirebaseFirestoreException) {
                // Show a snackbar on errors
                Snackbar.make(binding.root, R.string.error_load_data, Snackbar.LENGTH_LONG).show()
            }
        }

        binding.recyclerStories.layoutManager = LinearLayoutManager(context)
        binding.recyclerStories.adapter = mAdapter
    }

    private fun newStory(storyId: String = "") {
        startActivity(Intent(activity, StoryEditorActivity::class.java).putExtra(FirestoreUtil.FIELD_ID, storyId))
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
        private val LIMIT = 50

        private val TAG = StoryListFragment::class.java.simpleName
    }
}