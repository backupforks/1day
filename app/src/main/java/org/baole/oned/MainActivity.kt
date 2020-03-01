package org.baole.oned

import android.content.Intent
import android.os.Bundle
import android.text.format.DateUtils
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
import org.baole.oned.story.ex.StoryBlockFragment
import org.baole.oned.story.list.StoryListFragment
import org.baole.oned.story.pager.StoryPagerFragment
import org.baole.oned.util.AppState
import org.baole.oned.util.DateUtil
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
        val fragment = when(AppState.get(this).getViewMode()) {
            AppState.VIEW_MODE_PAGER -> StoryPagerFragment()
            AppState.VIEW_MODE_LIST -> StoryListFragment()
            AppState.VIEW_MODE_BLOCK -> StoryBlockFragment()
            else -> StoryListFragment()
        }
        supportFragmentManager.beginTransaction().replace(R.id.content, fragment).commitAllowingStateLoss()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
         menu.findItem(R.id.menu_view)?.let {
             when(AppState.get(this).getViewMode()) {
                 AppState.VIEW_MODE_PAGER -> {
                     it.setIcon(R.drawable.ic_view_carousel_black_24dp)
                     menu.findItem(R.id.menu_view_pager)?.isChecked = true
                 }
                 AppState.VIEW_MODE_LIST -> {
                     it.setIcon(R.drawable.ic_view_list_black_24dp)
                     menu.findItem(R.id.menu_view_list)?.isChecked = true
                 }
                 AppState.VIEW_MODE_BLOCK -> {
                     it.setIcon(R.drawable.ic_view_carousel_black_24dp)
                     menu.findItem(R.id.menu_view_block)?.isChecked = true
                 }
             }
         }
        menu.findItem(R.id.menu_sign_in).isVisible = !isSignIn()
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sign_in -> {
                backupStoriesAndSignIn()
                updateEmptyView()
            }

            R.id.menu_settings -> {
                startActivityForResult(Intent(this, SettingActivity::class.java), RC_SETTINGS)
//                var ts = System.currentTimeMillis() - 10 * DateUtils.DAY_IN_MILLIS
//                val user = FirebaseAuth.getInstance().currentUser
//                for (index in 0..100) {
//                    FirestoreUtil.story(mFirestore, user).document().set(Story(DateUtil.day2key(ts), "$index Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum"))
//                    ts -= DateUtils.DAY_IN_MILLIS
//                }
            }

            R.id.menu_view_list -> {
                onSelectView(AppState.VIEW_MODE_LIST)
            }

            R.id.menu_view_pager -> {
                onSelectView(AppState.VIEW_MODE_PAGER)
            }

            R.id.menu_view_block -> {
                onSelectView(AppState.VIEW_MODE_BLOCK)
            }

            R.id.menu_new_story -> {
                newStory()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onSelectView(mode: Int) {
        if (AppState.get(this).getViewMode() != mode) {
            AppState.get(this).setViewMode(mode)
            addStoryFragment()
            invalidateOptionsMenu()
        }
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
        } else if (requestCode == RC_SETTINGS) {
            addStoryFragment(true)
            updateEmptyView()
            invalidateOptionsMenu()
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
        private val TAG = MainActivity::class.java.simpleName
        private val RC_SIGN_IN = 9001
        private val RC_SETTINGS = 9002

    }
}
