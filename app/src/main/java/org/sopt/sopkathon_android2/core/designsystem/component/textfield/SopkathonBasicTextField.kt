package org.sopt.sopkathon_android2.core.designsystem.component.textfield

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

/**
 * 장식 없는 기본 입력 영역
 *
 * [ProfileCreateTextField] 같은 텍스트 필드 UI 안에서 입력 영역으로 사용
 *
 * @param state 텍스트, 커서, 선택 상태
 * @param isReadOnly 입력 불가, 선택·복사는 가능
 * @param inputTransformation 입력 변환 (길이 제한, 문자 필터링)
 * @param outputTransformation 표시 변환 (포맷팅)
 * @param interactionSource 포커스 등 상태 수집
 * @param suffix 오른쪽 끝 요소 (아이콘, 버튼 등)
 */
@Composable
fun SopkathonBasicTextField(
    state: TextFieldState,
    textColor: Color,
    textStyle: TextStyle,
    placeholder: String,
    placeholderColor: Color,
    placeholderStyle: TextStyle,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    isReadOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.SingleLine,
    inputTransformation: InputTransformation? = null,
    outputTransformation: OutputTransformation? = null,
    interactionSource: MutableInteractionSource? = null,
    cursorColor: Color = textColor,
    suffix: (@Composable () -> Unit)? = null,
) {
    val internalInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val isFocused by internalInteractionSource.collectIsFocusedAsState()

    BasicTextField(
        state = state,
        modifier = modifier,
        enabled = isEnabled,
        readOnly = isReadOnly,
        textStyle = textStyle.copy(color = textColor),
        keyboardOptions = keyboardOptions,
        onKeyboardAction = onKeyboardAction,
        lineLimits = lineLimits,
        inputTransformation = inputTransformation,
        outputTransformation = outputTransformation,
        interactionSource = internalInteractionSource,
        cursorBrush = SolidColor(cursorColor),
        decorator = { innerTextField ->
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (!isFocused && state.text.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = placeholderColor,
                            style = placeholderStyle,
                        )
                    }
                    innerTextField()
                }
                suffix?.invoke()
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun SopkathonBasicTextFieldPreview() {
    SopkathonTheme {
        SopkathonBasicTextField(
            state = rememberTextFieldState(),
            textColor = SopkathonTheme.colors.coolGray950,
            textStyle = SopkathonTheme.typography.body,
            placeholder = "이름을 입력해 주세요",
            placeholderColor = SopkathonTheme.colors.coolGray300,
            placeholderStyle = SopkathonTheme.typography.body,
        )
    }
}
