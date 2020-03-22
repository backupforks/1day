package org.baole.oned.editor

class EditorAction(editor: StoryEditorActivity, id: Int, label: String? = null, subLabel: String? = null, iconId: Int = 0, type: Int = TYPE_TEXT) :
        Action(editor, id, label, subLabel, iconId, type) {

    init {
        editor.mBinding.editor.addOnEditorChangedListener {
            onEditorChanged(it)
        }

    }

    private fun onEditorChanged(edit: StoryEditText) {
        when (id) {
            1 -> {

            }

            2 -> {
            }

            3 -> {
            }
        }
    }

    override fun onAction(editor: StoryEditorActivity) {
        when (id) {
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