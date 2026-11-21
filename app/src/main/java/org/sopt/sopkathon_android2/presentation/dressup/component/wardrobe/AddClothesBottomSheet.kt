package org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sopt.sopkathon_android2.core.common.extensions.clearFocusOnTap
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme
import org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe.model.ClothesDraft
import org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe.model.WardrobeCategory

private val SheetScrimColor = Color.Black.copy(alpha = 0.45f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddClothesBottomSheet(
    isUploading: Boolean,
    onAddClick: (ClothesDraft) -> Unit,
    onDismiss: () -> Unit,
) {
    val state = rememberAddClothesState()
    val currentIsUploading by rememberUpdatedState(isUploading)
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isSaved ->
        state.onCameraResult(isSaved)
    }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let(state::removeBackground)
    }

    LaunchedEffect(state) { state.prepareModel() }

    ModalBottomSheet(
        onDismissRequest = {
            if (!currentIsUploading) {
                state.discard()
                onDismiss()
            }
        },
        // 업로드 중에 닫히면 요청이 취소돼 서버에만 옷이 생길 수 있어 닫히지 않게 함
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
            confirmValueChange = { it != SheetValue.Hidden || !currentIsUploading },
        ),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = SopkathonTheme.colors.coolGray50,
        scrimColor = SheetScrimColor,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .background(SopkathonTheme.colors.coolGray300, RoundedCornerShape(2.dp)),
            )
        },
    ) {
        AddClothesSheetContent(
            cutoutUri = state.cutoutUri,
            isProcessing = state.isProcessing,
            category = state.category,
            nameState = state.nameState,
            linkState = state.linkState,
            isAddEnabled = state.isAddEnabled && !isUploading,
            isUploading = isUploading,
            onCameraClick = { cameraLauncher.launch(state.newCameraUri()) },
            onGalleryClick = {
                galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            onReselectClick = state::reselect,
            onCategorySelect = { state.category = it },
            onAddClick = { state.toDraft()?.let(onAddClick) },
        )
    }
}

@Composable
private fun AddClothesSheetContent(
    cutoutUri: Uri?,
    isProcessing: Boolean,
    category: WardrobeCategory?,
    nameState: TextFieldState,
    linkState: TextFieldState,
    isAddEnabled: Boolean,
    isUploading: Boolean,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onReselectClick: () -> Unit,
    onCategorySelect: (WardrobeCategory) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SopkathonTheme.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clearFocusOnTap()
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "옷 추가하기",
                style = SopkathonTheme.typography.buttonLarge.copy(fontSize = 18.sp, lineHeight = 27.sp),
                color = colors.coolGray950,
            )
            Text(
                text = "사진을 올리면 배경을 지우고 내 옷장에 넣어 드려요.",
                style = SopkathonTheme.typography.caption,
                color = colors.coolGray500,
            )
        }

        ClothesUploader(
            cutoutUri = cutoutUri,
            isProcessing = isProcessing,
            onCameraClick = onCameraClick,
            onGalleryClick = onGalleryClick,
            onReselectClick = onReselectClick,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.white, RoundedCornerShape(16.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "카테고리",
                    style = SopkathonTheme.typography.buttonSmall,
                    color = colors.coolGray500,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    WardrobeCategory.entries.forEach {
                        CategoryChip(
                            label = it.label,
                            isSelected = it == category,
                            onClick = { onCategorySelect(it) },
                        )
                    }
                }
            }
            ClothesTextField(
                state = nameState,
                label = "아이템 이름 (선택)",
                placeholder = "예) 네이비 크루넥 니트",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            )
            ClothesTextField(
                state = linkState,
                label = "쇼핑몰 링크",
                placeholder = "https://",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Done),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(if (isAddEnabled) colors.coolGray950 else colors.coolGray150)
                .noRippleClickable(enabled = isAddEnabled, onClick = onAddClick),
            contentAlignment = Alignment.Center,
        ) {
            if (isUploading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = colors.coolGray950,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(
                    text = "옷장에 추가하기",
                    style = SopkathonTheme.typography.buttonLarge,
                    color = if (isAddEnabled) colors.white else colors.coolGray400,
                )
            }
        }
    }
}

@Composable
private fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val colors = SopkathonTheme.colors
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = Modifier
            .clip(shape)
            .background(if (isSelected) colors.coolGray950 else colors.white)
            .then(if (isSelected) Modifier else Modifier.border(1.dp, colors.coolGray150, shape))
            .noRippleClickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp),
    ) {
        Text(
            text = label,
            style = SopkathonTheme.typography.button,
            color = if (isSelected) colors.white else colors.coolGray800,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF3F5F8, widthDp = 360)
@Composable
private fun AddClothesSheetEmptyPreview() {
    WardrobePreviewTheme {
        AddClothesSheetContent(
            cutoutUri = null,
            isProcessing = false,
            category = null,
            nameState = rememberTextFieldState(),
            linkState = rememberTextFieldState(),
            isAddEnabled = false,
            isUploading = false,
            onCameraClick = {},
            onGalleryClick = {},
            onReselectClick = {},
            onCategorySelect = {},
            onAddClick = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF3F5F8, widthDp = 360)
@Composable
private fun AddClothesSheetFilledPreview() {
    WardrobePreviewTheme {
        AddClothesSheetContent(
            cutoutUri = Uri.EMPTY,
            isProcessing = false,
            category = WardrobeCategory.TOP,
            nameState = rememberTextFieldState("그레이 카디건"),
            linkState = rememberTextFieldState("https://www.musinsa.com/products/1"),
            isAddEnabled = true,
            isUploading = false,
            onCameraClick = {},
            onGalleryClick = {},
            onReselectClick = {},
            onCategorySelect = {},
            onAddClick = {},
        )
    }
}
