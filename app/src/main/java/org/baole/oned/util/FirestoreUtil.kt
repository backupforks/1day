package org.baole.oned.util

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import org.baole.oned.model.Book
import org.baole.oned.model.Story

object FirestoreUtil {
    const val DEF_UID = "default"

    fun book(fs: FirebaseFirestore, user: FirebaseUser?): DocumentReference {
        return fs.collection(user?.uid ?: DEF_UID).document(Book.PATH)
    }

    fun story(fs: FirebaseFirestore, user: FirebaseUser?): CollectionReference {
        return book(fs, user).collection(Story.PATH)
    }

    fun day(fs: FirebaseFirestore, user: FirebaseUser?, day: String): DocumentReference {
        return story(fs, user).document(day)
    }

}