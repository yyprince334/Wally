package com.prince.wally.ui.activity.categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.prince.wally.R
import java.util.*

class CategoriesAdapter(
    private val categories: List<Locale.Category>,
    private val clickListener: (Locale.Category) -> Unit
) : RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CategoryViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
    )

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) = with(holder) {
        val category = categories[position]
        categoryName.text = category.categoryName
        itemView.context.loadImage(category.categoryUrl, categoryImage, null, R.color.gray)
        itemView.setOnClickListener { clickListener(category) }
    }

    override fun getItemCount() = categories.size

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var categoryImage: ImageView = itemView.findViewById(R.id.categoryImageView)
        var categoryName: TextView = itemView.findViewById(R.id.categoryTitle)
    }
}