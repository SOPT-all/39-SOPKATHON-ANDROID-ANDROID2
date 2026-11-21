package org.sopt.sopkathon_android2.presentation.dressup.component

import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.transformations
import coil3.size.Size
import org.sopt.sopkathon_android2.R
import org.sopt.sopkathon_android2.core.designsystem.component.Sticker
import org.sopt.sopkathon_android2.core.designsystem.component.StickerCanvas
import org.sopt.sopkathon_android2.core.designsystem.component.StickerCanvasScope
import org.sopt.sopkathon_android2.core.designsystem.component.StickerCanvasState
import org.sopt.sopkathon_android2.core.designsystem.component.rememberStickerCanvasState
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme
import org.sopt.sopkathon_android2.presentation.dressup.DressUpUiState
import org.sopt.sopkathon_android2.presentation.dressup.component.character.CharacterHead
import org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe.ClothesTrimTransformation
import org.sopt.sopkathon_android2.presentation.dressup.model.ClothesCategory
import org.sopt.sopkathon_android2.presentation.dressup.model.WornItemUiModel

// 피그마 캐릭터 프레임 크기
private val CharacterSize = DpSize(190.dp, 500.dp)

// 옷을 옆으로 옮기고 키울 수 있게 캐릭터 양옆으로 넓힌 캔버스. 캡처 영역
private val CanvasSize = DpSize(280.dp, CharacterSize.height)
private val CharacterInset = (CanvasSize.width - CharacterSize.width) / 2

/**
 * 받침대 위 캐릭터. 몸, 하의, 상의, 머리 순서로 쌓음
 *
 * 받침대는 캡처에 안 들어가게 [StickerCanvas] 밖에 그림
 */
@Composable
fun DressUpCharacter(
    headUri: Uri?,
    wornItems: List<WornItemUiModel>,
    stickerState: StickerCanvasState,
    onItemDelete: (key: Any) -> Unit,
    onStickerActionClick: (key: Any) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.size(CanvasSize)) {
        Pedestal(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 484.dp),
        )
        StickerCanvas(
            state = stickerState,
            onDeleteClick = onItemDelete,
            selectionAction = { key ->
                val item = wornItems.find { it.key == key }
                StickerActionChip(
                    isEdit = item != null && (item.name.isNotBlank() || item.link.isNotBlank()),
                    onClick = { onStickerActionClick(key) },
                )
            },
            modifier = Modifier.fillMaxSize(),
        ) {
            // 캐릭터 프레임과 같은 비율이라 프레임을 채움
            Image(
                painter = painterResource(R.drawable.img_dressup_body),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(CharacterSize),
            )
            WornItemStickers(wornItems.filter { it.category == ClothesCategory.BOTTOM })
            WornItemStickers(wornItems.filter { it.category == ClothesCategory.TOP })
            // 몸 이미지의 목 윗선이 가운데로 파여 있어서 턱이 목 윗선을 덮도록 내림
            CharacterHead(
                imageUri = headUri,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 10.dp),
            )
        }
    }
}

@Composable
private fun StickerCanvasScope.WornItemStickers(items: List<WornItemUiModel>) {
    val context = LocalContext.current

    items.forEach { item ->
        key(item.key) {
            val (size, position) = item.category.stickerFrame()
            Sticker(
                key = item.key,
                size = size,
                initialPosition = DpOffset(position.x + CharacterInset, position.y),
            ) {
                if (item.imageUri != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(item.imageUri)
                            .size(Size.ORIGINAL)
                            .transformations(ClothesTrimTransformation)
                            .build(),
                        contentDescription = item.displayName,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    StickerPlaceholder(label = item.displayName)
                }
            }
        }
    }
}

// 피그마 캐릭터 안 옷 자리
private fun ClothesCategory.stickerFrame(): Pair<DpSize, DpOffset> = when (this) {
    ClothesCategory.TOP -> DpSize(190.dp, 213.dp) to DpOffset(0.dp, 102.dp)
    ClothesCategory.BOTTOM -> DpSize(105.dp, 228.dp) to DpOffset(43.dp, 272.dp)
}

// 옷장 연결 전 예시 옷에 이미지가 없을 때 보여 줌
@Composable
private fun StickerPlaceholder(label: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(SopkathonTheme.colors.coolGray400),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = SopkathonTheme.typography.button,
            color = SopkathonTheme.colors.white,
        )
    }
}

@Composable
private fun Pedestal(modifier: Modifier = Modifier) {
    val colors = SopkathonTheme.colors

    Box(modifier = modifier.size(180.dp, 34.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawOval(color = colors.coolGray100)
        }
        Canvas(
            modifier = Modifier
                .offset(x = 35.dp, y = 12.dp)
                .size(110.dp, 14.dp)
                .blur(2.dp),
        ) {
            drawOval(color = colors.coolGray950.copy(alpha = 0.1f))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF3F5F8, widthDp = 360, heightDp = 640)
@Composable
private fun DressUpCharacterPreview() {
    SopkathonTheme {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            DressUpCharacter(
                headUri = null,
                wornItems = DressUpUiState.dummy.wornItems,
                stickerState = rememberStickerCanvasState(),
                onItemDelete = {},
                onStickerActionClick = {},
            )
        }
    }
}
