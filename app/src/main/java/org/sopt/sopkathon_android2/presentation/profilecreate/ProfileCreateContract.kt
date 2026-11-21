package org.sopt.sopkathon_android2.presentation.profilecreate

import android.net.Uri

data class ProfileCreateUiState(
    val photoUri: Uri? = null,
    val name: String = "",
    val department: String = "",
    val rank: String = "",
) {
    val isSaveEnabled: Boolean
        get() = photoUri != null && name.isNotBlank() && department.isNotEmpty() && rank.isNotEmpty()

    companion object {
        const val NAME_MAX_LENGTH = 10

        // 서버 값 정해지면 교체
        val departments = listOf("영업1팀", "영업2팀", "마케팅팀", "인사팀", "개발팀", "디자인팀")
        val ranks = listOf("사원", "주임", "대리", "과장", "차장", "부장", "이사")
    }
}
