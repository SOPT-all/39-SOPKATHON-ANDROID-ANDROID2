package org.sopt.sopkathon_android2.presentation.dressup

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.sopt.sopkathon_android2.core.designsystem.component.StickerCanvasState
import org.sopt.sopkathon_android2.core.designsystem.component.rememberStickerCanvasState
import org.sopt.sopkathon_android2.core.designsystem.preview.ScreenPreview
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme
import org.sopt.sopkathon_android2.presentation.dressup.component.DressUpBottomActions
import org.sopt.sopkathon_android2.presentation.dressup.component.DressUpCharacter
import org.sopt.sopkathon_android2.presentation.dressup.component.DressUpTopBar
import org.sopt.sopkathon_android2.presentation.dressup.component.ItemInfoBottomSheet
import org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe.WardrobeDrawer
import org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe.WardrobeDrawerContent
import org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe.model.WardrobeUiState
import org.sopt.sopkathon_android2.presentation.dressup.model.ItemInfoInput
import org.sopt.sopkathon_android2.presentation.main.navigation.DressUp
import org.sopt.sopkathon_android2.presentation.main.navigation.PostUpload

// 피그마에서 앱 바 아래부터 하단 버튼 줄 위까지 높이
private val CharacterAreaHeight = 580.dp

// 2.2 옷 입히기
@Composable
fun DressUpRoute(
    dressUp: DressUp,
    navigateUp: () -> Unit,
    navigateToPostUpload: (PostUpload) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DressUpViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val stickerState = rememberStickerCanvasState()

    LaunchedEffect(dressUp) { viewModel.setColleague(dressUp) }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is DressUpUiEffect.NavigateToPostUpload -> navigateToPostUpload(effect.postUpload)
            }
        }
    }

    DressUpScreen(
        uiState = uiState,
        stickerState = stickerState,
        navigateUp = navigateUp,
        onCompleteClick = { viewModel.onCompleteClick(stickerState::capture) },
        onItemDelete = viewModel::onItemDelete,
        onStickerActionClick = viewModel::onStickerActionClick,
        onPurchaseLinkClick = viewModel::onPurchaseLinkClick,
        onItemInfoSave = viewModel::onItemInfoSave,
        onItemInfoSheetDismiss = viewModel::onItemInfoSheetDismiss,
        wardrobeDrawer = { drawerModifier ->
            WardrobeDrawer(
                selectedKeys = uiState.wornItems.map { it.key }.toSet(),
                onClothesClick = viewModel::onWear,
                onClothesAdd = viewModel::onWear,
                modifier = drawerModifier,
            )
        },
        modifier = modifier,
    )
}

/**
 * @param wardrobeDrawer 옷장 드로어. 드로어 ViewModel이 열리자마자 옷장을 조회해서 미리보기에서는 바꿔 넣음
 */
@Composable
private fun DressUpScreen(
    uiState: DressUpUiState,
    stickerState: StickerCanvasState,
    navigateUp: () -> Unit,
    onCompleteClick: () -> Unit,
    onItemDelete: (key: Any) -> Unit,
    onStickerActionClick: (key: Any) -> Unit,
    onPurchaseLinkClick: () -> Unit,
    onItemInfoSave: (List<ItemInfoInput>) -> Unit,
    onItemInfoSheetDismiss: () -> Unit,
    wardrobeDrawer: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SopkathonTheme.colors.coolGray50),
    ) {
        DressUpTopBar(
            colleagueName = uiState.colleagueName,
            onBackClick = navigateUp,
        )
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .pointerInput(stickerState) { detectTapGestures { stickerState.clearSelection() } },
        ) {
            // 피그마보다 낮은 화면에서 받침대가 하단 버튼에 가리지 않게 비율 그대로 줄임
            val scale = (maxHeight / CharacterAreaHeight).coerceAtMost(1f)

            // 왼쪽 옷장 폭만큼 오른쪽으로 비켜 둠 (피그마 캐릭터 x 119)
            DressUpCharacter(
                headUri = uiState.headUri,
                wornItems = uiState.wornItems,
                stickerState = stickerState,
                onItemDelete = onItemDelete,
                onStickerActionClick = onStickerActionClick,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(x = 34.dp, y = 52.dp * scale)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        transformOrigin = TransformOrigin(0.5f, 0f)
                    },
            )
            // 피그마 옷장 위치와 높이. 낮은 화면에서는 남은 높이만큼만 차지
            wardrobeDrawer(
                Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 12.dp)
                    .height(560.dp),
            )
        }
        DressUpBottomActions(
            incompleteCount = uiState.incompleteCount,
            isCompleteEnabled = uiState.isCompleteEnabled,
            onPurchaseLinkClick = onPurchaseLinkClick,
            onCompleteClick = onCompleteClick,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        )
    }

    uiState.itemInfoSheetItems?.let { items ->
        ItemInfoBottomSheet(
            items = items,
            onSaveClick = onItemInfoSave,
            onDismissRequest = onItemInfoSheetDismiss,
        )
    }
}

@ScreenPreview
@Composable
private fun DressUpScreenPreview() {
    SopkathonTheme {
        DressUpScreen(
            uiState = DressUpUiState.dummy,
            stickerState = rememberStickerCanvasState(),
            navigateUp = {},
            onCompleteClick = {},
            onItemDelete = {},
            onStickerActionClick = {},
            onPurchaseLinkClick = {},
            onItemInfoSave = {},
            onItemInfoSheetDismiss = {},
            wardrobeDrawer = { drawerModifier ->
                WardrobeDrawerContent(
                    uiState = WardrobeUiState.dummy,
                    selectedKeys = emptySet(),
                    onCategoryChange = {},
                    onExpandedChange = {},
                    onAddClick = {},
                    onClothesClick = {},
                    modifier = drawerModifier,
                )
            },
        )
    }
}
