package org.baole.oned.editor

abstract class Action(val mEditor: StoryEditorActivity, val mId: Int, val mLabel: String?=null,
                      val mSubLabel: String? = null, val mIconId: Int = 0, val mType: Int = TYPE_TEXT) {
    companion object {
        const val TYPE_TEXT = 0
        const val TYPE_ICON = 1
        const val TYPE_ICON_AND_TEXT = 2
    }

    open fun onAction(editor: StoryEditorActivity) {

    }

}