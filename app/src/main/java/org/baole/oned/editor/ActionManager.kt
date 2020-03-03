package org.baole.oned.editor

import org.baole.oned.R

class ActionManager {

    companion object {
        fun buildActionBar(): List<Action> {
            val actions = mutableListOf<Action>()
            actions.add(Action(1, type =  Action.TYPE_ICON, iconId = R.drawable.ic_keyboard_black_24dp))
            actions.add(MarkdownAction(100, pattern = "****", moveForwardCursor = 2, label = "B", subLabel = "**|**"))
            actions.add(MarkdownAction(101, pattern = "**", moveForwardCursor = 1, label = "I", subLabel = "*|*"))
            actions.add(MarkdownAction(101, pattern = "# ", moveForwardCursor = 2, label = "T", subLabel = "#"))
            return actions
        }

        fun buildKeyboardTool(): List<Action> {
            val actions = mutableListOf<Action>()
            actions.add(MarkdownAction(100, pattern = "****", moveForwardCursor = 2, label = "B", subLabel = "**|**"))
            actions.add(MarkdownAction(101, pattern = "**", moveForwardCursor = 1, label = "I", subLabel = "*|*"))
            actions.add(MarkdownAction(101, pattern = "# ", moveForwardCursor = 2, label = "T", subLabel = "#"))
            return actions
        }
    }
}