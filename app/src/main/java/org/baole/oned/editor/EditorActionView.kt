package org.baole.oned.editor

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.SubscriptSpan
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
            if (::action.isInitialized) {
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
        val spanText = SpannableString(listOf(action.label ?: "", action.subLabel
                ?: "").joinToString(""))
        var labelLength = 0

        action.label?.let {
            labelLength = it.length
            spanText.setSpan(ForegroundColorSpan(Color.parseColor("#aaaaaa")), 0, labelLength, 0)
            spanText.setSpan(RelativeSizeSpan(1.5f), 0, labelLength, 0)
        }

        action.subLabel?.let {
            spanText.setSpan(SubscriptSpan(), labelLength, labelLength + it.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        text = spanText
    }

    private fun bindIcon() {
        setCompoundDrawablesWithIntrinsicBounds(action.iconId, 0, 0, 0)
    }
}