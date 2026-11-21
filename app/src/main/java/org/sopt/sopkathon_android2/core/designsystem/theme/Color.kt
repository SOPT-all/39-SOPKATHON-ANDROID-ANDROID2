package org.sopt.sopkathon_android2.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

private val Lavender1 = Color(0xFFF9EBFF)
private val Lavender2 = Color(0xFFFFFFFF)
private val Lavender3 = Color(0xFFFFFFFF)
private val Lavender4 = Color(0xFFF0D8FF)
private val Lavender5 = Color(0xFFD6A9F7)
private val Lavender6 = Color(0xFFB77BEA)
private val Lavender7 = Color(0xFF915DC4)
private val Lavender8 = Color(0xFF6E439E)
private val Lavender9 = Color(0xFF4E2D77)
private val Lavender10 = Color(0xFF331E51)

@Immutable
data class SopkathonColors(
    val lavender1: Color,
    val lavender2: Color,
    val lavender3: Color,
    val lavender4: Color,
    val lavender5: Color,
    val lavender6: Color,
    val lavender7: Color,
    val lavender8: Color,
    val lavender9: Color,
    val lavender10: Color,
)

val defaultSopkathonColors = SopkathonColors(
    lavender1 = Lavender1,
    lavender2 = Lavender2,
    lavender3 = Lavender3,
    lavender4 = Lavender4,
    lavender5 = Lavender5,
    lavender6 = Lavender6,
    lavender7 = Lavender7,
    lavender8 = Lavender8,
    lavender9 = Lavender9,
    lavender10 = Lavender10,
)
