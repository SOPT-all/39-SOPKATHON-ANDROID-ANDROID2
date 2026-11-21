package org.sopt.sopkathon_android2.presentation.dressup.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sopt.sopkathon_android2.core.designsystem.component.textfield.SopkathonBasicTextField
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

private val FieldShape = RoundedCornerShape(12.dp)

/**
 * 아이템 정보 시트 입력칸. 라벨과 오류 문구가 박스 안에 들어감
 *
 * 빈 칸(회색 면) · 포커스(검정 1.5dp) · 채움(흰 면 + 선) · 오류(빨강 1dp)
 *
 * @param error null이 아니면 오류 상태로 값 아래에 표시
 */
@Composable
fun ItemInfoTextField(
    state: TextFieldState,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    val colors = SopkathonTheme.colors
    val typography = SopkathonTheme.typography
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isEmpty = state.text.isEmpty()

    val border = when {
        error != null -> Modifier.border(1.dp, colors.red600, FieldShape)
        isFocused -> Modifier.border(1.5.dp, colors.coolGray950, FieldShape)
        isEmpty -> Modifier
        else -> Modifier.border(1.dp, colors.coolGray150, FieldShape)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(FieldShape)
            .background(if (isEmpty && !isFocused && error == null) colors.coolGray50 else colors.white)
            .then(border)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = label,
            style = typography.caption.copy(fontSize = 11.sp, lineHeight = 16.sp),
            color = colors.coolGray500,
        )
        SopkathonBasicTextField(
            state = state,
            textColor = colors.coolGray950,
            textStyle = typography.bodySmall,
            placeholder = placeholder,
            placeholderColor = colors.coolGray300,
            placeholderStyle = typography.bodySmall,
            keyboardOptions = keyboardOptions,
            interactionSource = interactionSource,
            modifier = Modifier.fillMaxWidth(),
        )
        if (error != null) {
            Text(
                text = error,
                style = typography.caption,
                color = colors.red600,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ItemInfoTextFieldPreview() {
    SopkathonTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            ItemInfoTextField(state = rememberTextFieldState(), label = "아이템 이름", placeholder = "아이템 이름을 입력해 주세요")
            ItemInfoTextField(state = rememberTextFieldState("네이비 크루넥 니트"), label = "아이템 이름", placeholder = "")
            ItemInfoTextField(
                state = rememberTextFieldState("H&M 데님 바지"),
                label = "쇼핑몰 링크",
                placeholder = "링크를 붙여 넣어 주세요",
                error = "https:// 로 시작하는 링크를 붙여 넣어 주세요",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            )
        }
    }
}
