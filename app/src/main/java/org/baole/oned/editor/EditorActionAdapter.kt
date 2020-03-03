package org.baole.oned.editor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.baole.oned.R
import org.baole.oned.databinding.StoryEditorActionHolderBinding

class EditorActionAdapter(private val editor: StoryEditText, private val listener: ((StoryEditText, Action) -> Unit)) : RecyclerView.Adapter<EditorActionHolder>() {
    var actions = mutableListOf<Action>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditorActionHolder {
        val inflater = LayoutInflater.from(parent.context)
        return EditorActionHolder(editor, listener, inflater.inflate(R.layout.story_editor_action_holder, parent, false))
    }

    override fun onBindViewHolder(holder: EditorActionHolder, position: Int) {
        holder.bind(actions[position])
    }

    override fun getItemCount(): Int {
        return actions.size
    }
}

class EditorActionHolder(val editor: StoryEditText, listener: ((StoryEditText, Action) -> Unit), itemView: View) : RecyclerView.ViewHolder(itemView) {
    val mBinding = StoryEditorActionHolderBinding.bind(itemView)

    init {
        mBinding.action.setActionListener(listener)
    }

    fun bind(data: Action) {
        mBinding.action.bind(editor, data)
    }
}