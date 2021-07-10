package ru.skillbranch.skillarticles.ui.article

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.skillbranch.skillarticles.data.models.CommentItemData
import ru.skillbranch.skillarticles.ui.custom.CommentItemView

class CommentsAdapter(private val listener: (CommentItemData) -> Unit) :
    PagedListAdapter<CommentItemData, CommentVH>(CommentsDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentVH {
        val commentItemView = CommentItemView(parent.context)
        return CommentVH(commentItemView, listener)
    }

    override fun onBindViewHolder(holder: CommentVH, position: Int) {
        holder.bind(getItem(position))
    }

}

class CommentVH(containerView: View, val listener: (CommentItemData) -> Unit) :
    RecyclerView.ViewHolder(containerView) {
    fun bind(item: CommentItemData?) {
        if (item != null) {
            itemView.setOnClickListener { listener(item) }
            (itemView as CommentItemView).bind(item)
        }
    }
}

class CommentsDiffCallback() : DiffUtil.ItemCallback<CommentItemData>() {
    override fun areItemsTheSame(oldItem: CommentItemData, newItem: CommentItemData): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CommentItemData, newItem: CommentItemData): Boolean {
        return oldItem == newItem
    }
}