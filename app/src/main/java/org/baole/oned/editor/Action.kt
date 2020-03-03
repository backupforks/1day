package org.baole.oned.editor

open class Action(val id: Int, val type: Int = TYPE_TEXt, val label: String?=null, val subLabel: String? = null, val iconId: Int = 0) {
    companion object {
        const val TYPE_TEXt = 0
        const val TYPE_ICON = 1
        const val TYPE_ICON_AND_TEXT = 2
    }

}