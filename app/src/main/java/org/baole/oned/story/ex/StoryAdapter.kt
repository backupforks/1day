package org.baole.oned.story.ex

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import org.baole.oned.R
import org.baole.oned.adapter.FirestoreAdapter
import org.baole.oned.databinding.StoryListItemBinding
import org.baole.oned.model.Story
import org.baole.oned.util.DateUtil
import org.baole.oned.util.TextUtil
import java.text.SimpleDateFormat

/**
 * RecyclerView adapter for a list of story.
 */
class StoryAdapter(query: Query, private val mHeaderListener: () -> Unit, private val mItemListener: (Story) -> Unit, options: FirestorePagingOptions<Story>) :
        FirestorePagingAdapter<Story, StoryViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return StoryItemViewHolder(inflater.inflate(R.layout.story_list_item, parent, false), mItemListener)
    }

    override fun onBindViewHolder(viewHolder: StoryViewHolder, position: Int, data: Story) {
        viewHolder.bind(data)
    }
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
//        val inflater = LayoutInflater.from(parent.context)
//        return if (viewType == ITEM_TYPE_HEADER) {
//            StoryHeaderViewHolder(inflater.inflate(R.layout.story_list_header, parent, false), mHeaderListener)
//        } else {
//            StoryItemViewHolder(inflater.inflate(R.layout.story_list_item, parent, false), mItemListener)
//        }
//    }
//
//    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
//        if (position < headerItemCount) {
//            holder.bind(null)
//        } else {
//            holder.bind(getSnapshot(position))
//        }
//    }

}

open class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    open fun bind(story: Story) {}
}

class StoryHeaderViewHolder2(itemView: View, private val mHeaderListener: () -> Unit) : StoryViewHolder(itemView) {
    init {
        itemView.setOnClickListener {
            mHeaderListener.invoke()
        }
    }
}

class StoryItemViewHolder(itemView: View, private val mItemListener: (Story) -> Unit) : StoryViewHolder(itemView) {
    var binding = StoryListItemBinding.bind(itemView)

    init {
        itemView.setOnClickListener {
            itemView.tag?.let {
                mItemListener.invoke(it as Story)
            }
        }
    }

    override fun bind(story: Story) {
        val day = DateUtil.key2date(story.day)
        binding.day.text = SimpleDateFormat("dd").format(day)
        binding.month.text = SimpleDateFormat("MMM").format(day)
        binding.content.text = TextUtil.markdown2text(story.content.trim())
        itemView.tag = story
    }
}
