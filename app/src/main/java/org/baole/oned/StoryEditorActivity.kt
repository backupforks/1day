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
    lateinit var mStory: Story
    var mStoryDocumentSnapshot: DocumentSnapshot? = null
    var mFirebaesUser: FirebaseUser? = null
    var mNewDay: String? = null
    lateinit var mStoryId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = StoryEditorActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setSupportActionBar(mBinding.toolbar)

        mBinding.toolbar.setOnClickListener {
            val newFragment = DatePickerFragment()
            newFragment.arguments = bundleOf(Story.FIELD_DAY to mStory.day)
            newFragment.onKeySelected = {
                onDaySelected(it)
            }
            newFragment.show(supportFragmentManager, "datePicker")
        }

        mFirestore = FirebaseFirestore.getInstance()
        mFirebaesUser = FirebaseAuth.getInstance().currentUser
        mBinding.save.setOnClickListener {
            mStoryDocumentSnapshot?.let {
                updateStory(it, mBinding.editor.text.toString())
            } ?: kotlin.run {
                createStory(mBinding.editor.text.toString())
            }
        }

        queryStory()
    }

    private fun queryStory() {
        mStoryId = intent.getStringExtra(FirestoreUtil.FIELD_ID) ?: throw RuntimeException("No ${FirestoreUtil.FIELD_ID} found")
        Log.w(TAG, "storyId: $mStoryId")
        if (mStoryId.isEmpty()) {
            onDaySelected(DateUtil.day2key())
        } else {
            FirestoreUtil.day(mFirestore, mFirebaesUser, mStoryId).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                onStorySnapshotUpdated(documentSnapshot)
            }
        }
    }

    private fun onDaySelected(day: String) {
        mNewDay = day
        FirestoreUtil.dayQuery(mFirestore, mFirebaesUser, day).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            onStorySnapshotUpdated(querySnapshot?.documents?.getOrNull(0))
        }
    }

    private fun onStorySnapshotUpdated(documentSnapshot: DocumentSnapshot?) {
        mStoryDocumentSnapshot = documentSnapshot
        mStoryDocumentSnapshot?.toObject(Story::class.java)?.let {
            setStory(it)
            mStory = it
            mBinding.editor.setSelection(mBinding.editor.length())
        } ?: kotlin.run {
            setStory(Story(mNewDay ?: DateUtil.day2key(), ""))
            mBinding.editor.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(mBinding.editor, 0)
        }
    }

    private fun setStory(story: Story) {
        mStory = story
        title = DateUtil.key2display(story.day)
        mBinding.editor.setText(story.content)
    }

    private fun createStory(text: String) {
        val story = Story(mStory.day, text)
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
        if (mNewDay != mStory.day) {
            createStory(newText)
        } else if (newText != mStory.content) {
            Log.d(TAG, "firestore: update =${snapshot.id} -> ${mNewDay}/${newText}")
            val task = FirestoreUtil.day(mFirestore, mFirebaesUser, snapshot.id).update(mapOf(Story.FIELD_CONTENT to newText,
                    Story.FIELD_DAY to mNewDay
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
