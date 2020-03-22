package org.baole.oned.editor

import org.baole.oned.R
import org.baole.oned.editor.MarkdownAction.*

class ActionManager {

    companion object {
        fun buildActionBar(editor: StoryEditorActivity): List<Action> {
            val actions = mutableListOf<Action>()
            actions.add(EditorAction(editor, 1, type =  Action.TYPE_ICON, iconId = R.drawable.ic_keyboard_black_24dp))
            actions.add(EditorAction(editor, 2, type =  Action.TYPE_ICON, iconId = R.drawable.ic_undo_black_24dp))
            actions.add(EditorAction(editor, 3, type =  Action.TYPE_ICON, iconId = R.drawable.ic_redo_black_24dp))
            actions.add(MarkdownAction(editor, 100, pattern = "****", moveForwardCursor = 2, label = "B", subLabel = "**|**"))
            actions.add(MarkdownAction(editor, 101, pattern = "**", moveForwardCursor = 1, label = "I", subLabel = "*|*"))
            actions.add(MarkdownAction(editor, 101, pattern = "# ", moveForwardCursor = 2, label = "T", subLabel = "#"))
            return actions
        }

        fun buildKeyboardTool(editor: StoryEditorActivity): List<Action> {
            val actions = mutableListOf<Action>()
            actions.add(MarkdownAction(editor, 100, "****", "Bold text", MarkdownAction.CONDITION_SPACE, moveForwardCursor = 2, label = "B", subLabel = "**|**"))
            actions.add(MarkdownAction(editor, 101, "**", "Italic text", MarkdownAction.CONDITION_SPACE, moveForwardCursor = 1, label = "I", subLabel = "*|*"))
            actions.add(MarkdownAction(editor, 101, "\" ", "Quote", MarkdownAction.CONDITION_LINE_BREAK, moveForwardCursor = 2, label = "\"", subLabel = ">"))
            actions.add(MarkdownAction(editor, 101, "- ", "Unordered list", MarkdownAction.CONDITION_LINE_BREAK, moveForwardCursor = 2, label = "\"", subLabel = ">"))
            actions.add(MarkdownAction(editor, 101, "* ", "Ordered list", MarkdownAction.CONDITION_LINE_BREAK, moveForwardCursor = 2, label = "\"", subLabel = ">"))
            actions.add(MarkdownAction(editor, 101, "# ", "Heading 1", MarkdownAction.CONDITION_LINE_BREAK, moveForwardCursor = 2, label = "T", subLabel = "#"))
            actions.add(MarkdownAction(editor, 101, "## ", "Heading 2", MarkdownAction.CONDITION_LINE_BREAK, moveForwardCursor = 3, label = "T", subLabel = "##"))
            actions.add(MarkdownAction(editor, 101, "### ", "Heading 3", MarkdownAction.CONDITION_LINE_BREAK, moveForwardCursor = 4, label = "T", subLabel = "###"))
            actions.add(MarkdownAction(editor, 101, "``", "``", MarkdownAction.CONDITION_NOTHING, moveForwardCursor = 1, label = "<>", subLabel = "`|`"))
            actions.add(MarkdownAction(editor, 101, "``````", "``````", MarkdownAction.CONDITION_NOTHING, moveForwardCursor = 3, label = "{}", subLabel = "```|```"))
            actions.add(MarkdownAction(editor, 101, "[]()", "Link", MarkdownAction.CONDITION_NOTHING, moveForwardCursor = 1, label = "", subLabel = "[|]()"))
            actions.add(MarkdownAction(editor, 101, "~~~~", "Strikethought", MarkdownAction.CONDITION_NOTHING, moveForwardCursor = 2, label = "S", subLabel = "~~"))

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