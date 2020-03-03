package org.baole.oned.editor

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class StoryEditText: AppCompatEditText {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun insertText(input: String, moveForwardCursor: Int = 0) {
        val start = selectionStart.coerceAtLeast(0)
        val end = selectionEnd.coerceAtLeast(0)
        text?.let {
            it.replace(start.coerceAtMost(end), start.coerceAtLeast(end), input, 0, input.length)
            setSelection(start + moveForwardCursor)
        }
    }
}