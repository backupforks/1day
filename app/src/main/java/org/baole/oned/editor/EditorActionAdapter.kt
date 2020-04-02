package org.baole.oned.editor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.baole.oned.databinding.StoryEditorActionHolderBinding

class EditorActionAdapter(private val mEditor: StoryEditText, private val mListener: ((StoryEditText, Action) -> Unit), val mLayoutId: Int) : RecyclerView.Adapter<EditorActionHolder>() {
    private var mActions = listOf<Action>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditorActionHolder {
        val inflater = LayoutInflater.from(parent.context)
        return EditorActionHolder(mEditor, mListener, inflater.inflate(mLayoutId, parent, false))
    }

    override fun onBindViewHolder(holder: EditorActionHolder, position: Int) {
        holder.bind(mActions[position])
    }

    override fun getItemCount(): Int {
        return mActions.size
    }

    fun setActions(actions: List<Action>) {
        this.mActions = actions
        this.notifyDataSetChanged()
    }
}

class EditorActionHolder(val mEditor: StoryEditText, mListener: ((StoryEditText, Action) -> Unit), itemView: View) : RecyclerView.ViewHolder(itemView) {
    val mBinding = StoryEditorActionHolderBinding.bind(itemView)

    init {
        mBinding.action.setActionListener(mListener)
    }

    fun bind(data: Action) {
        mBinding.action.bind(mEditor, data)
    }
}