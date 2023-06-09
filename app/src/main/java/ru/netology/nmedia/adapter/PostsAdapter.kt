package ru.netology.nmedia.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.FormatNumber
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post

interface OnInteractionListener {                       // группировка всех обработчики в интерфейсе
    fun onLike(post: Post) {}
    fun onShare(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
}

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener,
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener

) : RecyclerView.ViewHolder(binding.root) {
    private fun openVideo(videoUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
        itemView.context.startActivity(intent)
    }
    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            viewButton.text = FormatNumber.format(post.view)

            if (!post.video.isNullOrBlank()) {
            videoThumbnail.visibility = View.VISIBLE
            playButton.visibility = View.VISIBLE

                // Обработка клика на картинке-заглушке видео или кнопке "Play"
                videoThumbnail.setOnClickListener {
                    openVideo(post.video)
                }
                playButton.setOnClickListener {
                    openVideo(post.video)
                }
            } else {
                videoThumbnail.visibility = View.GONE
                playButton.visibility = View.GONE
            }

            // обращаемся к свойству isChecked и записываем туда флаг у поста (был поставлен лайк или нет)
            likeButton.isChecked = post.likedByMe
            likeButton.text = FormatNumber.format(post.likes)   // выводим количество лайков

            shareButton.isCheckable = post.shareByMe
            shareButton.text = FormatNumber.format(post.share)  // выводим количество репостов

            menuButton.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }
            likeButton.setOnClickListener {
                onInteractionListener.onLike(post)
            }
            shareButton.setOnClickListener {
                onInteractionListener.onShare(post)
            }
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}