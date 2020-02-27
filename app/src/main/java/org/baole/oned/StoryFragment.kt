package org.baole.oned

import androidx.fragment.app.Fragment

open class StoryFragment : Fragment() {


    fun main(): MainActivity? {
        val parent = activity
        if (parent is MainActivity) return parent
        else return null
    }
}