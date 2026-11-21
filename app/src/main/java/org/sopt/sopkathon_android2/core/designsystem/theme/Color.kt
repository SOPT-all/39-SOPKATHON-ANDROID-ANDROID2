package org.sopt.sopkathon_android2.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

private val White = Color(0xFFFFFFFF)
private val CoolGray25 = Color(0xFFF9FAFB)
private val CoolGray50 = Color(0xFFF3F5F8)
private val CoolGray100 = Color(0xFFEBEDF0)
private val CoolGray150 = Color(0xFFE0E3E7)
private val CoolGray200 = Color(0xFFD4D8DC)
private val CoolGray300 = Color(0xFFBABEC4)
private val CoolGray400 = Color(0xFF9A9FA5)
private val CoolGray500 = Color(0xFF7C8187)
private val CoolGray600 = Color(0xFF5F646A)
private val CoolGray700 = Color(0xFF44484D)
private val CoolGray800 = Color(0xFF2D3135)
private val CoolGray900 = Color(0xFF1B1D20)
private val CoolGray950 = Color(0xFF0E1012)
private val Red600 = Color(0xFFD92D20)
private val Green50 = Color(0xFFE8F6EE)
private val Green600 = Color(0xFF1E9E5A)
private val Pink50 = Color(0xFFFFEDF2)
private val Pink400 = Color(0xFFFF5C8A)
private val Pink500 = Color(0xFFE0457B)

@Immutable
data class SopkathonColors(
    val white: Color,
    val coolGray25: Color,
    val coolGray50: Color,
    val coolGray100: Color,
    val coolGray150: Color,
    val coolGray200: Color,
    val coolGray300: Color,
    val coolGray400: Color,
    val coolGray500: Color,
    val coolGray600: Color,
    val coolGray700: Color,
    val coolGray800: Color,
    val coolGray900: Color,
    val coolGray950: Color,
    val red600: Color,
    val green50: Color,
    val green600: Color,
    val pink50: Color,
    val pink400: Color,
    val pink500: Color,
)

val defaultSopkathonColors = SopkathonColors(
    white = White,
    coolGray25 = CoolGray25,
    coolGray50 = CoolGray50,
    coolGray100 = CoolGray100,
    coolGray150 = CoolGray150,
    coolGray200 = CoolGray200,
    coolGray300 = CoolGray300,
    coolGray400 = CoolGray400,
    coolGray500 = CoolGray500,
    coolGray600 = CoolGray600,
    coolGray700 = CoolGray700,
    coolGray800 = CoolGray800,
    coolGray900 = CoolGray900,
    coolGray950 = CoolGray950,
    red600 = Red600,
    green50 = Green50,
    green600 = Green600,
    pink50 = Pink50,
    pink400 = Pink400,
    pink500 = Pink500,
)
