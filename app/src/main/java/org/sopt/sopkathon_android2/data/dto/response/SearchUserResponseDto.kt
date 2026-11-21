package org.sopt.sopkathon_android2.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class SearchUserResponseDto(
    val userId: Long,
    val name: String,
    val position: String,
    val department: String,
    val profileImageUrl: String? = null,
)
