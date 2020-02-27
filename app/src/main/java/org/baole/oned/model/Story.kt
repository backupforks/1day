package org.baole.oned.model

class Story() {
    var timestamp: Long = 0
    var day: String = ""
    var content: String = ""

    constructor(day: String, content: String) : this() {
        this.day = day
        this.content = content
        this.timestamp = System.currentTimeMillis()
    }

    companion object {
        const val PATH = "stories"
        const val FIELD_DAY = "day"
        const val FIELD_TIMESTAMP = "timestamp"
        const val FIELD_CONTENT = "content"
    }

    override fun toString(): String {
        return """{"day": "$day", "content": "$content", "timestamp": $timestamp}"""
    }
}