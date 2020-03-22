package org.baole.oned.editor

import android.content.Context
import android.view.inputmethod.InputMethodManager

class ShortcutAction(editor: StoryEditorActivity, id: Int, val shortcutText: String, private val moveForwardCursor: Int = shortcutText.length)
    : Action(editor, id, shortcutText) {
    override fun onAction(editor: StoryEditorActivity) {
        editor.mBinding.editor.insertText(shortcutText, moveForwardCursor)
        val imm = editor.mBinding.editor.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editor.mBinding.editor, 0)
    }
}