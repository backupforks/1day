package org.baole.oned

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import org.baole.oned.databinding.StoryEditorActivityBinding
import org.baole.oned.model.Story
import org.baole.oned.util.DateUtil
import org.baole.oned.util.FirestoreUtil


class StoryEditorActivity : AppCompatActivity() {
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var binding: StoryEditorActivityBinding
    var story: Story? = null
    lateinit var day: String
    lateinit var storyRef: DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.story_editor_activity)

        day = intent.getStringExtra(Story.FIELD_DAY) ?: DateUtil.day2key()
        binding.date.text = DateUtil.key2display(day)

        mFirestore = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        storyRef = FirestoreUtil.day(mFirestore, user, day)
        storyRef.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            documentSnapshot?.toObject(Story::class.java)?.let {
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
            story?.let {
                updateStory(binding.editor.text.toString())
            } ?: kotlin.run {
                createStory(binding.editor.text.toString())
            }
        }
    }

    private fun createStory(text: String) {
        val story = Story()
        story.day = day
        story.timestamp = System.currentTimeMillis()
        story.content = text

        storyRef.set(story).addOnCompleteListener {
            finish()
        }.addOnFailureListener { it.printStackTrace() }
    }

    private fun updateStory(newText: String) {
        if (newText != story?.content) {
            storyRef.update(Story.FIELD_CONTENT, newText).addOnCompleteListener { finish() }
        } else {
            finish()
        }
    }

    companion object {

        private val TAG = "StoryEditorActivity"
    }
}
