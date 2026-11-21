package org.sopt.sopkathon_android2.presentation.profilecreate.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.sopt.sopkathon_android2.R
import org.sopt.sopkathon_android2.core.common.extensions.bringIntoViewOnFocus
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.component.textfield.SopkathonBasicTextField
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

private enum class FieldState { DEFAULT, FOCUSED, FILLED, ERROR }

private fun fieldState(isError: Boolean, isFocused: Boolean, isFilled: Boolean) = when {
    isError -> FieldState.ERROR
    isFocused -> FieldState.FOCUSED
    isFilled -> FieldState.FILLED
    else -> FieldState.DEFAULT
}

/**
 * 입력 필드
 *
 * Default(회색 면) · Focused(검정 2px) · Filled(흰 면 + 선) · Error(경고색)
 *
 * @param maxLength 넘기면 길이 제한 + 오른쪽 아래 글자 수 표시
 */
@Composable
fun ProfileCreateTextField(
    state: TextFieldState,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    helper: String? = null,
    maxLength: Int? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val fieldState = fieldState(isError, isFocused, state.text.isNotEmpty())

    FieldLayout(
        fieldState = fieldState,
        helper = helper,
        counter = maxLength?.let { "${state.text.length} / $it" },
        modifier = modifier.bringIntoViewOnFocus(),
    ) {
        FieldLabel(label = label, fieldState = fieldState)
        SopkathonBasicTextField(
            state = state,
            textColor = SopkathonTheme.colors.coolGray950,
            textStyle = SopkathonTheme.typography.body,
            placeholder = placeholder,
            placeholderColor = SopkathonTheme.colors.coolGray300,
            placeholderStyle = SopkathonTheme.typography.body,
            inputTransformation = maxLength?.let { InputTransformation.maxLength(it) },
            interactionSource = interactionSource,
        )
    }
}

/**
 * 선택 필드 (▾ 포함)
 *
 * @param isExpanded 선택지가 열려 있을 때 Focused로 표시
 */
@Composable
fun FieldSelect(
    value: String,
    label: String,
    placeholder: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false,
    isError: Boolean = false,
    helper: String? = null,
) {
    val fieldState = fieldState(isError, isExpanded, value.isNotEmpty())

    FieldLayout(
        fieldState = fieldState,
        helper = helper,
        counter = null,
        modifier = modifier,
        boxModifier = Modifier.noRippleClickable(onClick = onClick),
        trailing = {
            Icon(
                painter = painterResource(R.drawable.ic_profilecreate_chevrondown_20),
                contentDescription = null,
                tint = Color.Unspecified,
            )
        },
    ) {
        FieldLabel(label = label, fieldState = fieldState)
        Text(
            text = value.ifEmpty { placeholder },
            style = SopkathonTheme.typography.body,
            color = if (value.isEmpty()) SopkathonTheme.colors.coolGray300 else SopkathonTheme.colors.coolGray950,
        )
    }
}

@Composable
private fun FieldLabel(label: String, fieldState: FieldState) {
    Text(
        text = label,
        style = SopkathonTheme.typography.caption,
        color = when (fieldState) {
            FieldState.FOCUSED -> SopkathonTheme.colors.coolGray950
            FieldState.ERROR -> SopkathonTheme.colors.red600
            else -> SopkathonTheme.colors.coolGray500
        },
    )
}

@Composable
private fun FieldLayout(
    fieldState: FieldState,
    helper: String?,
    counter: String?,
    modifier: Modifier = Modifier,
    boxModifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val colors = SopkathonTheme.colors
    val border = when (fieldState) {
        FieldState.DEFAULT -> Modifier
        FieldState.FOCUSED -> Modifier.border(2.dp, colors.coolGray950, RoundedCornerShape(12.dp))
        FieldState.FILLED -> Modifier.border(1.dp, colors.coolGray100, RoundedCornerShape(12.dp))
        FieldState.ERROR -> Modifier.border(2.dp, colors.red600, RoundedCornerShape(12.dp))
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            modifier = boxModifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (fieldState == FieldState.DEFAULT) colors.coolGray50 else colors.white)
                .then(border)
                .padding(start = 16.dp, end = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) { content() }
            trailing?.invoke()
        }

        if (helper != null || counter != null) {
            val helperColor = if (fieldState == FieldState.ERROR) colors.red600 else colors.coolGray500
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = helper.orEmpty(), style = SopkathonTheme.typography.caption, color = helperColor)
                if (counter != null) {
                    Text(text = counter, style = SopkathonTheme.typography.caption, color = helperColor)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileCreateTextFieldPreview() {
    SopkathonTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ProfileCreateTextField(state = rememberTextFieldState(), label = "이름", placeholder = "이름을 입력해 주세요", helper = "실명으로 적어 주세요", maxLength = 10)
            ProfileCreateTextField(state = rememberTextFieldState("김민지"), label = "이름", placeholder = "", isError = true, helper = "실명으로 적어 주세요", maxLength = 10)
            FieldSelect(value = "", label = "직급", placeholder = "직급을 선택해 주세요", onClick = {})
            FieldSelect(value = "대리", label = "직급", placeholder = "", onClick = {}, isExpanded = true)
        }
    }
}
