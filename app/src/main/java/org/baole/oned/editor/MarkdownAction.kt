package org.baole.oned.editor

class MarkdownAction(id: Int, private val pattern: String, private val moveForwardCursor: Int = 0, type: Int = TYPE_TEXt, label: String? = null, subLabel: String? = null, iconId: Int = 0) : Action(id, type, label, subLabel, iconId) {
    fun onAction(editor: StoryEditText) {
        editor.insertText(pattern, moveForwardCursor)
    }
}