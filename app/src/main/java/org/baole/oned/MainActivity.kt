package org.baole.oned

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import org.baole.oned.databinding.MainActivityBinding
import org.baole.oned.model.Story
import org.baole.oned.util.FirestoreUtil
import org.baole.oned.viewmodel.MainActivityViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var mViewModel: MainActivityViewModel
    private lateinit var mBinding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = MainActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setSupportActionBar(mBinding.toolbar)
        mViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        // Enable Firestore logging
        setupFirestore()
        initEmptyView()
        addStoryFragment()
        Log.d(TAG, "firestore: ${FirebaseFirestore.getInstance()}")
    }

    private fun setupFirestore() {
        mFirestore = FirebaseFirestore.getInstance()
        FirebaseFirestore.setLoggingEnabled(BuildConfig.DEBUG)
        val isPersistent = FirebaseAuth.getInstance().currentUser != null
        if (isPersistent) {
            mFirestore.enableNetwork().addOnFailureListener {
                it.printStackTrace()
            }
        } else {
            mFirestore.disableNetwork().addOnFailureListener {
                it.printStackTrace()
            }
        }
    }

    private fun addStoryFragment(replace: Boolean = false) {
        supportFragmentManager.beginTransaction().replace(R.id.content, StoryListFragment()).commitAllowingStateLoss()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val isSignIn = isSignIn()

        if (isSignIn) {
            menu.findItem(R.id.menu_sign_in).isVisible = false
        } else {
            menu.findItem(R.id.menu_sign_out).isVisible = false
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sign_out -> {
                AuthUI.getInstance().signOut(this).addOnSuccessListener {
                    FirebaseFirestore.getInstance().let { ff ->
                        addStoryFragment(true)
                        updateEmptyView()
                        invalidateOptionsMenu()
                    }
                }
            }
            R.id.menu_sign_in -> {
                backupStoriesAndSignIn()
                updateEmptyView()
            }

            R.id.menu_settings -> {
                startActivity(Intent(this, SettingActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    var backupStories = mutableListOf<Story>()
    var backupListener: ListenerRegistration? = null

    fun backupStoriesAndSignIn() {
        Log.d(TAG, "data: backupStoriesAndSignIn")
        backupListener = FirestoreUtil.story(mFirestore, null)
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    backupListener?.remove()
                    querySnapshot?.documents?.forEach {
                        it.toObject(Story::class.java)?.let { story ->
                            Log.d(TAG, "data: $story")
                            backupStories.add(story)
                        }
                    }
                    Log.d(TAG, "data:  backup data size: ${backupStories.size}")

                    startSignIn()
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            invalidateOptionsMenu()
            if (isSignIn()) {
                moveStoriesFromLocal2Cloud(FirebaseAuth.getInstance().currentUser!!)
                setupFirestore()
                addStoryFragment(true)
            }
        }
    }

    private fun moveStoriesFromLocal2Cloud(user: FirebaseUser) {
        Log.d(TAG, "data: moveStoriesFromLocal2Cloud ${backupStories.size}")
        val stories = mutableListOf<Story>()
        stories.addAll(backupStories)
        backupStories.clear()
        stories.forEach {
            Log.d(TAG, "data: move $it")
            FirestoreUtil.story(mFirestore, user).document().set(it)
        }

    }

    private fun isSignIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    private fun startSignIn() {
        // Sign in with FirebaseUI
        Log.d(TAG, "startSignIn")
        val intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(listOf(AuthUI.IdpConfig.GoogleBuilder().build()))
                .setIsSmartLockEnabled(false)
                .build()

        startActivityForResult(intent, RC_SIGN_IN)
    }


    private fun initEmptyView() {
        mBinding.viewEmpty.signIn.setOnClickListener {
            backupStoriesAndSignIn()
        }

        mBinding.viewEmpty.newStory.setOnClickListener {
            newStory()
        }

        updateEmptyView()
    }

    private fun updateEmptyView() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            mBinding.viewEmpty.signIn.visibility = View.INVISIBLE
        }
    }

    private fun newStory() {
        startActivity(Intent(this, StoryEditorActivity::class.java).putExtra(FirestoreUtil.FIELD_ID, ""))
    }

    fun showEmptyView(visible: Boolean) {
        if (visible) {
            mBinding.content.visibility = View.GONE
            mBinding.viewEmpty.viewEmpty.visibility = View.VISIBLE
        } else {
            mBinding.content.visibility = View.VISIBLE
            mBinding.viewEmpty.viewEmpty.visibility = View.GONE
        }
    }

    companion object {

        private val TAG = "MainActivity"

        private val RC_SIGN_IN = 9001

    }
}
