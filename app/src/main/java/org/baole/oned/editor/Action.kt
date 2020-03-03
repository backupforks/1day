package org.baole.oned.editor

open class Action(val id: Int, val label: String?=null, val subLabel: String? = null, val iconId: Int = 0, val type: Int = TYPE_TEXT) {
    companion object {
        const val TYPE_TEXT = 0
        const val TYPE_ICON = 1
        const val TYPE_ICON_AND_TEXT = 2
    }

}