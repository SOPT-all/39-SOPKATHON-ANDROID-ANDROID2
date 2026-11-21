package org.sopt.sopkathon_android2.presentation.upload.model

// 이름이 서버 visibility 값. 시안의 두 가지만 사용 (서버엔 PRIVATE도 있음)
enum class PostVisibility(
    val label: String,
    val description: String,
) {
    DEPARTMENT(label = "우리 부서", description = "같은 부서 동료"),
    PUBLIC(label = "회사 전체", description = "전 직원"),
}
