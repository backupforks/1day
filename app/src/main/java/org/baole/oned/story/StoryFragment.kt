package org.baole.oned.story

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import org.baole.oned.MainActivity
import org.baole.oned.editor.StoryEditorActivity
import org.baole.oned.model.Story
import org.baole.oned.util.FirestoreUtil

open class StoryFragment : Fragment() {
    lateinit var mViewModel: StoryViewModel
    var mFirebaseUser: FirebaseUser? = null
    lateinit var mFirestore: FirebaseFirestore
    lateinit var mQuery: Query

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
        mViewModel = ViewModelProvider(this).get(StoryViewModel::class.java)
        setupFirestore()
    }
    fun setupFirestore() {
        mFirestore = FirebaseFirestore.getInstance()
        mFirebaseUser = FirebaseAuth.getInstance().currentUser
        setupQuery()
    }

    open fun setupQuery() {
        mQuery = FirestoreUtil.story(mFirestore, mFirebaseUser)
                .orderBy(Story.FIELD_DAY, Query.Direction.DESCENDING)
    }

    fun main(): MainActivity? {
        val parent = activity
        if (parent is MainActivity) return parent
        else return null
    }

    fun editStory(storyId: String = "") {
        startActivity(Intent(activity, StoryEditorActivity::class.java).putExtra(FirestoreUtil.FIELD_ID, storyId))
    }

    companion object {
        val TAG = StoryFragment::class.java.simpleName
    }
}