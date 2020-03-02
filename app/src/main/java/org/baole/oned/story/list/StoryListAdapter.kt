package org.baole.oned.story.list

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.baole.oned.OnedApp
import org.baole.oned.R
import org.baole.oned.databinding.StoryListItemBinding
import org.baole.oned.story.*
import org.baole.oned.util.DateUtil
import java.text.SimpleDateFormat

/**
 * RecyclerView adapter for a list of story.
 */
class StoryListAdapter(private val mFragment: StoryFragment, private val viewModel: StoryViewModel) :
        StoryAdapter<StoryViewHolder>(mFragment.requireContext()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == StoryAdapterData.ITEM_TYPE_HEADER) {
            StoryHeaderViewHolder(inflater.inflate(R.layout.story_list_header, parent, false), mFragment)
        } else {
            StoryItemViewHolder(inflater.inflate(R.layout.story_list_item, parent, false), mFragment)
        }
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(getStory(position))
        if (position == itemCount - 1) {
            viewModel.loadNext()
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
    var mBinding = StoryListItemBinding.bind(itemView)

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
            mBinding.day.text = SimpleDateFormat("dd").format(day)
            mBinding.month.text = SimpleDateFormat("MMM").format(day)
            OnedApp.sApp.mMarkwon.setMarkdown(mBinding.content, it.mStory.content.trim())
            mBinding.content.movementMethod = LinkMovementMethod.getInstance()
            itemView.tag = it
        }
    }
}
