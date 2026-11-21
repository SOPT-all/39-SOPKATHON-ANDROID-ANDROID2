package org.sopt.sopkathon_android2.presentation.profilecreate

import android.net.Uri
import org.sopt.sopkathon_android2.presentation.main.navigation.SignUpComplete

data class ProfileCreateUiState(
    val photoUri: Uri? = null,
    val name: String = "",
    val department: String = "",
    val rank: String = "",
    val isSaving: Boolean = false,
) {
    val isSaveEnabled: Boolean
        get() = photoUri != null && name.isNotBlank() && department.isNotEmpty() && rank.isNotEmpty() && !isSaving

    companion object {
        const val NAME_MAX_LENGTH = 10

        // 화면 표시값 to 서버 코드
        val departmentCodes = linkedMapOf(
            "영업1팀" to "SALES_1",
            "영업2팀" to "SALES_2",
            "마케팅팀" to "MARKETING",
            "인사팀" to "HR",
            "개발팀" to "DEVELOPMENT",
            "디자인팀" to "DESIGN",
        )
        val rankCodes = linkedMapOf(
            "사원" to "STAFF",
            "주임" to "ASSOCIATE",
            "대리" to "ASSISTANT_MANAGER",
            "과장" to "MANAGER",
            "차장" to "DEPUTY_GENERAL_MANAGER",
            "부장" to "GENERAL_MANAGER",
            "이사" to "DIRECTOR",
        )
        val departments = departmentCodes.keys.toList()
        val ranks = rankCodes.keys.toList()
    }
}

sealed interface ProfileCreateUiEffect {
    data class NavigateToSignUpComplete(val route: SignUpComplete) : ProfileCreateUiEffect
    data class ShowToast(val message: String) : ProfileCreateUiEffect
}
