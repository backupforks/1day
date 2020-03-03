package org.baole.oned.editor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import io.noties.markwon.editor.MarkwonEditor
import io.noties.markwon.editor.MarkwonEditorTextWatcher
import org.baole.oned.DatePickerFragment
import org.baole.oned.OnedApp
import org.baole.oned.R
import org.baole.oned.SettingActivity
import org.baole.oned.databinding.StoryEditorActivityBinding
import org.baole.oned.model.Story
import org.baole.oned.story.StoryAdapterItem
import org.baole.oned.util.AppState
import org.baole.oned.util.DateUtil
import org.baole.oned.util.FirestoreUtil
import org.baole.oned.util.StoryEditorEvent


class StoryEditorActivity : AppCompatActivity() {
    private var mKeyboardTopHeight: Int = 0
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var mBinding: StoryEditorActivityBinding
    lateinit var mStory: Story
    var mStoryDocumentSnapshot: DocumentSnapshot? = null
    var mFirebaesUser: FirebaseUser? = null
    var mNewDay: String? = null
    lateinit var mStoryId: String
    var mIsKeyboardOpen = false
    var mIsKeyboardManualTrigger = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = StoryEditorActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setupToolbar()
        setupFirestore()
        setupSaveButton()
        setupEditor()
    }

    private fun setupEditor() {
        val editor = MarkwonEditor.create(OnedApp.sApp.mMarkwon)
        mBinding.editor.addTextChangedListener(MarkwonEditorTextWatcher.withProcess(editor))
        mKeyboardTopHeight = resources.getDimensionPixelSize(R.dimen.keyboard_top_height)
        KeyboardHeightProvider(this, windowManager, mBinding.root) { keyboardHeight, keyboardOpen, isLandscape ->
            Log.i(TAG, "keyboardHeight: $keyboardHeight keyboardOpen: $keyboardOpen isLandscape: $isLandscape $mIsKeyboardManualTrigger")
            if (keyboardOpen) {
                setViewHeight(mBinding.keyboardRoot, keyboardHeight + mKeyboardTopHeight)
                mBinding.keyboardRoot.visibility = View.VISIBLE
                mBinding.keyboardView.visibility = View.VISIBLE
            } else {
                if (mIsKeyboardManualTrigger) {
                    setViewHeight(mBinding.keyboardRoot, mKeyboardTopHeight)
                    mBinding.keyboardView.visibility = View.GONE
                } else {
                    mBinding.keyboardRoot.visibility = View.GONE
                }
            }
            mIsKeyboardManualTrigger = false
            mIsKeyboardOpen = keyboardOpen
        }

        mBinding.editorbar.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)


        mBinding.keyboardView.layoutManager = GridLayoutManager(this, 4)


