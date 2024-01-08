package vn.edu.hust.networkexamples

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MyService {
    @GET("posts")
    suspend fun listAllPosts(): List<Post>

    @GET("posts/{postId}")
    suspend fun getPost(@Path("postId") postId: Int): Post
}