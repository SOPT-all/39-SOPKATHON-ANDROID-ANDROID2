package org.sopt.sopkathon_android2.data.service

import org.sopt.sopkathon_android2.core.network.BaseResponse
import org.sopt.sopkathon_android2.data.dto.request.ClothesCreateRequestDto
import org.sopt.sopkathon_android2.data.dto.request.PresignedUrlRequestDto
import org.sopt.sopkathon_android2.data.dto.request.UserCreateRequestDto
import org.sopt.sopkathon_android2.data.dto.response.ClothesCreateResponseDto
import org.sopt.sopkathon_android2.data.dto.response.ClothesListResponseDto
import org.sopt.sopkathon_android2.data.dto.response.CursorResponseDto
import org.sopt.sopkathon_android2.data.dto.response.FeedItemResponseDto
import org.sopt.sopkathon_android2.data.dto.response.PresignedUrlResponseDto
import org.sopt.sopkathon_android2.data.dto.response.SearchUserResponseDto
import org.sopt.sopkathon_android2.data.dto.response.UserCreateResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("/api/v1/clothes")
    suspend fun getClothes(): BaseResponse<ClothesListResponseDto>

    @POST("/api/v1/presigned-url")
    suspend fun postPresignedUrl(
        @Body request: PresignedUrlRequestDto,
    ): BaseResponse<PresignedUrlResponseDto>

    @POST("/api/v1/clothes")
    suspend fun postClothes(
        @Body request: ClothesCreateRequestDto,
    ): BaseResponse<ClothesCreateResponseDto>

    @POST("/api/v1/users")
    suspend fun postUser(
        @Body request: UserCreateRequestDto,
    ): BaseResponse<UserCreateResponseDto>

    @GET("/api/v1/users")
    suspend fun searchUsers(
        @Query("keyword") keyword: String?,
        @Query("position") position: String?,
        @Query("cursor") cursor: String?,
    ): BaseResponse<CursorResponseDto<SearchUserResponseDto>>

    @GET("/api/v1/users/{userId}/feed")
    suspend fun getFeed(
        @Path("userId") userId: Long,
        @Query("sort") sort: String,
        @Query("period") period: String,
        @Query("limit") limit: Int,
        @Query("cursor") cursor: String?,
    ): BaseResponse<CursorResponseDto<FeedItemResponseDto>>
}
