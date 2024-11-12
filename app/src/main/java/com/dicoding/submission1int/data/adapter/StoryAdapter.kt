package com.dicoding.submission1int.data.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.submission1int.data.model.Story
import com.dicoding.submission1int.databinding.ItemStoryBinding

class StoryAdapter : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {
    private val stories = ArrayList<Story>()
    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setStories(stories: List<Story>) {
        this.stories.clear()
        this.stories.addAll(stories)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(stories[position])
    }

    override fun getItemCount() = stories.size

    inner class StoryViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Story) {
            with(binding) {
                tvItemName.text = story.name
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .into(ivItemPhoto)

                itemView.setOnClickListener {
                    onItemClickCallback?.onItemClicked(story)
                }
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(story: Story)
    }
}