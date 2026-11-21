package org.sopt.sopkathon_android2.core.common.util

import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object RelativeTimeFormatter {
    private val timestampPattern = Regex(
        "^(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2})(?:\\.(\\d{1,9}))?(Z|[+-]\\d{2}:\\d{2})?$",
    )

    fun format(
        createdAt: String,
        nowMillis: Long = System.currentTimeMillis(),
        serverTimeZone: TimeZone? = null,
        displayTimeZone: TimeZone = TimeZone.getDefault(),
    ): String {
        val createdAtMillis = parse(createdAt, serverTimeZone) ?: return ""
        val elapsedMillis = if (createdAtMillis >= nowMillis) 0L else nowMillis - createdAtMillis
        val minutes = elapsedMillis / 60_000L
        val hours = minutes / 60L
        val days = hours / 24L

        return when {
            minutes < 1L -> "방금 전"
            minutes < 60L -> "${minutes}분 전"
            hours < 24L -> "${hours}시간 전"
            days < 7L -> "${days}일 전"
            else -> SimpleDateFormat("yyyy.MM.dd", Locale.KOREAN).apply {
                timeZone = displayTimeZone
            }.format(Date(createdAtMillis))
        }
    }

    private fun parse(createdAt: String, serverTimeZone: TimeZone?): Long? {
        val match = timestampPattern.matchEntire(createdAt) ?: return null
        val (dateTime, fraction, offset) = match.destructured
        // 시간대 없는 서버 응답을 기기 시간대로 임의 해석하지 않도록 명시적 설정 요구
        if (offset.isEmpty() && serverTimeZone == null) return null
        val milliseconds = fraction.padEnd(3, '0').take(3)
        val normalized = "$dateTime.$milliseconds$offset"
        val pattern = if (offset.isEmpty()) "yyyy-MM-dd'T'HH:mm:ss.SSS" else "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
        val parser = SimpleDateFormat(pattern, Locale.US).apply {
            isLenient = false
            timeZone = serverTimeZone ?: TimeZone.getTimeZone("UTC")
        }
        val position = ParsePosition(0)
        return parser.parse(normalized, position)
            ?.takeIf { position.index == normalized.length }
            ?.time
    }
}
