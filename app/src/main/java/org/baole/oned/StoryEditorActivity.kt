package org.baole.oned

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import org.baole.oned.databinding.StoryEditorActivityBinding
import org.baole.oned.model.Story
import org.baole.oned.util.DateUtil


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
        mFirestore = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser!!
        storyRef = mFirestore.collection(user.uid).document("book").collection("stories").document(day)
        storyRef.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            documentSnapshot?.toObject(Story::class.java)?.let {
                binding.editor.setText(it.content)
            }
        }

        binding.save.setOnClickListener {
            story?.let {
                it.content = binding.editor.text.toString()
                storyRef.update("content", binding.editor.text.toString()).addOnCompleteListener { finish() }
            } ?: kotlin.run {
                val story = Story()
                story.day = DateUtil.day2key()
                story.timestamp = System.currentTimeMillis()
                story.content = binding.editor.text.toString()

                storyRef.set(story).addOnCompleteListener {
                    finish()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.editor, InputMethodManager.SHOW_IMPLICIT)
    }

    companion object {

        private val TAG = "StoryEditorActivity"
    }
}
