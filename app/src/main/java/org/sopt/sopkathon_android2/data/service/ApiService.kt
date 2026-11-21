package org.sopt.sopkathon_android2.data.service

import org.sopt.sopkathon_android2.core.network.BaseResponse
import org.sopt.sopkathon_android2.data.dto.response.CursorResponseDto
import org.sopt.sopkathon_android2.data.dto.response.FeedPostResponseDto
import org.sopt.sopkathon_android2.data.dto.response.PostLikeResponseDto
import retrofit2.http.DELETE
import retrofit2.http.POST
import org.sopt.sopkathon_android2.data.dto.response.PostDetailResponseDto
import org.sopt.sopkathon_android2.data.dto.response.CommentResponseDto
import org.sopt.sopkathon_android2.data.dto.response.CommentCreateResponseDto
import org.sopt.sopkathon_android2.data.dto.request.CommentCreateRequestDto
import retrofit2.http.Header
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("api/v1/users/{userId}/feed")
    suspend fun getFeed(
        @Path("userId") userId: Long,
        @Query("sort") sort: String = "LATEST",
        @Query("period") period: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("cursor") cursor: String? = null,
    ): BaseResponse<CursorResponseDto<FeedPostResponseDto>>
    @POST("api/v1/users/{userId}/posts/{postId}/like")
    suspend fun postPostLike(
        @Path("userId") userId: Long,
        @Path("postId") postId: Long,
    ): BaseResponse<PostLikeResponseDto>

    @DELETE("api/v1/users/{userId}/posts/{postId}/like")
    suspend fun deletePostLike(
        @Path("userId") userId: Long,
        @Path("postId") postId: Long,
    ): BaseResponse<PostLikeResponseDto>

    @GET("api/v1/users/{userId}/posts/{postId}")
    suspend fun getPostDetail(
        @Path("userId") userId: Long,
        @Path("postId") postId: Long,
    ): BaseResponse<PostDetailResponseDto>

    @GET("api/v1/users/{userId}/posts/{postId}/comments")
    suspend fun getPostComments(
        @Path("userId") userId: Long,
        @Path("postId") postId: Long,
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int = 20,
        @Header("X-User-Id") requesterId: Long = userId,
    ): BaseResponse<CursorResponseDto<CommentResponseDto>>

    @POST("api/v1/users/{userId}/posts/{postId}/comments")
    suspend fun postPostComment(
        @Path("userId") userId: Long,
        @Path("postId") postId: Long,
        @Body request: CommentCreateRequestDto,
        @Header("X-User-Id") requesterId: Long = userId,
    ): BaseResponse<CommentCreateResponseDto>
}