//        mBinding.actionKeyboard.setOnClickListener {
//            if (mIsKeyboardOpen) {
//                hideSoftKeyboard()
//            } else {
//                showSoftKeyboard()
//            }
//        }
//        mBinding.actionBold.setOnClickListener {
//            mBinding.editor.insertText("****", 2)
//        }
    }

    private fun setViewHeight(keyboardRoot: ViewGroup, newHeight: Int) {
        val params = mBinding.keyboardRoot.layoutParams
        params.height = newHeight
        mBinding.keyboardRoot.layoutParams = params
    }

    private fun setupSaveButton() {
    }

    override fun onPause() {
        super.onPause()
        mBinding.editor.text.toString().takeIf { it.trim().isNotEmpty() }?.let { story ->
            mStoryDocumentSnapshot?.let {
                updateStory(it, story)
            } ?: kotlin.run {
                createStory(story)
            }
        }
    }

    private fun setupFirestore() {
        mFirestore = FirebaseFirestore.getInstance()
        mFirebaesUser = FirebaseAuth.getInstance().currentUser
        mStoryId = intent.getStringExtra(FirestoreUtil.FIELD_ID) ?: throw RuntimeException("No ${FirestoreUtil.FIELD_ID} found")
        if (mStoryId.isEmpty()) {
            onDaySelected(DateUtil.day2key())
        } else {
            FirestoreUtil.day(mFirestore, mFirebaesUser, mStoryId).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                onStorySnapshotUpdated(documentSnapshot)
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(mBinding.toolbar)

        mBinding.toolbar.setOnClickListener {
            val newFragment = DatePickerFragment()
            newFragment.arguments = bundleOf(Story.FIELD_DAY to mStory.day)
            newFragment.onKeySelected = {
                onDaySelected(it)
            }
            newFragment.show(supportFragmentManager, "datePicker")
        }
    }
    private fun onDaySelected(day: String) {
        mNewDay = day
        FirestoreUtil.dayQuery(mFirestore, mFirebaesUser, day).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            onStorySnapshotUpdated(querySnapshot?.documents?.getOrNull(0))
        }
    }

    private fun onStorySnapshotUpdated(documentSnapshot: DocumentSnapshot?) {
        if (isFinishing) return
        mStoryDocumentSnapshot = documentSnapshot
        mStoryDocumentSnapshot?.toObject(Story::class.java)?.let {
            setStory(it)
            mStory = it
            mBinding.editor.setSelection(mBinding.editor.length())
        } ?: kotlin.run {
            setStory(Story(mNewDay ?: DateUtil.day2key(), ""))
            showSoftKeyboard()
        }
    }

    private fun showSoftKeyboard() {
        mIsKeyboardManualTrigger = true
        Log.i(TAG, "showSoftKeyboard $mIsKeyboardManualTrigger")
        mBinding.editor.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(mBinding.editor, 0)
    }

    private fun hideSoftKeyboard() {
        mIsKeyboardManualTrigger = true
        Log.i(TAG, "hideSoftKeyboard $mIsKeyboardManualTrigger")
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(mBinding.editor.windowToken, 0)
    }

    private fun setStory(story: Story) {
        mStory = story
        title = DateUtil.key2display(story.day)
        mBinding.editor.setText(story.content)
    }

    private fun createStory(text: String) {
        val story = Story(mStory.day, text)
        val documentReference = FirestoreUtil.story(mFirestore, mFirebaesUser).document()
        val task = documentReference.set(story)
        mStory = story
        documentReference.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            mStoryDocumentSnapshot = documentSnapshot
        }
//        if (isSignedIn()) {
//            finish()
//        } else {
//            task.addOnCompleteListener {
//                finish()
//            }.addOnFailureListener { it.printStackTrace() }
//        }
        AppState.get(this).markLastStoryTimestamp(DateUtil.key2date(mStory.day).time)
        OnedApp.sApp.mStoryObservable.setData(StoryEditorEvent(StoryEditorEvent.TYPE_ADDED, StoryAdapterItem(documentReference.id, story)))
    }

    private fun updateStory(snapshot: DocumentSnapshot, newText: String) {
        Log.d(TAG, "firestore: update =${snapshot.id}  -> ${mStory.day} ${mNewDay}/${newText}")
        if (mNewDay != null && mNewDay != mStory.day) {
            createStory(newText)
        } else if (newText != mStory.content) {
            val task = FirestoreUtil.day(mFirestore, mFirebaesUser, snapshot.id).update(mapOf(Story.FIELD_CONTENT to newText
            ))
            mStory.content = newText
//            if (isSignedIn()) {
//                finish()
//            } else {
//                task.addOnSuccessListener {
//                    finish()
//                }.addOnFailureListener {
//                    Log.d(TAG, "firestore: addOnCompleteListener")
//                    it.printStackTrace()
//                }
//            }
            OnedApp.sApp.mStoryObservable.setData(StoryEditorEvent(StoryEditorEvent.TYPE_UPDATED, StoryAdapterItem(snapshot.id, Story(mStory.day, newText))))
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
            AppState.get(this).clearLastStoryTimestamp(DateUtil.key2date(mStory.day).time)
            OnedApp.sApp.mStoryObservable.setData(StoryEditorEvent(StoryEditorEvent.TYPE_DELETED, StoryAdapterItem(it.id, mStory)))

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

        private val TAG = StoryEditorActivity::class.java.simpleName
    }
}
