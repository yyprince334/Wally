package com.prince.wally.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.prince.wally.R
import com.prince.wally.databinding.ItemPictureBinding
import com.prince.wally.model.remote.response.CommonPic
import com.prince.wally.util.loadImage

class GalleryAdapter(
    private val onClick: (CommonPic?) -> Unit
) : PagingDataAdapter<CommonPic, RecyclerView.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GalleryViewHolder(
        ItemPictureBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        onClick
    )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is GalleryViewHolder) getItem(position)?.let { holder.bind(it) }
    }

    class GalleryViewHolder(
        val binding: ItemPictureBinding,
        private val onClick: (CommonPic?) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        private var pic: CommonPic? = null

        fun bind(pic: CommonPic) {
            this.pic = pic
            itemView.context.loadImage(pic.url, binding.image, null, R.color.gray)
            itemView.setOnClickListener { onClick(pic) }
        }
    }

    companion object {
        object DiffCallback : DiffUtil.ItemCallback<CommonPic>() {
            override fun areItemsTheSame(old: CommonPic, new: CommonPic) = old == new
            override fun areContentsTheSame(old: CommonPic, new: CommonPic) = old.id == new.id
        }
    }
}