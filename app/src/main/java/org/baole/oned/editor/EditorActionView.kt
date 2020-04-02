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

    private var mListener: ((StoryEditText, Action) -> Unit)? = null
    private lateinit var mAction: Action
    private lateinit var mEditor: StoryEditText

    init {
        setOnClickListener {
            if (::mAction.isInitialized) {
                mListener?.invoke(mEditor, mAction)
            }
        }
    }

    fun setActionListener(listener: (StoryEditText, Action) -> Unit) {
        this.mListener = listener
    }

    open fun bind(editor: StoryEditText, action: Action) {
        this.mAction = action
        this.mEditor = editor
        when (action.mType) {
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
        val spanText = SpannableString(listOf(mAction.mLabel ?: "", mAction.mSubLabel
                ?: "").joinToString(""))
        var labelLength = 0

        mAction.mLabel?.let {
            labelLength = it.length
            spanText.setSpan(ForegroundColorSpan(Color.parseColor("#aaaaaa")), 0, labelLength, 0)
            spanText.setSpan(RelativeSizeSpan(1.5f), 0, labelLength, 0)
        }

        mAction.mSubLabel?.let {
            spanText.setSpan(SubscriptSpan(), labelLength, labelLength + it.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        text = spanText
    }

    private fun bindIcon() {
        setCompoundDrawablesWithIntrinsicBounds(mAction.mIconId, 0, 0, 0)
    }
}