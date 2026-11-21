package org.sopt.sopkathon_android2.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import org.sopt.sopkathon_android2.R

object PretendardFont {
    val Bold = FontFamily(Font(R.font.pretendard_bold, FontWeight.Bold))
    val SemiBold = FontFamily(Font(R.font.pretendard_semibold, FontWeight.SemiBold))
    val Regular = FontFamily(Font(R.font.pretendard_regular, FontWeight.Normal))
}

object GalmuriFont {
    val Bold = FontFamily(Font(R.font.galmuri11_bold, FontWeight.Bold))
    val Regular = FontFamily(Font(R.font.galmuri11_regular, FontWeight.Normal))
    val MonoRegular = FontFamily(Font(R.font.galmurimono11_regular, FontWeight.Normal))
}

@Immutable
data class SopkathonTypography(
    val display: TextStyle,
    val headline: TextStyle,
    val screenTitle: TextStyle,
    val title: TextStyle,
    val subtitle: TextStyle,
    val itemName: TextStyle,
    val body: TextStyle,
    val bodySmall: TextStyle,
    val caption: TextStyle,
    val buttonLarge: TextStyle,
    val button: TextStyle,
    val buttonSmall: TextStyle,
    val tab: TextStyle,
    val logo: TextStyle,
    val scoreLarge: TextStyle,
    val score: TextStyle,
    val counter: TextStyle,
    val decoLarge: TextStyle,
    val deco: TextStyle,
)

private fun sopkathonTextStyle(
    fontFamily: FontFamily,
    fontSize: TextUnit,
    lineHeight: TextUnit,
    letterSpacing: TextUnit,
): TextStyle = TextStyle(
    fontFamily = fontFamily,
    fontSize = fontSize,
    lineHeight = lineHeight,
    letterSpacing = letterSpacing,
    lineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.None,
    ),
)

private fun pretendardTextStyle(
    fontFamily: FontFamily,
    fontSize: TextUnit,
    lineHeight: TextUnit,
): TextStyle = sopkathonTextStyle(
    fontFamily = fontFamily,
    fontSize = fontSize,
    lineHeight = lineHeight,
    letterSpacing = (-0.02).em,
)

// 픽셀 폰트라 자간 0 고정
private fun galmuriTextStyle(
    fontFamily: FontFamily,
    fontSize: TextUnit,
    lineHeight: TextUnit,
): TextStyle = sopkathonTextStyle(
    fontFamily = fontFamily,
    fontSize = fontSize,
    lineHeight = lineHeight,
    letterSpacing = 0.em,
)

val defaultSopkathonTypography = SopkathonTypography(
    display = pretendardTextStyle(PretendardFont.Bold, 28.sp, 36.sp),
    headline = pretendardTextStyle(PretendardFont.Bold, 24.sp, 32.sp),
    screenTitle = pretendardTextStyle(PretendardFont.Bold, 22.sp, 28.sp),
    title = pretendardTextStyle(PretendardFont.SemiBold, 22.sp, 28.sp),
    subtitle = pretendardTextStyle(PretendardFont.SemiBold, 16.sp, 24.sp),
    itemName = pretendardTextStyle(PretendardFont.SemiBold, 16.sp, 24.sp),
    body = pretendardTextStyle(PretendardFont.Regular, 16.sp, 24.sp),
    bodySmall = pretendardTextStyle(PretendardFont.Regular, 14.sp, 20.sp),
    caption = pretendardTextStyle(PretendardFont.Regular, 12.sp, 16.sp),
    buttonLarge = pretendardTextStyle(PretendardFont.Bold, 16.sp, 24.sp),
    button = pretendardTextStyle(PretendardFont.SemiBold, 14.sp, 20.sp),
    buttonSmall = pretendardTextStyle(PretendardFont.SemiBold, 12.sp, 16.sp),
    tab = pretendardTextStyle(PretendardFont.SemiBold, 14.sp, 20.sp),
    logo = galmuriTextStyle(GalmuriFont.Bold, 44.sp, 56.sp),
    scoreLarge = galmuriTextStyle(GalmuriFont.MonoRegular, 33.sp, 44.sp),
    score = galmuriTextStyle(GalmuriFont.MonoRegular, 22.sp, 30.sp),
    counter = galmuriTextStyle(GalmuriFont.Regular, 11.sp, 16.sp),
    decoLarge = galmuriTextStyle(GalmuriFont.Regular, 22.sp, 30.sp),
    deco = galmuriTextStyle(GalmuriFont.Regular, 11.sp, 16.sp),
)
