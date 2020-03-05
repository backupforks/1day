package org.baole.oned.editor

import android.content.Context
import android.view.inputmethod.InputMethodManager

class ShortcutAction(id: Int, val shortcutText: String, private val moveForwardCursor: Int = shortcutText.length)
    : Action(id, shortcutText) {
    fun onAction(editor: StoryEditText) {
        editor.insertText(shortcutText, moveForwardCursor)
        val imm = editor.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editor, 0)
    }
}