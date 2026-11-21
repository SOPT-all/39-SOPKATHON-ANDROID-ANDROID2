package org.sopt.sopkathon_android2.core.designsystem.theme

import androidx.activity.compose.LocalActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LocalSopkathonColors = staticCompositionLocalOf { defaultSopkathonColors }
private val LocalSopkathonTypography = staticCompositionLocalOf { defaultSopkathonTypography }

object SopkathonTheme {
    val colors: SopkathonColors
        @Composable
        @ReadOnlyComposable
        get() = LocalSopkathonColors.current

    val typography: SopkathonTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalSopkathonTypography.current
}

@Composable
fun SopkathonTheme(
    content: @Composable () -> Unit,
) {
    val view = LocalView.current
    val activity = LocalActivity.current

    // Preview에서는 Activity가 없어 상태바 설정 생략
    if (!view.isInEditMode) {
        SideEffect {
            activity?.window?.let { window ->
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            }
        }
    }

    CompositionLocalProvider(
        LocalSopkathonColors provides defaultSopkathonColors,
        LocalSopkathonTypography provides defaultSopkathonTypography,
    ) {
        MaterialTheme(content = content)
    }
}
