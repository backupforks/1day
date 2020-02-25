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
        addStoryFragment()

   }

    private fun addStoryFragment(replace: Boolean = false) {
        val fragment = StoryListFragment()
        if (replace) {
            supportFragmentManager.beginTransaction().replace(R.id.content, fragment).commitAllowingStateLoss()
        } else {
            supportFragmentManager.beginTransaction().add(R.id.content, fragment).commitAllowingStateLoss()
        }
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
//            R.id.menu_add_items -> onAddItemsClicked()
            R.id.menu_sign_out -> {
                AuthUI.getInstance().signOut(this)
                FirebaseFirestore.getInstance().let {
                    it.terminate()
                    it.clearPersistence()
                }

                addStoryFragment(true)
            }
            R.id.menu_sign_in -> {
                startSignIn()
            }

            R.id.menu_settings -> {
                startActivity(Intent(this, SettingActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            mViewModel.isSigningIn = false
            invalidateOptionsMenu()

            if (resultCode != Activity.RESULT_OK && isSignIn()) {
                startSignIn()
            } else {
                addStoryFragment(true)
                moveStoriesFromLocal2Cloud()
            }
        }
    }

    private fun moveStoriesFromLocal2Cloud() {
        //TODO to be impl
    }

    private fun isSignIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
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
