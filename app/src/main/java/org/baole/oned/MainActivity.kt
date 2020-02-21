package org.baole.oned

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.format.DateUtils
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.baole.oned.databinding.MainActivityBinding
import org.baole.oned.model.Story
import org.baole.oned.util.DateUtil
import org.baole.oned.viewmodel.MainActivityViewModel
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var mViewModel: MainActivityViewModel
    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)

        setSupportActionBar(binding.toolbar)
        // View model
        mViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(BuildConfig.DEBUG)
        initFirestore()
   }

    private fun initFirestore() {
        mFirestore = FirebaseFirestore.getInstance()
    }

    public override fun onStart() {
        super.onStart()

        // Start sign in if necessary
        if (shouldStartSignIn()) {
            startSignIn()
            return
        } else {
            addStoryFragment()
        }

    }

    private fun addStoryFragment() {
        val fragment = StoryListFragment()
        supportFragmentManager.beginTransaction().add(R.id.content, fragment).commitAllowingStateLoss()
    }



    private fun onAddItemsClicked() {
        // Get a reference to the restaurants collection
        val user = FirebaseAuth.getInstance().currentUser!!
        val bookRef = mFirestore!!.collection(user.uid).document("book").collection("stories")
        val ts = Calendar.getInstance().timeInMillis

        for (i in 0..9) {
            val story = Story()
            story.timestamp = ts - i * DateUtils.DAY_IN_MILLIS
            story.day = DateUtil.day2key(story.timestamp)
            story.content = "a random generated content ${story.timestamp}"
            bookRef.document(DateUtil.day2key(story.timestamp)).set(story)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_items -> onAddItemsClicked()
            R.id.menu_sign_out -> {
                AuthUI.getInstance().signOut(this)
                startSignIn()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            mViewModel!!.isSigningIn = false

            if (resultCode != Activity.RESULT_OK && shouldStartSignIn()) {
                startSignIn()
            }
        }
    }

    private fun shouldStartSignIn(): Boolean {
        return !mViewModel.isSigningIn && FirebaseAuth.getInstance().currentUser == null
    }

    private fun startSignIn() {
        // Sign in with FirebaseUI
        val intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(listOf(AuthUI.IdpConfig.GoogleBuilder().build()))
                .setIsSmartLockEnabled(false)
                .build()

        startActivityForResult(intent, RC_SIGN_IN)
        mViewModel.isSigningIn = true
    }

    companion object {

        private val TAG = "MainActivity"

        private val RC_SIGN_IN = 9001

    }
}
