package org.sopt.sopkathon_android2.data.service

import org.sopt.sopkathon_android2.core.network.BaseResponse
import org.sopt.sopkathon_android2.data.dto.response.CursorResponseDto
import org.sopt.sopkathon_android2.data.dto.response.FeedPostResponseDto
import org.sopt.sopkathon_android2.data.dto.response.PostLikeResponseDto
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FeedApiService {
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

}
