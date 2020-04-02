package org.baole.oned.story.pager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.baole.oned.R
import org.baole.oned.databinding.StoryPagerItemBinding
import org.baole.oned.story.*
import org.baole.oned.util.DateUtil
import java.text.SimpleDateFormat

/**
 * RecyclerView adapter for a list of story.
 */
class StoryPagerAdapter(private val mFragment: StoryFragment, private val mViewModel: StoryViewModel) :
        StoryAdapter<StoryViewHolder>(mFragment.requireContext()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == StoryAdapterData.ITEM_TYPE_HEADER) {
            StoryHeaderViewHolder(inflater.inflate(R.layout.story_pager_header, parent, false), mFragment)
        } else {
            StoryItemViewHolder(inflater.inflate(R.layout.story_pager_item, parent, false), mFragment)
        }
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(getStory(position))
        if (position == itemCount - 1) {
            mViewModel.loadNext()
        }
    }

}

open class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    open fun bind(story: StoryAdapterData) {}

}

class StoryHeaderViewHolder(itemView: View, private val fragment: StoryFragment) : StoryViewHolder(itemView) {
    init {

        itemView.setOnClickListener {
            fragment.editStory()
        }
    }
}

class StoryItemViewHolder(itemView: View, private val mFragment: StoryFragment) : StoryViewHolder(itemView) {
    var mBinding = StoryPagerItemBinding.bind(itemView)

    init {
        itemView.setOnClickListener {
            itemView.tag?.let {
                mFragment.editStory((it as StoryAdapterItem).mDocumentId)
            }
        }
    }

    override fun bind(story: StoryAdapterData) {
        (story as StoryAdapterItem).let {
            val day = DateUtil.key2date(it.mStory.day)
            mBinding.day.text = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM).format(day)
            mBinding.content.setStory(story)
            itemView.tag = it
        }
    }
}
