package org.baole.oned.editor

import android.content.Context
import android.view.inputmethod.InputMethodManager

class MarkdownAction(id: Int, private val pattern: String, private val moveForwardCursor: Int = 0, label: String? = null, subLabel: String? = null, iconId: Int = 0, type: Int = TYPE_TEXT) : Action(id, label, subLabel, iconId, type) {
    fun onAction(editor: StoryEditText) {
        editor.insertText(pattern, moveForwardCursor)
        val imm = editor.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editor, 0)
    }
}