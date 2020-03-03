package org.baole.oned.editor

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.PopupWindow

class KeyboardHeightProvider(context: Context, windowManager: WindowManager, parentView: View, listener: (Int, Boolean, Boolean) -> Unit) : PopupWindow(context) {
    var mIsOpen = false
    init {
        val popupView = LinearLayout(context)
        popupView.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        popupView.viewTreeObserver.addOnGlobalLayoutListener {
            val metrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(metrics)
            val rect = Rect()
            popupView.getWindowVisibleDisplayFrame(rect)
            var keyboardHeight = metrics.heightPixels - (rect.bottom - rect.top)
            val resourceID = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceID > 0) {
                keyboardHeight -= context.resources.getDimensionPixelSize(resourceID)
            }
            if (keyboardHeight < 100) {
                keyboardHeight = 0
            }
            val keyboardOpen = keyboardHeight > 0
            if (keyboardOpen != mIsOpen) {
                mIsOpen = keyboardOpen

                val isLandscape = metrics.widthPixels > metrics.heightPixels
                listener.invoke(keyboardHeight, keyboardOpen, isLandscape)
            }

        }
        contentView = popupView
        softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
        inputMethodMode = INPUT_METHOD_NEEDED
        width = 1
        height = ViewGroup.LayoutParams.MATCH_PARENT
        setBackgroundDrawable(ColorDrawable(0))
        parentView.post { showAtLocation(parentView, Gravity.NO_GRAVITY, 0, 0) }
    }
}