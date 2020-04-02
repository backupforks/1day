package org.baole.oned.editor

import org.baole.oned.R
import org.baole.oned.editor.MarkdownAction.*

class ActionManager {

    companion object {
        fun buildActionBar(editor: StoryEditorActivity): List<Action> {
            val actions = mutableListOf<Action>()
            actions.add(EditorAction(editor, 1, mType =  Action.TYPE_ICON, mIconId = R.drawable.ic_keyboard_black_24dp))
            actions.add(EditorAction(editor, 2, mType =  Action.TYPE_ICON, mIconId = R.drawable.ic_undo_black_24dp))
            actions.add(EditorAction(editor, 3, mType =  Action.TYPE_ICON, mIconId = R.drawable.ic_redo_black_24dp))
            actions.add(MarkdownAction(editor, 100, mPattern = "****", mMoveForwardCursor = 2, mLabel = "B", mSubLabel = "**|**"))
            actions.add(MarkdownAction(editor, 101, mPattern = "**", mMoveForwardCursor = 1, mLabel = "I", mSubLabel = "*|*"))
            actions.add(MarkdownAction(editor, 101, mPattern = "# ", mMoveForwardCursor = 2, mLabel = "T", mSubLabel = "#"))
            return actions
        }

        fun buildKeyboardTool(editor: StoryEditorActivity): List<Action> {
            val actions = mutableListOf<Action>()
            actions.add(MarkdownAction(editor, 100, "****", "Bold text", mMoveForwardCursor = 2, mLabel = "B", mSubLabel = "**|**"))
            actions.add(MarkdownAction(editor, 101, "**", "Italic text", mMoveForwardCursor = 1, mLabel = "I", mSubLabel = "*|*"))
            actions.add(MarkdownAction(editor, 101, "\" ", "Quote", MarkdownAction.CONDITION_LINE_BREAK, mMoveForwardCursor = 2, mLabel = "\"", mSubLabel = ">"))
            actions.add(MarkdownAction(editor, 101, "- ", "Unordered list", MarkdownAction.CONDITION_LINE_BREAK, mMoveForwardCursor = 2, mLabel = "\"", mSubLabel = ">"))
            actions.add(MarkdownAction(editor, 101, "* ", "Ordered list", MarkdownAction.CONDITION_LINE_BREAK, mMoveForwardCursor = 2, mLabel = "\"", mSubLabel = ">"))
            actions.add(MarkdownAction(editor, 101, "# ", "Heading 1", MarkdownAction.CONDITION_LINE_BREAK, mMoveForwardCursor = 2, mLabel = "T", mSubLabel = "#"))
            actions.add(MarkdownAction(editor, 101, "## ", "Heading 2", MarkdownAction.CONDITION_LINE_BREAK, mMoveForwardCursor = 3, mLabel = "T", mSubLabel = "##"))
            actions.add(MarkdownAction(editor, 101, "### ", "Heading 3", MarkdownAction.CONDITION_LINE_BREAK, mMoveForwardCursor = 4, mLabel = "T", mSubLabel = "###"))
            actions.add(MarkdownAction(editor, 101, "``", "``", mMoveForwardCursor = 1, mLabel = "<>", mSubLabel = "`|`"))
            actions.add(MarkdownAction(editor, 101, "``````", "``````", mMoveForwardCursor = 3, mLabel = "{}", mSubLabel = "```|```"))
            actions.add(MarkdownAction(editor, 101, "[]()", "Link", mMoveForwardCursor = 1, mLabel = "", mSubLabel = "[|]()"))
            actions.add(MarkdownAction(editor, 101, "~~~~", "Strikethought", mMoveForwardCursor = 2, mLabel = "S", mSubLabel = "~~"))

            actions.add(ShortcutAction(editor, 101, "<"))
            actions.add(ShortcutAction(editor, 101, ">"))
            actions.add(ShortcutAction(editor, 101, "+"))
            actions.add(ShortcutAction(editor, 101, "-"))
            actions.add(ShortcutAction(editor, 101, "_"))
            actions.add(ShortcutAction(editor, 101, "@"))
            actions.add(ShortcutAction(editor, 101, "."))
            actions.add(ShortcutAction(editor, 101, "/"))
            actions.add(ShortcutAction(editor, 101, "\\"))
            actions.add(ShortcutAction(editor, 101, "="))
            actions.add(ShortcutAction(editor, 101, "\"\"", 1))
            actions.add(ShortcutAction(editor, 101, "''", 1))
            actions.add(ShortcutAction(editor, 101, "()", 1))
            actions.add(ShortcutAction(editor, 101, "{}", 1))
            actions.add(ShortcutAction(editor, 101, "[]", 1))
            actions.add(ShortcutAction(editor, 101, ";"))
            actions.add(ShortcutAction(editor, 101, ":"))
            actions.add(ShortcutAction(editor, 101, ","))
            actions.add(ShortcutAction(editor, 101, "?"))
            actions.add(ShortcutAction(editor, 101, "!"))

            return actions
        }
    }
}