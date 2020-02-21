package org.baole.oned.model

class Book {
    var authorId: String = ""
    var authorName: String = ""
    var email: String = ""
    var name: String = ""

    companion object {
        const val PATH = "book"
    }
}