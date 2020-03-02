package org.baole.oned.story.list

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.Query
import org.baole.oned.R
import org.baole.oned.databinding.StoryListItemBinding
import org.baole.oned.story.StoryAdapter
import org.baole.oned.story.StoryData
import org.baole.oned.util.DateUtil
import org.baole.oned.util.TextUtil
import java.text.SimpleDateFormat

/**
 * RecyclerView adapter for a list of story.
 */
open class StoryListAdapter(context: Context, query: Query, private val mHeaderListener: () -> Unit, private val mItemListener: (StoryData) -> Unit) :
        StoryAdapter<StoryViewHolder>(context, query) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == ITEM_TYPE_HEADER) {
            StoryHeaderViewHolder(inflater.inflate(R.layout.story_list_header, parent, false), mHeaderListener)
        } else {
            StoryItemViewHolder(inflater.inflate(R.layout.story_list_item, parent, false), mItemListener)
        }
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(getStory(position))
        Log.d(StoryAdapter.TAG, "onBindViewHolder $position $itemCount")
        if (position == itemCount - 1) {
            loadNext()
        }
    }

}

open class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    open fun bind(story: StoryData?) {}

}

class StoryHeaderViewHolder(itemView: View, private val mHeaderListener: () -> Unit) : StoryViewHolder(itemView) {
    init {
        itemView.setOnClickListener {
            mHeaderListener.invoke()
        }
    }
}

class StoryItemViewHolder(itemView: View, private val mItemListener: (StoryData) -> Unit) : StoryViewHolder(itemView) {
    var binding = StoryListItemBinding.bind(itemView)

    init {
        itemView.setOnClickListener {
            itemView.tag?.let {
                mItemListener.invoke(it as StoryData)
            }
        }
    }

    override fun bind(story: StoryData?) {
        story?.let {
            val day = DateUtil.key2date(it.mStory.day)
            binding.day.text = SimpleDateFormat("dd").format(day)
            binding.month.text = SimpleDateFormat("MMM").format(day)
            binding.content.text = TextUtil.markdown2text(it.mStory.content.trim())
            itemView.tag = it
        }
    }
}
