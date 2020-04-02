package org.baole.oned.editor

class EditorAction(mEditor: StoryEditorActivity, mId: Int, mLabel: String? = null, mSubLabel: String? = null, mIconId: Int = 0, mType: Int = TYPE_TEXT) :
        Action(mEditor, mId, mLabel, mSubLabel, mIconId, mType) {

    init {
        mEditor.mBinding.editor.addOnEditorChangedListener {
            onEditorChanged(it)
        }
    }

    private fun onEditorChanged(edit: StoryEditText) {
        when (mId) {
            1 -> {

            }

            2 -> {
            }

            3 -> {
            }
        }
    }

    override fun onAction(editor: StoryEditorActivity) {
        when (mId) {
            1 -> {
                if (editor.mIsKeyboardOpen) {
                    editor.hideSoftKeyboard()
                } else {
                    editor.showSoftKeyboard()
                }
            }

            2 -> {
                editor.mBinding.editor.undo()
            }

            3 -> {
                editor.mBinding.editor.redo()
            }
        }
    }

}