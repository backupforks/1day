package org.baole.oned

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import org.baole.oned.databinding.StoryEditorActivityBinding
import org.baole.oned.model.Story
import org.baole.oned.util.DateUtil
import org.baole.oned.util.FirestoreUtil


class StoryEditorActivity : AppCompatActivity() {
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var binding: StoryEditorActivityBinding
    lateinit var day: String
    var storyDocumentSnapshot: DocumentSnapshot? = null
    var story: Story? = null
    var firebaesUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.story_editor_activity)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setOnClickListener {
            val newFragment = DatePickerFragment()
            newFragment.arguments = bundleOf(Story.FIELD_DAY to day)
            newFragment.onKeySelected = {
                day = it
                onDayUpdated()
            }
            newFragment.show(supportFragmentManager, "datePicker")

        }

        day = intent.getStringExtra(Story.FIELD_DAY) ?: DateUtil.day2key()
        onDayUpdated()

        mFirestore = FirebaseFirestore.getInstance()
        firebaesUser = FirebaseAuth.getInstance().currentUser

        FirestoreUtil.dayQuery(mFirestore, firebaesUser, day).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            storyDocumentSnapshot = querySnapshot?.documents?.getOrNull(0)
            storyDocumentSnapshot?.toObject(Story::class.java)?.let {
                binding.editor.setText(it.content)
                binding.editor.setSelection(binding.editor.length())
                story = it
            } ?: kotlin.run {
                binding.editor.requestFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.editor, InputMethodManager.SHOW_FORCED)
            }
        }

        binding.save.setOnClickListener {
            storyDocumentSnapshot?.let {
                updateStory(it, binding.editor.text.toString())
            } ?: kotlin.run {
                createStory(binding.editor.text.toString())
            }
        }
    }

    private fun onDayUpdated() {
        title = DateUtil.key2display(day)
    }

    private fun createStory(text: String) {
        val story = Story()
        story.day = day
        story.timestamp = System.currentTimeMillis()
        story.content = text

        FirestoreUtil.story(mFirestore, firebaesUser).document().set(story).addOnCompleteListener {
            finish()
        }.addOnFailureListener { it.printStackTrace() }
    }

    private fun updateStory(snapshot: DocumentSnapshot, newText: String) {
        if (newText != story?.content || day != story?.day) {
            FirestoreUtil.day(mFirestore, firebaesUser, snapshot.id).update(mapOf(Story.FIELD_CONTENT to newText,
                    Story.FIELD_DAY to day
                    )).addOnCompleteListener { finish() }
        } else {
            finish()
        }
    }

    companion object {

        private val TAG = "StoryEditorActivity"
    }
}
