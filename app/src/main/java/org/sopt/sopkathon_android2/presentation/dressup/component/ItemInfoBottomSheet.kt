package org.sopt.sopkathon_android2.presentation.dressup.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.drop
import org.sopt.sopkathon_android2.R
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme
import org.sopt.sopkathon_android2.presentation.dressup.model.ClothesCategory
import org.sopt.sopkathon_android2.presentation.dressup.model.ItemInfoInput
import org.sopt.sopkathon_android2.presentation.dressup.model.WornItemUiModel

private const val LINK_PREFIX = "https://"

private class ItemInfoDraft(val item: WornItemUiModel) {
    val name = TextFieldState(item.name)
    val link = TextFieldState(item.link)

    val isComplete: Boolean get() = name.text.isNotBlank() && link.text.isNotBlank()

    val hasLinkError: Boolean
        get() = link.text.trim().let { it.isNotEmpty() && !it.startsWith(LINK_PREFIX) }

    fun toInput() = ItemInfoInput(
        key = item.key,
        name = name.text.trim().toString(),
        link = link.text.trim().toString(),
    )
}

/**
 * 입힌 옷마다 이름과 쇼핑몰 링크를 받는 시트
 *
 * 저장할 때 링크가 https://로 시작하지 않으면 그 칸에 오류를 보이고 닫지 않음
 *
 * @param items 시트에 보일 옷. 칩으로 열면 선택한 옷 하나, 구매 링크 버튼으로 열면 입힌 옷 전체
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemInfoBottomSheet(
    items: List<WornItemUiModel>,
    onSaveClick: (List<ItemInfoInput>) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val colors = SopkathonTheme.colors

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = colors.coolGray50,
        scrimColor = colors.coolGray950.copy(alpha = 0.45f),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 12.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .background(colors.coolGray300, RoundedCornerShape(2.dp)),
            )
        },
    ) {
        ItemInfoSheetContent(
            items = items,
            onSaveClick = onSaveClick,
            modifier = Modifier.navigationBarsPadding(),
        )
    }
}

@Composable
private fun ColumnScope.ItemInfoSheetContent(
    items: List<WornItemUiModel>,
    onSaveClick: (List<ItemInfoInput>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val drafts = remember(items) { items.map(::ItemInfoDraft) }
    var errorKeys by remember(items) { mutableStateOf(emptySet<String>()) }

    Column(
        modifier = modifier
            .weight(1f, fill = false)
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "입힌 아이템 정보",
                style = SopkathonTheme.typography.buttonLarge.copy(fontSize = 18.sp, lineHeight = 27.sp),
                color = SopkathonTheme.colors.coolGray950,
            )
            Text(
                text = "아이템 이름과 링크는 피드의 아이템 상세에 함께 보여요.",
                style = SopkathonTheme.typography.caption,
                color = SopkathonTheme.colors.coolGray500,
            )
        }

        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            drafts.forEach { draft ->
                ItemInfoCard(
                    draft = draft,
                    isLinkError = draft.item.key in errorKeys,
                    onLinkEdit = { errorKeys = errorKeys - draft.item.key },
                )
            }
        }

        DressUpFilledButton(
            text = "저장하기",
            onClick = {
                errorKeys = drafts.filter { it.hasLinkError }.map { it.item.key }.toSet()
                if (errorKeys.isEmpty()) onSaveClick(drafts.map { it.toInput() })
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ItemInfoCard(
    draft: ItemInfoDraft,
    isLinkError: Boolean,
    onLinkEdit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SopkathonTheme.colors
    val cardShape = RoundedCornerShape(16.dp)

    // 오류가 난 링크를 고치기 시작하면 오류 문구를 내림
    LaunchedEffect(draft) {
        snapshotFlow { draft.link.text.toString() }
            .drop(1)
            .collect { onLinkEdit() }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(colors.white)
            .border(1.dp, colors.coolGray150, cardShape)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = draft.item.imageUri,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(colors.coolGray50),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = WornItemUiModel.BRAND,
                    style = SopkathonTheme.typography.buttonSmall.copy(fontSize = 11.sp, lineHeight = 16.sp),
                    color = colors.coolGray500,
                )
                Text(
                    text = draft.name.text.toString().ifBlank { draft.item.category.label },
                    style = SopkathonTheme.typography.buttonLarge.copy(fontSize = 14.sp, lineHeight = 20.sp),
                    color = colors.coolGray950,
                    maxLines = 1,
                )
            }
            ItemInfoStatusChip(isComplete = draft.isComplete)
        }
        ItemInfoTextField(
            state = draft.name,
            label = "아이템 이름",
            placeholder = "아이템 이름을 입력해 주세요",
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )
        ItemInfoTextField(
            state = draft.link,
            label = "쇼핑몰 링크 (선택)",
            placeholder = "링크를 붙여 넣어 주세요",
            error = if (isLinkError) "https:// 로 시작하는 링크를 붙여 넣어 주세요" else null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Done),
        )
    }
}

@Composable
private fun ItemInfoStatusChip(
    isComplete: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = SopkathonTheme.colors

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isComplete) colors.green50 else colors.pink50)
            .padding(start = 8.dp, end = 10.dp, top = 4.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isComplete) {
            Icon(
                painter = painterResource(R.drawable.ic_dressup_check_12),
                contentDescription = null,
                tint = Color.Unspecified,
            )
        }
        Text(
            text = if (isComplete) "완료" else "미입력",
            style = SopkathonTheme.typography.buttonSmall.copy(fontSize = 11.sp, lineHeight = 13.sp),
            color = if (isComplete) colors.green600 else colors.pink500,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF3F5F8, heightDp = 640)
@Composable
private fun ItemInfoSheetContentPreview() {
    SopkathonTheme {
        Column {
            ItemInfoSheetContent(
                items = listOf(
                    WornItemUiModel(
                        key = "top",
                        category = ClothesCategory.TOP,
                        name = "네이비 크루넥 니트",
                        link = "https://uniqlo.com/kr/navy-knit",
                    ),
                    WornItemUiModel(key = "bottom", category = ClothesCategory.BOTTOM),
                ),
                onSaveClick = {},
            )
        }
    }
}
