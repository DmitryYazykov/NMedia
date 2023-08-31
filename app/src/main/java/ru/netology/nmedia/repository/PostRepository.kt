package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {

    // асинхронный
    fun getAllAsync(callback: Callback<List<Post>>)
    fun saveAsync(post: Post, callback: Callback<Unit>)
    fun likeByIdAsync(id: Long, callback: Callback<Unit>)
    fun unlikeByIdAsync(id: Long, callback: Callback<Unit>)
    fun removeByIdAsync(id: Long, callback: Callback<Unit>)

    interface Callback<T> {                            // общий дженерик-интерфейс
        fun onSuccess(posts: T)
        fun onError()
    }
}