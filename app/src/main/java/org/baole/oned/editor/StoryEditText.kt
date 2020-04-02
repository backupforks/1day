package org.baole.oned.editor

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import org.baole.oned.OnedApp

class StoryEditText: AppCompatEditText {
    var mUndoRedoHelper: UndoRedoHelper = UndoRedoHelper(this)
    private val mOnEditorChangedListeners = mutableListOf<(StoryEditText) -> Unit>()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {

        addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mOnEditorChangedListeners.forEach {
                    it.invoke(this@StoryEditText)
                }
            }

        })
    }

    fun addOnEditorChangedListener(listener: (StoryEditText) -> Unit) {
        mOnEditorChangedListeners.add(listener)
    }

    fun startNewLineIfNeeded() {
        selectionStart.coerceAtLeast(0).takeIf { it > 0 }?.let { index ->
            text?.get(index - 1)?.takeIf { it != '\n' }?.let {
                insertText("\n", 1)
            }
        }
    }

    fun insertText(input: String, moveForwardCursor: Int = 0) {
        val start = selectionStart.coerceAtLeast(0)
        val end = selectionEnd.coerceAtLeast(0)
        text?.let {
            it.replace(start.coerceAtMost(end), start.coerceAtLeast(end), input, 0, input.length)
            setSelection(start + moveForwardCursor)
        }
    }

    fun undo() {
        if (mUndoRedoHelper.canUndo) {
            mUndoRedoHelper.undo()
        }
    }

    fun redo() {
        if (mUndoRedoHelper.canRedo) {
            mUndoRedoHelper.redo()
        }
    }

    fun canUndo() = mUndoRedoHelper.canUndo
    fun canRedo() = mUndoRedoHelper.canRedo
}