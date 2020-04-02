package org.baole.oned.editor

import android.content.Context
import android.view.inputmethod.InputMethodManager

class MarkdownAction(mEditor: StoryEditorActivity, mId: Int, private val mPattern: String, private val mExplain: String?= null, private val mCondition: Int = CONDITION_NOTHING,
                     private val mMoveForwardCursor: Int = 0, mLabel: String? = null, mSubLabel: String? = null, mIconId: Int = 0, mType: Int = TYPE_TEXT) :
        Action(mEditor, mId, mLabel, mSubLabel, mIconId, mType) {
    override fun onAction(editor: StoryEditorActivity) {
        when(mCondition) {
            CONDITION_LINE_BREAK -> editor.mBinding.editor.startNewLineIfNeeded()
            else -> {}
        }

        editor.mBinding.editor.insertText(mPattern, mMoveForwardCursor)
        val imm = editor.mBinding.editor.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editor.mBinding.editor, 0)
    }

    companion object {
        const val CONDITION_NOTHING = 0
        const val CONDITION_LINE_BREAK = 1

    }
}