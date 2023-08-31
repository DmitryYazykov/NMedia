package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.internal.EMPTY_REQUEST
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit

class PostRepositoryImpl(
    //private val dao: PostDao,
) : PostRepository {

    // Настраиваю okhttp клиент
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    // парсинг ответов
    private val gson = Gson()

    // получаемый тип
    private val typeToken = object : TypeToken<List<Post>>() {}.type


    // константы
    private companion object {
        const val BASE_URL = "http://10.0.2.2:9999/"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAllAsync(callback: PostRepository.Callback<List<Post>>) {
        val request = Request.Builder()
            .url("${BASE_URL}api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    try {
                        val body = response.body?.string() ?: throw RuntimeException("body is null")
                        callback.onSuccess(gson.fromJson(body, typeToken))
                    } catch (e: Exception) {
                        callback.onError()
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError()
                }
            })
    }

    override fun likeByIdAsync(id: Long, callback: PostRepository.Callback<Unit>) {
        val request = Request.Builder()
            .post(EMPTY_REQUEST)
            .url("${BASE_URL}api/posts/$id/likes")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    try {
                        response.body?.string() ?: throw RuntimeException("body is null")
                        callback.onSuccess(Unit)
                    } catch (e: Exception) {
                        callback.onError()
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError()
                }
            })

    }

    override fun unlikeByIdAsync(id: Long, callback: PostRepository.Callback<Unit>) {
        val request = Request.Builder()
            .delete(EMPTY_REQUEST)
            .url("${BASE_URL}api/posts/$id/likes")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {

                override fun onResponse(call: Call, response: Response) {

                    response.body?.string() ?: throw RuntimeException("body is null")
                    callback.onSuccess(Unit)
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError()
                }
            })
    }

    override fun saveAsync(post: Post, callback: PostRepository.Callback<Unit>) {
        val request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    try {
                        if (response.isSuccessful) {
                            callback.onSuccess(Unit)
                        } else {
                            callback.onError()
                        }
                    } catch (e: Exception) {
                        callback.onError()
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError()
                }
            })
    }

    override fun removeByIdAsync(id: Long, callback: PostRepository.Callback<Unit>) {
        val request = Request.Builder()
            .url("${BASE_URL}api/slow/posts/$id")
            .delete()
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    callback.onSuccess(Unit)
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError()
                }
            })
    }
}