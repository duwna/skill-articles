package ru.skillbranch.skillarticles.ui.bookmarks

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import ru.skillbranch.skillarticles.data.models.ArticleItemData
import ru.skillbranch.skillarticles.ui.custom.ArticleItemView

class BookmarksAdapter(
    private val listener: (ArticleItemData) -> Unit,
    private val toggleBookmarkListener: (String, Boolean) -> Unit
) :
    PagedListAdapter<ArticleItemData, BookmarkVH>(BookmarksDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkVH {
        val containerView = ArticleItemView(parent.context)
        return BookmarkVH(containerView)
    }

    override fun onBindViewHolder(holder: BookmarkVH, position: Int) {
        holder.bind(getItem(position), listener, toggleBookmarkListener)
    }

}

class BookmarksDiffCallback : DiffUtil.ItemCallback<ArticleItemData>() {
    override fun areItemsTheSame(oldItem: ArticleItemData, newItem: ArticleItemData): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ArticleItemData, newItem: ArticleItemData): Boolean {
        return oldItem == newItem
    }
}

class BookmarkVH(
    override val containerView: View
) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun bind(
        item: ArticleItemData?,
        listener: (ArticleItemData) -> Unit,
        toggleBookmarkListener: (String, Boolean) -> Unit
    ) {
        (itemView as ArticleItemView).bind(item!!, toggleBookmarkListener)
        itemView.setOnClickListener { listener(item) }
    }

}
