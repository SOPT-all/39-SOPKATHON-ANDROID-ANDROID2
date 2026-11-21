package org.sopt.sopkathon_android2.presentation.upload.model

import android.net.Uri
import org.sopt.sopkathon_android2.presentation.main.navigation.PostUpload

/**
 * 코디 완성 때 올릴 옷 하나. `OutfitUploader.upload`의 입력
 *
 * @param name 화면에 보이는 이름. 입력하지 않았으면 "상의", "하의"를 넣어서 넘김
 * @param category 서버 값 TOP, BOTTOM
 * @param categoryLabel "상의", "하의". 2.3 안내 문구에 사용
 * @param link 쇼핑몰 링크. 서버가 필수로 받아서 새 옷은 비면 안 됨
 * @param isInfoComplete 이름과 링크를 다 넣었는지. 2.3 점 색과 안내 문구에 사용
 * @param clothesId 옷장에 있던 옷. null이면 [cutoutUri]를 올리고 옷 추가 API로 받음
 * @param cutoutUri 새로 찍은 옷의 누끼 PNG. 업로드가 끝나면 삭제
 */
data class OutfitUploadItem(
    val name: String,
    val category: String,
    val categoryLabel: String,
    val link: String,
    val isInfoComplete: Boolean,
    val clothesId: Long? = null,
    val cutoutUri: Uri? = null,
)

/**
 * `OutfitUploader.upload`의 결과
 *
 * @param imageKey 캐릭터 캡처 이미지 key. 피드 생성 API에 보냄
 * @param clothesIds 입력 목록과 같은 순서의 옷 ID. 새 옷은 옷 추가 API로 받은 값
 */
data class OutfitUploadResult(
    val imageKey: String,
    val clothesIds: List<Long>,
)

/**
 * 2.3 코디 게시하기로 넘길 Route를 만드는 함수
 *
 * @param captureUri 업로드에 쓴 캡처 PNG. 2.3 썸네일로 쓰고 2.3을 나가면 삭제
 * @param items `OutfitUploader.upload`에 넘긴 목록 그대로
 */
fun OutfitUploadResult.toPostUpload(
    colleagueId: Long,
    colleagueName: String,
    colleaguePosition: String,
    captureUri: Uri,
    items: List<OutfitUploadItem>,
) = PostUpload(
    colleagueId = colleagueId,
    colleagueName = colleagueName,
    colleaguePosition = colleaguePosition,
    imageKey = imageKey,
    imageUri = captureUri.toString(),
    clothesIds = clothesIds,
    itemNames = items.map { it.name },
    itemCategoryLabels = items.map { it.categoryLabel },
    itemInfoCompletes = items.map { it.isInfoComplete },
)
