package org.sopt.sopkathon_android2.presentation.main.navigation

import kotlinx.serialization.Serializable
import org.sopt.sopkathon_android2.core.navigation.Route

// 0.2 사진 사용 동의
@Serializable
data object Consent : Route

// 0.3 프로필 생성
@Serializable
data object ProfileCreate : Route

// 1 피드 (하단 탭)
@Serializable
data object Feed : Route

// 6 검색 (하단 탭)
@Serializable
data object Search : Route

// 2.1 동료 선택 (하단 탭 '코디 만들기')
@Serializable
data object ColleagueSelect : Route

// 2.2 옷 입히기
@Serializable
data class DressUp(val colleagueId: Long) : Route

// 2.3 게시글 업로드
@Serializable
data class PostUpload(val colleagueId: Long) : Route

// 3 내 프로필 (하단 탭)
@Serializable
data object MyProfile : Route

// 4 동료 프로필
@Serializable
data class ColleagueProfile(val userId: Long) : Route

// 5 게시물 상세
@Serializable
data class PostDetail(val postId: Long) : Route
