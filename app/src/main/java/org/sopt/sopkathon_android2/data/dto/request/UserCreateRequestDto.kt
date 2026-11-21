package org.sopt.sopkathon_android2.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserCreateRequestDto(
    @SerialName("name")
    val name: String,
    @SerialName("department")
    val department: String, // 부서 코드 (SALES_1 등)
    @SerialName("position")
    val position: String, // 직급 코드 (STAFF 등)
    @SerialName("description")
    val description: String? = null,
    @SerialName("profileImageKey")
    val profileImageKey: String? = null,
)
