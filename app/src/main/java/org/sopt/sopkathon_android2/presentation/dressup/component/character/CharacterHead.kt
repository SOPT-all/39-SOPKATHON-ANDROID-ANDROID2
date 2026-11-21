package org.sopt.sopkathon_android2.presentation.dressup.component.character

import android.net.Uri
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

/**
 * 옷 입히기 캐릭터 머리 (동료 실물 사진 누끼)
 *
 * 스티커가 아니라 고정. `StickerCanvas` 안에서 `Modifier.align(Alignment.TopCenter)`로 둠.
 * 턱선 아래가 잘린 머리라 상의 `Sticker`보다 뒤에 불러 위에 그려야 턱이 옷깃에 안 가려짐.
 * 턱이 옷깃 쪽에 닿도록 영역 아래 가운데에 맞춤
 *
 * @param imageUri [CharacterHeadMaker.make] 결과. 만드는 중이면 null
 */
@Composable
fun CharacterHead(
    imageUri: Uri?,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = imageUri,
        contentDescription = null,
        // 피그마 캐릭터 190x500 안 머리 크기
        modifier = modifier.size(95.dp, 115.dp),
        alignment = Alignment.BottomCenter,
        contentScale = ContentScale.Fit,
    )
}
