package org.sopt.sopkathon_android2.presentation.main.navigation

import kotlinx.serialization.Serializable
import org.sopt.sopkathon_android2.core.navigation.Route

// 0.2 사진 사용 동의
@Serializable
data object Consent : Route

// 0.3 프로필 생성
@Serializable
data object ProfileCreate : Route

// 0.3-7 가입 완료
@Serializable
data class SignUpComplete(
    val name: String,
    val department: String,
    val rank: String,
    val photoUri: String,
) : Route

// 1 피드 (하단 탭)
@Serializable
data object Feed : Route

// 6 검색 (하단 탭)
@Serializable
data object Search : Route

// 2.1 동료 선택 (하단 탭 '코디 만들기')
@Serializable
data object ColleagueSelect : Route

/**
 * 2.2 옷 입히기
 *
 * 2.1에서 고른 동료 정보로 앱 바 이름과 캐릭터 머리를 만듦.
 * 2.1이 아직 colleagueId만 넘겨서 나머지는 기본값
 */
@Serializable
data class DressUp(
    val colleagueId: Long,
    val name: String = "",
    val position: String = "",
    val profileImageUrl: String? = null,
) : Route

/**
 * 2.3 코디 게시하기
 *
 * 2.2 코디 완성 때 올린 결과. `OutfitUploadResult.toPostUpload()`로 만듦
 * item으로 시작하는 목록은 [clothesIds]와 순서가 같음
 *
 * @param imageUri 썸네일로 쓰는 캐릭터 캡처 PNG. 2.3을 나가면 삭제
 */
@Serializable
data class PostUpload(
    val colleagueId: Long,
    val colleagueName: String,
    val colleaguePosition: String,
    val imageKey: String,
    val imageUri: String,
    val clothesIds: List<Long>,
    val itemNames: List<String>,
    val itemCategoryLabels: List<String>,
    val itemInfoCompletes: List<Boolean>,
) : Route

// 3 내 프로필 (하단 탭)
@Serializable
data object MyProfile : Route

// 4 동료 프로필
@Serializable
data class ColleagueProfile(val userId: Long) : Route

// 5 게시물 상세
@Serializable
data class PostDetail(val postId: Long) : Route
