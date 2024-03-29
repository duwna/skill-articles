package ru.skillbranch.skillarticles.ui.dialogs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_category_dialog.*
import kotlinx.android.synthetic.main.item_category_dialog.view.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.local.entities.CategoryData
import ru.skillbranch.skillarticles.extensions.dpToIntPx

class CategoryAdapter(private val listener: (String, Boolean) -> Unit) :
    ListAdapter<CategoryDataItem, CategoryVH>(CategoryDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_dialog, parent, false)
        return CategoryVH(view)
    }

    override fun onBindViewHolder(holder: CategoryVH, position: Int) {
        holder.bind(getItem(position), listener)
    }
}

class CategoryVH(override val containerView: View) : RecyclerView.ViewHolder(containerView),
    LayoutContainer {

    private val categorySize = containerView.context.dpToIntPx(40)

    fun bind(item: CategoryDataItem, listener: (String, Boolean) -> Unit) {
        containerView.ch_select.isChecked = item.isChecked

        Glide.with(containerView.context)
            .load(item.icon)
            .circleCrop()
            .override(categorySize)
            .into(containerView.iv_icon)

        containerView.tv_category.text = item.title
        containerView.tv_count.text = "${item.articlesCount}"

        containerView.ch_select.setOnCheckedChangeListener { _, isChecked ->
            listener(item.categoryId, isChecked)
        }
        itemView.setOnClickListener { ch_select.toggle() }
    }
}

class CategoryDiffCallback : DiffUtil.ItemCallback<CategoryDataItem>() {
    override fun areItemsTheSame(
        oldItem: CategoryDataItem,
        newItem: CategoryDataItem
    ) =
        oldItem.categoryId == newItem.categoryId

    override fun areContentsTheSame(
        oldItem: CategoryDataItem,
        newItem: CategoryDataItem
    ) =
        oldItem == newItem
}

data class CategoryDataItem(
    val categoryId: String,
    val icon: String,
    val title: String,
    val articlesCount: Int = 0,
    val isChecked: Boolean = false
)

fun CategoryData.toItem(isChecked: Boolean) = CategoryDataItem(
    categoryId,
    icon,
    title,
    articlesCount,
    isChecked
)