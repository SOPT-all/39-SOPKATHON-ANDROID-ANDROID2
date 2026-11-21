package org.sopt.sopkathon_android2.presentation.colleagueselect

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.preview.ScreenPreview
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme
import org.sopt.sopkathon_android2.presentation.colleagueselect.component.ColleagueSelectFilterChip
import org.sopt.sopkathon_android2.presentation.colleagueselect.component.ColleagueSelectPersonCard
import org.sopt.sopkathon_android2.presentation.colleagueselect.model.ColleagueUiModel

// 2.1 동료 선택
@Composable
fun ColleagueSelectRoute(
    onNextClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ColleagueSelectViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ColleagueSelectScreen(
        uiState = uiState,
        onPositionClick = viewModel::selectPosition,
        onColleagueClick = viewModel::toggleColleague,
        onNextClick = onNextClick,
        modifier = modifier,
    )
}

@Composable
private fun ColleagueSelectScreen(
    uiState: ColleagueSelectUiState,
    onPositionClick: (String) -> Unit,
    onColleagueClick: (Long) -> Unit,
    onNextClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val gridState = rememberLazyGridState()

    // 필터 변경 시 맨 위로
    LaunchedEffect(uiState.selectedPosition) {
        if (gridState.firstVisibleItemIndex != 0) gridState.scrollToItem(0)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SopkathonTheme.colors.white),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    text = "누구를 꾸며볼까요?",
                    style = SopkathonTheme.typography.screenTitle,
                    color = SopkathonTheme.colors.coolGray950,
                )
            }

            LazyRow(
                contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(items = uiState.positions) { position ->
                    ColleagueSelectFilterChip(
                        text = position,
                        isSelected = position == uiState.selectedPosition,
                        onClick = { onPositionClick(position) },
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                state = gridState,
                modifier = Modifier.weight(1f),
                // 하단 버튼에 마지막 카드가 가리지 않게 여백
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 120.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(items = uiState.filteredColleagues, key = { it.id }) { colleague ->
                    ColleagueSelectPersonCard(
                        name = colleague.name,
                        position = colleague.position,
                        team = colleague.team,
                        coordiCount = colleague.coordiCount,
                        isSelected = colleague.id == uiState.selectedColleagueId,
                        onClick = { onColleagueClick(colleague.id) },
                    )
                }
            }
        }

        val selectedColleague = uiState.selectedColleague

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.4f to SopkathonTheme.colors.white,
                    ),
                )
                .padding(start = 16.dp, top = 40.dp, end = 16.dp, bottom = 12.dp),
        ) {
            ColleagueSelectNextButton(
                text = if (selectedColleague != null) {
                    "${selectedColleague.name} ${selectedColleague.position}님 꾸미기"
                } else {
                    "꾸밀 동료를 선택해 주세요"
                },
                isEnabled = selectedColleague != null,
                onClick = { selectedColleague?.let { onNextClick(it.id) } },
            )
        }
    }
}

@Composable
private fun ColleagueSelectNextButton(
    text: String,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isEnabled) SopkathonTheme.colors.coolGray950 else SopkathonTheme.colors.coolGray150)
            .noRippleClickable(enabled = isEnabled, onClick = onClick)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = SopkathonTheme.typography.buttonLarge,
            color = if (isEnabled) SopkathonTheme.colors.white else SopkathonTheme.colors.coolGray300,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@ScreenPreview
@Composable
private fun ColleagueSelectScreenPreview() {
    SopkathonTheme {
        ColleagueSelectScreen(
            uiState = ColleagueSelectUiState(
                colleagues = listOf(
                    ColleagueUiModel(id = 1, name = "박정훈", position = "부장", team = "영업1팀", coordiCount = 12),
                    ColleagueUiModel(id = 2, name = "이준호", position = "사원", team = "영업1팀", coordiCount = 3),
                    ColleagueUiModel(id = 3, name = "최은지", position = "부장", team = "영업1팀", coordiCount = 8),
                    ColleagueUiModel(id = 4, name = "정태호", position = "차장", team = "영업1팀", coordiCount = 4),
                    ColleagueUiModel(id = 5, name = "한지민", position = "대리", team = "영업1팀", coordiCount = 5),
                    ColleagueUiModel(id = 6, name = "김상희", position = "부장", team = "영업2팀", coordiCount = 1),
                ),
                selectedColleagueId = 1,
            ),
            onPositionClick = {},
            onColleagueClick = {},
            onNextClick = {},
        )
    }
}

