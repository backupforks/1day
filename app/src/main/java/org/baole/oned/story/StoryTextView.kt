package org.baole.oned.story

import android.annotation.SuppressLint
import android.content.Context
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import org.baole.oned.OnedApp

class StoryTextView : AppCompatTextView {
    companion object {
        val TAG = StoryTextView::class.java.simpleName
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    fun setStory(storyData: StoryAdapterItem) {
        OnedApp.sApp.mMarkwon.setMarkdown(this, storyData.mStory.content.trim())
    }


    fun setStoryText(markdown: String) {
        OnedApp.sApp.mMarkwon.setMarkdown(this, markdown.trim())
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return false
    }
}