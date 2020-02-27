package org.baole.oned.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import org.baole.oned.R
import org.baole.oned.databinding.StoryListItemBinding
import org.baole.oned.model.Story
import org.baole.oned.util.DateUtil
import org.baole.oned.util.TextUtil
import java.text.SimpleDateFormat
import java.time.Month
import java.util.*

/**
 * RecyclerView adapter for a list of story.
 */
open class StoryAdapter(query: Query, private val mHeaderListener: () -> Unit, private val mItemListener: (DocumentSnapshot) -> Unit) : FirestoreAdapter<StoryViewHolder>(query, 1) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == ITEM_TYPE_HEADER) {
            return StoryHeaderViewHolder(inflater.inflate(R.layout.story_list_header, parent, false), mHeaderListener)
        } else {
            return StoryItemViewHolder(inflater.inflate(R.layout.story_list_item, parent, false), mItemListener)
        }
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        if (position == 0) {
            holder.bind(null)
        } else {
            holder.bind(getSnapshot(position))
        }
    }

}

open class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    open fun bind(snapshot: DocumentSnapshot?) {}

}

class StoryHeaderViewHolder(itemView: View, private val mHeaderListener: () -> Unit) : StoryViewHolder(itemView) {
    init {
        itemView.setOnClickListener {
            mHeaderListener.invoke()
        }
    }
}

class StoryItemViewHolder(itemView: View, private val mItemListener: (DocumentSnapshot) -> Unit) : StoryViewHolder(itemView) {
    var binding = StoryListItemBinding.bind(itemView)

    init {
        itemView.setOnClickListener {
            itemView.tag?.let {
                mItemListener.invoke(it as DocumentSnapshot)
            }
        }
    }

    override fun bind(snapshot: DocumentSnapshot?) {

        snapshot?.toObject(Story::class.java)?.let {
            val day = DateUtil.key2date(it.day)
            binding.day.text = SimpleDateFormat("dd").format(day)
            binding.month.text = SimpleDateFormat("MMM").format(day)
            binding.content.text = TextUtil.markdown2text(it.content)
            itemView.tag = snapshot
        }
    }
}
