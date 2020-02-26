package org.baole.oned

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import org.baole.oned.databinding.StoryEditorActivityBinding
import org.baole.oned.model.Story
import org.baole.oned.util.DateUtil
import org.baole.oned.util.FirestoreUtil


class StoryEditorActivity : AppCompatActivity() {
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var mBinding: StoryEditorActivityBinding
    lateinit var mDay: String
    var mStoryDocumentSnapshot: DocumentSnapshot? = null
    var mStory: Story? = null
    var mFirebaesUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = StoryEditorActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setSupportActionBar(mBinding.toolbar)
        mBinding.toolbar.setOnClickListener {
            val newFragment = DatePickerFragment()
            newFragment.arguments = bundleOf(Story.FIELD_DAY to mDay)
            newFragment.onKeySelected = {
                mDay = it
                onDayUpdated()
            }
            newFragment.show(supportFragmentManager, "datePicker")

        }

        mDay = intent.getStringExtra(Story.FIELD_DAY) ?: DateUtil.day2key()
        onDayUpdated()

        mFirestore = FirebaseFirestore.getInstance()
        Log.d(TAG, "firestore: isPersistenceEnabled=${mFirestore.firestoreSettings.isPersistenceEnabled}")
        Log.d(TAG, "firestore: $mFirestore")

        mFirebaesUser = FirebaseAuth.getInstance().currentUser

        FirestoreUtil.dayQuery(mFirestore, mFirebaesUser, mDay).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            mStoryDocumentSnapshot = querySnapshot?.documents?.getOrNull(0)

            mStoryDocumentSnapshot?.toObject(Story::class.java)?.let {
                mBinding.editor.setText(it.content)
                mBinding.editor.setSelection(mBinding.editor.length())
                mStory = it
            } ?: kotlin.run {
                mBinding.editor.requestFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(mBinding.editor, 0)
            }
        }

        mBinding.save.setOnClickListener {
            mStoryDocumentSnapshot?.let {
                updateStory(it, mBinding.editor.text.toString())
            } ?: kotlin.run {
                createStory(mBinding.editor.text.toString())
            }
        }
    }

    private fun onDayUpdated() {
        title = DateUtil.key2display(mDay)
    }

    private fun createStory(text: String) {
        val story = Story()
        story.day = mDay
        story.timestamp = System.currentTimeMillis()
        story.content = text

        val task = FirestoreUtil.story(mFirestore, mFirebaesUser).document().set(story)
        if (isSignedIn()) {
            finish()
        } else {
            task.addOnCompleteListener {
                finish()
            }.addOnFailureListener { it.printStackTrace() }
        }
    }

    private fun updateStory(snapshot: DocumentSnapshot, newText: String) {
        if (mDay != mStory?.day) {
            createStory(newText)
        } else if (newText != mStory?.content) {
            Log.d(TAG, "firestore: update =${snapshot.id} -> ${mDay}/${newText}")
            val task = FirestoreUtil.day(mFirestore, mFirebaesUser, snapshot.id).update(mapOf(Story.FIELD_CONTENT to newText,
                    Story.FIELD_DAY to mDay
            ))
            if (isSignedIn()) {
                finish()
            } else {
                task.addOnSuccessListener {
                    finish()
                }.addOnFailureListener {
                    Log.d(TAG, "firestore: addOnCompleteListener")
                    it.printStackTrace()
                }
            }
        } else {
            finish()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_editor, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_clear -> {
                mBinding.editor.setText("")
            }
            R.id.menu_delete -> {
                deleteStory()
            }

            R.id.menu_settings -> {
                startActivity(Intent(this, SettingActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteStory() {
        mStoryDocumentSnapshot?.let {
            val task = FirestoreUtil.day(mFirestore, mFirebaesUser, it.id).delete()

            if (isSignedIn()) {
                finish()
            } else {
                task.addOnSuccessListener {
                    Log.d(TAG, "firestore: deleteStory success")
                    finish()
                }.addOnFailureListener {
                    Log.d(TAG, "firestore: deleteStory failure")
                    it.printStackTrace()
                }.addOnCompleteListener {
                    Log.d(TAG, "firestore: deleteStory complete")
                }
            }
        }
    }

    private fun isSignedIn(): Boolean = mFirebaesUser == null

    companion object {

        private val TAG = "StoryEditorActivity"
    }
}
