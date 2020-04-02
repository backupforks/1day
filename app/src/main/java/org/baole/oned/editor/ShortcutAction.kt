package org.baole.oned.editor

import android.content.Context
import android.view.inputmethod.InputMethodManager

class ShortcutAction(mEditor: StoryEditorActivity, mId: Int, val mShortcutText: String, private val mMoveForwardCursor: Int = mShortcutText.length)
    : Action(mEditor, mId, mShortcutText) {
    override fun onAction(editor: StoryEditorActivity) {
        editor.mBinding.editor.insertText(mShortcutText, mMoveForwardCursor)
        val imm = editor.mBinding.editor.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editor.mBinding.editor, 0)
    }
}