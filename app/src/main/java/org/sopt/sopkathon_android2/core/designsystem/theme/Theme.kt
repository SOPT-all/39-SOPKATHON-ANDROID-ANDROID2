package org.sopt.sopkathon_android2.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

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
    CompositionLocalProvider(
        LocalSopkathonColors provides defaultSopkathonColors,
        LocalSopkathonTypography provides defaultSopkathonTypography,
    ) {
        MaterialTheme(
            colorScheme = lightColorScheme(
                background = defaultSopkathonColors.white,
                surface = defaultSopkathonColors.white,
            ),
            content = content,
        )
    }
}
