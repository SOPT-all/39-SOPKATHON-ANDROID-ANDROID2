package org.sopt.sopkathon_android2.presentation.upload.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.sopt.sopkathon_android2.core.common.extensions.graphemeLength
import org.sopt.sopkathon_android2.core.designsystem.component.textfield.SopkathonBasicTextField
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

private val FieldShape = RoundedCornerShape(12.dp)

/**
 * 위에 라벨, 아래 오른쪽에 글자 수가 있는 입력칸
 *
 * 빈칸은 회색 면, 입력하면 흰 면 + 선, 포커스는 검정 선
 *
 * @param maxLength 글자 수 제한. 이모지도 1자
 * @param isSingleLine false면 최소 높이 64에 3줄까지 늘어남
 */
@Composable
fun PostUploadTextField(
    state: TextFieldState,
    label: String,
    placeholder: String,
    maxLength: Int,
    modifier: Modifier = Modifier,
    isSingleLine: Boolean = true,
    imeAction: ImeAction = ImeAction.Default,
) {
    val colors = SopkathonTheme.colors
    val typography = SopkathonTheme.typography
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val inputTransformation = remember(maxLength) { GraphemeMaxLengthTransformation(maxLength) }
    val boxModifier = when {
        isFocused -> Modifier
            .background(colors.white, FieldShape)
            .border(2.dp, colors.coolGray950, FieldShape)

        state.text.isNotEmpty() -> Modifier
            .background(colors.white, FieldShape)
            .border(1.dp, colors.coolGray100, FieldShape)

        else -> Modifier.background(colors.coolGray50, FieldShape)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = label,
            style = typography.button,
            color = colors.coolGray950,
        )

        SopkathonBasicTextField(
            state = state,
            textColor = colors.coolGray950,
            textStyle = typography.body,
            placeholder = placeholder,
            placeholderColor = colors.coolGray300,
            placeholderStyle = typography.body,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = if (isSingleLine) 0.dp else 64.dp)
                .then(boxModifier)
                .padding(14.dp),
            keyboardOptions = KeyboardOptions(imeAction = imeAction),
            lineLimits = if (isSingleLine) {
                TextFieldLineLimits.SingleLine
            } else {
                TextFieldLineLimits.MultiLine(maxHeightInLines = 3)
            },
            inputTransformation = inputTransformation,
            interactionSource = interactionSource,
        )

        Text(
            text = "${state.text.graphemeLength()} / $maxLength",
            modifier = Modifier.fillMaxWidth(),
            style = typography.caption,
            color = colors.coolGray500,
            textAlign = TextAlign.End,
        )
    }
}

// InputTransformation.maxLength는 이모지를 2자 이상으로 셈
private class GraphemeMaxLengthTransformation(private val maxLength: Int) : InputTransformation {
    override fun TextFieldBuffer.transformInput() {
        if (asCharSequence().graphemeLength() > maxLength) revertAllChanges()
    }
}

@Preview(showBackground = true)
@Composable
private fun PostUploadTextFieldPreview() {
    SopkathonTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            PostUploadTextField(
                state = rememberTextFieldState("오늘의 회식룩"),
                label = "제목",
                placeholder = "제목을 입력해 주세요",
                maxLength = 20,
            )
            PostUploadTextField(
                state = rememberTextFieldState(),
                label = "한 줄 소개 (선택)",
                placeholder = "과장님께 어울릴 코디를 소개해 주세요",
                maxLength = 60,
                isSingleLine = false,
            )
        }
    }
}
