package org.baole.oned.editor

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class EditorActionView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var listener: ((StoryEditText, Action) -> Unit)? = null
    private lateinit var action: Action
    private lateinit var editor: StoryEditText

    init {
        setOnClickListener {
            if(::action.isInitialized) {
                listener?.invoke(editor, action)
            }
        }
    }

    fun setActionListener(listener: (StoryEditText, Action) -> Unit) {
        this.listener = listener
    }

    open fun bind(editor: StoryEditText, action: Action) {
        this.action = action
        this.editor = editor
        when (action.type) {
            Action.TYPE_ICON -> {
                bindIcon()
            }

            Action.TYPE_TEXT -> {
                bindText()
            }

            Action.TYPE_ICON_AND_TEXT -> {
                bindIcon()
                bindText()
            }
        }
    }
    private fun bindText() {
        text = "${action.label} ${action.subLabel}"
    }
    private fun bindIcon() {
        setCompoundDrawablesWithIntrinsicBounds(action.iconId, 0, 0,0)
    }
}