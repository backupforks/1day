package org.baole.oned.story.monthlist

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.text.SimpleDateFormat
import java.util.*

class MonthListAdapter(fragment: MonthListFragment) : FragmentStateAdapter(fragment) {
    companion object {
        const val PAGES = 50
    }
    private val mTitleFormatter = SimpleDateFormat("MMM dd")
    private val mTitleFullFormatter = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM)

    private val mYear: Int = Calendar.getInstance().get(Calendar.YEAR)

    override fun getItemCount(): Int {
        return PAGES
    }

    fun getDate(position: Int): Calendar {

        val date = Calendar.getInstance()
        date.add(Calendar.MONTH, -(PAGES - position -1))
        return date
    }

    override fun createFragment(position: Int): Fragment {
        val f = StoryListFragment()
        f.arguments = bundleOf(MonthListFragment.DAY to getDate(position).timeInMillis,
                MonthListFragment.IS_TODAY to (position == PAGES - 1))
        return f
    }

    fun getTitle(position: Int): String {
        val date = getDate(position)
        return if (date.get(Calendar.YEAR) == mYear) mTitleFormatter.format(date.time) else mTitleFullFormatter.format(date.time)
    }
}