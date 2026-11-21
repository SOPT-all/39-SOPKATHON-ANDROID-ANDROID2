package org.sopt.sopkathon_android2.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// 커서 페이징 목록 API의 data. 마지막 페이지는 nextCursor = null, hasMore = false
@Serializable
data class CursorResponseDto<T>(
    @SerialName("items")
    val items: List<T>,
    @SerialName("nextCursor")
    val nextCursor: String? = null, // 마지막 페이지에 키가 없어도 파싱 실패 방지
    @SerialName("hasMore")
    val hasMore: Boolean,
)
