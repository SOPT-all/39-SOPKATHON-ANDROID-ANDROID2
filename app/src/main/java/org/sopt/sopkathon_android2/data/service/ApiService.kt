package org.sopt.sopkathon_android2.data.service

import org.sopt.sopkathon_android2.core.network.BaseResponse
import org.sopt.sopkathon_android2.data.dto.request.ClothesCreateRequestDto
import org.sopt.sopkathon_android2.data.dto.request.PresignedUrlRequestDto
import org.sopt.sopkathon_android2.data.dto.response.ClothesCreateResponseDto
import org.sopt.sopkathon_android2.data.dto.response.ClothesListResponseDto
import org.sopt.sopkathon_android2.data.dto.response.PresignedUrlResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    // 서버 API 나오면 여기에 추가
    // @GET("/api/v1/...")
    // suspend fun getXxx(): BaseResponse<XxxResponseDto>

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
}
