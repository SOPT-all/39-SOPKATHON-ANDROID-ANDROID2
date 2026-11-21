package org.sopt.sopkathon_android2.presentation.profilecreate.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import org.sopt.sopkathon_android2.R
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

/**
 * 누르면 바텀시트로 선택지가 열리는 선택 필드 (부서, 직급)
 *
 * @param title 시트 제목 (부서 선택, 직급 선택)
 * @param searchPlaceholder 넘기면 시트 상단에 검색 필드 표시
 * @param customLabel 맨 아래 직접 입력 행 문구 (기능 없음)
 */
@Composable
fun ProfileCreateSelectField(
    value: String,
    label: String,
    placeholder: String,
    title: String,
    customLabel: String,
    options: List<String>,
    onOptionSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    helper: String? = null,
    searchPlaceholder: String? = null,
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    FieldSelect(
        value = value,
        label = label,
        placeholder = placeholder,
        helper = helper,
        isExpanded = isExpanded,
        onClick = {
            // 입력 중인 필드 포커스 해제
            focusManager.clearFocus()
            isExpanded = true
        },
        modifier = modifier,
    )

    if (isExpanded) {
        ProfileCreateSelectBottomSheet(
            title = title,
            selected = value,
            options = options,
            customLabel = customLabel,
            searchPlaceholder = searchPlaceholder,
            onSelect = { option ->
                onOptionSelect(option)
                isExpanded = false
            },
            onDismiss = { isExpanded = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
private fun ProfileCreateSelectBottomSheet(
    title: String,
    selected: String,
    options: List<String>,
    customLabel: String,
    searchPlaceholder: String?,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val searchState = rememberTextFieldState()
    var query by remember { mutableStateOf("") }
    // 입력이 0.3초 멈추면 검색 완료로 보고 목록 갱신, 글자마다 시트 높이가 바뀌는 것 방지
    LaunchedEffect(searchState) {
        snapshotFlow { searchState.text.toString().trim() }
            .debounce(300)
            .collect { query = it }
    }
    val filteredOptions = if (query.isEmpty()) options else options.filter { it.contains(query) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = SopkathonTheme.colors.white,
        scrimColor = Color(0x660D0F12),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .size(width = 32.dp, height = 4.dp)
                    .background(SopkathonTheme.colors.coolGray100, RoundedCornerShape(2.dp)),
            )
        },
    ) {
        Column(modifier = Modifier.navigationBarsPadding().padding(bottom = 16.dp)) {
            Text(
                text = title,
                style = SopkathonTheme.typography.subtitle,
                color = SopkathonTheme.colors.coolGray950,
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 12.dp),
            )

            if (searchPlaceholder != null) {
                ProfileCreateTextField(
                    state = searchState,
                    label = "검색",
                    placeholder = searchPlaceholder,
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 32.dp),
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState()),
            ) {
                filteredOptions.forEach { option ->
                    SelectItem(
                        label = option,
                        isSelected = option == selected,
                        onClick = { onSelect(option) },
                    )
                }
            }

            HorizontalDivider(thickness = 1.dp, color = SopkathonTheme.colors.coolGray100)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_profilecreate_plus_20),
                    contentDescription = null,
                    tint = Color.Unspecified,
                )
                Text(
                    text = customLabel,
                    style = SopkathonTheme.typography.subtitle,
                    color = SopkathonTheme.colors.coolGray950,
                )
            }
        }
    }
}

@Composable
private fun SelectItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(if (isSelected) SopkathonTheme.colors.coolGray50 else SopkathonTheme.colors.white)
            .noRippleClickable(onClick = onClick)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = if (isSelected) SopkathonTheme.typography.buttonLarge else SopkathonTheme.typography.body,
            color = SopkathonTheme.colors.coolGray950,
            modifier = Modifier.weight(1f),
        )
        if (isSelected) {
            Icon(
                painter = painterResource(R.drawable.ic_profilecreate_check_20),
                contentDescription = null,
                tint = Color.Unspecified,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileCreateSelectFieldPreview() {
    SopkathonTheme {
        ProfileCreateSelectField(
            value = "",
            label = "직급",
            placeholder = "직급을 선택해 주세요",
            title = "직급 선택",
            customLabel = "찾는 직급이 없나요? 직접 입력하기",
            options = listOf("사원", "대리"),
            onOptionSelect = {},
        )
    }
}
