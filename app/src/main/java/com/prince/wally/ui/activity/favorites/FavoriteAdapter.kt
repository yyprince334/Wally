package com.prince.wally.ui.activity.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.prince.wally.R
import com.prince.wally.databinding.ItemPictureBinding
import com.prince.wally.model.local.Favorite
import com.prince.wally.util.loadImage

class FavoriteAdapter(
    private val onClick: (Favorite) -> Unit
) : ListAdapter<Favorite, FavoritesAdapter.FavoritesViewHolder>(FavDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FavoritesViewHolder(
        ItemPictureBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        onClick
    )

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FavoritesViewHolder(
        private val binding: ItemPictureBinding,
        private val onClick: (Favorite) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        var favorite: Favorite? = null

        fun bind(favorite: Favorite) {
            this.favorite = favorite
            itemView.context.loadImage(favorite.url, binding.image, null, R.color.gray)
            itemView.setOnClickListener { onClick(favorite) }
        }
    }

    companion object {
        class FavDiffCallback : DiffUtil.ItemCallback<Favorite>() {
            override fun areItemsTheSame(old: Favorite, new: Favorite) = old.url == new.url
            override fun areContentsTheSame(old: Favorite, new: Favorite) = old == new
        }
    }
}