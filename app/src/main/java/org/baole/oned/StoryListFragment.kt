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

    private lateinit var mAdapter: StoryAdapter
    private  var mQuery: Query? = null
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
    }

    private fun initFirestore() {
        mFirestore = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser


        val isPersistent = FirebaseAuth.getInstance().currentUser != null
        val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(isPersistent)
                .build()
        mFirestore.firestoreSettings = settings



        mQuery = FirestoreUtil.story(mFirestore, user)
                .orderBy(Story.FIELD_TIMESTAMP, Query.Direction.DESCENDING)
                .limit(LIMIT.toLong())
    }

    private fun initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView")
        }

        mAdapter = object : StoryAdapter(mQuery!!, {
            startActivity(Intent(activity, StoryEditorActivity::class.java).putExtra(Story.FIELD_DAY, DateUtil.day2key()))
        }, {
            startActivity(Intent(activity, StoryEditorActivity::class.java).putExtra(Story.FIELD_DAY, it.day))
        }) {

            override fun onDataChanged() {
                // Show/hide content if the query returns empty.
                if (itemCount == 0) {
                    binding.recyclerStories.visibility = View.GONE
                    binding.viewEmpty.visibility = View.VISIBLE
                } else {
                    binding.recyclerStories.visibility = View.VISIBLE
                    binding.viewEmpty.visibility = View.GONE
                }
            }

            override fun onError(e: FirebaseFirestoreException) {
                // Show a snackbar on errors
                Snackbar.make(binding.root,"Error: check logs for info.", Snackbar.LENGTH_LONG).show()
            }
        }

        binding.recyclerStories.layoutManager = LinearLayoutManager(context)
        binding.recyclerStories.adapter = mAdapter
    }

    override fun onStart() {
        super.onStart()

        // Start sign in if necessary
//        if (shouldStartSignIn()) {
//            startSignIn()
//            return
//        }

        // Apply filters
//        onFilter(mViewModel.filters)

        // Start listening for Firestore updates
        mAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        mAdapter.stopListening()
    }


//
    fun onFilter(filters: Filters) {
        // Construct query basic query
        var query: Query = mFirestore!!.collection("restaurants")

        // Category (equality filter)
        if (filters.hasCategory()) {
            query = query.whereEqualTo("category", filters.category)
        }

        // City (equality filter)
        if (filters.hasCity()) {
            query = query.whereEqualTo("city", filters.city)
        }

        // Price (equality filter)
        if (filters.hasPrice()) {
            query = query.whereEqualTo("price", filters.price)
        }

        // Sort by (orderBy with direction)
        if (filters.hasSortBy()) {
            query = query.orderBy(filters.sortBy!!, filters.sortDirection!!)
        }

        // Limit items
        query = query.limit(LIMIT.toLong())
        // Update the query
        mQuery = query
        mAdapter.setQuery(query)

        // Set header
//        mCurrentSearchView!!.text = Html.fromHtml(filters.getSearchDescription(this))
//        mCurrentSortByView!!.text = filters.getOrderDescription(this)

        // Save filters
        mViewModel.filters = filters

        // Set header
//        mCurrentSearchView!!.text = Html.fromHtml(filters.getSearchDescription(this))
//        mCurrentSortByView!!.text = filters.getOrderDescription(this)

        // Save filters
//        mViewModel.filters = filters
    }
//
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return super.onCreateOptionsMenu(menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.menu_add_items -> onAddItemsClicked()
//            R.id.menu_sign_out -> {
//                AuthUI.getInstance().signOut(this)
//                startSignIn()
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == RC_SIGN_IN) {
//            mViewModel!!.isSigningIn = false
//
//            if (resultCode != Activity.RESULT_OK && shouldStartSignIn()) {
//                startSignIn()
//            }
//        }
//    }
//
//    override fun onClick(v: View) {
//        when (v.id) {
//            R.id.filter_bar -> onFilterClicked()
//            R.id.button_clear_filter -> onClearFilterClicked()
//        }
//    }
//
//    fun onFilterClicked() {
//        // Show the dialog containing filter options
//        mFilterDialog!!.show(supportFragmentManager, FilterDialogFragment.TAG)
//    }
//
//    fun onClearFilterClicked() {
//        mFilterDialog!!.resetFilters()
//
//        onFilter(Filters.default)
//    }
//
//    override fun onRestaurantSelected(restaurant: DocumentSnapshot) {
//        // Go to the details page for the selected restaurant
//        val intent = Intent(this, RestaurantDetailActivity::class.java)
//        intent.putExtra(RestaurantDetailActivity.KEY_RESTAURANT_ID, restaurant.id)
//
//        startActivity(intent)
//    }
//
//    private fun shouldStartSignIn(): Boolean {
//        return !mViewModel!!.isSigningIn && FirebaseAuth.getInstance().currentUser == null
//    }
//
//    private fun startSignIn() {
//        // Sign in with FirebaseUI
//        val intent = AuthUI.getInstance().createSignInIntentBuilder()
//                .setAvailableProviders(listOf(AuthUI.IdpConfig.EmailBuilder().build()))
//                .setIsSmartLockEnabled(false)
//                .build()
//
//        startActivityForResult(intent, RC_SIGN_IN)
//        mViewModel!!.isSigningIn = true
//    }
//
//    private fun showTodoToast() {
//        Toast.makeText(this, "TODO: Implement", Toast.LENGTH_SHORT).show()
//    }
//
    companion object {
        private val LIMIT = 50

        private val TAG = StoryListFragment::class.java.simpleName
    }
}