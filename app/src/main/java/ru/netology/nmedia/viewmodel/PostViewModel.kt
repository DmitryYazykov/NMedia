package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorAvatar = "",
    likedByMe = false,
    likes = 0,
    published = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepository = PostRepositoryImpl(
        //AppDb.getInstance(context = application).postDao()
    )
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel> = _data
    val edited = MutableLiveData(empty)

    // создаём событие - пост был создан
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit> = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.postValue(FeedModel(loading = true))
        repository.getAllAsync(object : PostRepository.Callback<List<Post>> {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError() {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun save() {
        edited.value?.let { post ->
            repository.saveAsync(post, object : PostRepository.Callback<Unit> {
                override fun onSuccess(posts: Unit) {
                    _postCreated.postValue(Unit)
                }

                override fun onError() {
                    _data.postValue(FeedModel(error = true))
                }
            })
            edited.postValue(empty)
        }
    }


    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(post: Post) {
        repository.likeByIdAsync(post.id, object : PostRepository.Callback<Unit> {
            override fun onSuccess(posts: Unit) {
                if (post.likedByMe) {
                    repository.unlikeByIdAsync(post.id, object : PostRepository.Callback<Unit> {
                        override fun onSuccess(posts: Unit) {
                            val newPosts = _data.value?.posts?.map {
                                if (it.id == post.id) {
                                    post.copy(likedByMe = false, likes = it.likes - 1)
                                } else {
                                    it
                                }
                            }.orEmpty()
                            _data.postValue(_data.value?.copy(posts = newPosts))
                        }

                        override fun onError() {
                            _data.postValue(FeedModel(error = true))
                        }
                    })
                } else {
                    repository.likeByIdAsync(post.id, object : PostRepository.Callback<Unit> {
                        override fun onSuccess(posts: Unit) {
                            val newPosts = _data.value?.posts?.map {
                                if (it.id == post.id) {
                                    post.copy(likedByMe = true, likes = it.likes + 1)
                                } else {
                                    it
                                }
                            }.orEmpty()
                            _data.postValue(_data.value?.copy(posts = newPosts))
                        }

                        override fun onError() {
                            _data.postValue(FeedModel(error = true))
                        }
                    })
                }
            }

            override fun onError() {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun removeById(id: Long) {
        val old = _data.value
        _data.postValue(
            old?.copy(
                posts = old.posts.filter {
                    it.id != id
                }
            )
        )
        repository.removeByIdAsync(id, object : PostRepository.Callback<Unit> {
            override fun onSuccess(posts: Unit) {

            }

            override fun onError() {
                _data.postValue(FeedModel(error = true))
            }

        })
    }
}