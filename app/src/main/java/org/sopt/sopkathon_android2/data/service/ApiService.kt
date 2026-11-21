package org.sopt.sopkathon_android2.data.service

import org.sopt.sopkathon_android2.core.network.BaseResponse
import org.sopt.sopkathon_android2.data.dto.response.ClothesListResponseDto
import retrofit2.http.GET

interface ApiService {
    // 서버 API 나오면 여기에 추가
    // @GET("/api/v1/...")
    // suspend fun getXxx(): BaseResponse<XxxResponseDto>

    @GET("/api/v1/clothes")
    suspend fun getClothes(): BaseResponse<ClothesListResponseDto>
}
