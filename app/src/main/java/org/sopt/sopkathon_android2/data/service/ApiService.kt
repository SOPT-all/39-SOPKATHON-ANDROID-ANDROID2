package org.sopt.sopkathon_android2.data.service

import org.sopt.sopkathon_android2.core.network.BaseResponse
import org.sopt.sopkathon_android2.data.dto.response.CursorResponseDto
import org.sopt.sopkathon_android2.data.dto.response.FeedPostResponseDto
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
}
