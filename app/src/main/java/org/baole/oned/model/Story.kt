package org.baole.oned.model

class Story {
    var timestamp: Long = 0
    var day: String = ""
    var content: String = ""

    companion object {
        const val PATH = "stories"
        const val FIELD_DAY = "day"
        const val FIELD_TIMESTAMP = "timestamp"
        const val FIELD_CONTENT = "content"
    }
}