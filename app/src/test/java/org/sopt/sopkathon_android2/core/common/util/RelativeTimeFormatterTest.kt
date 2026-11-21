package org.sopt.sopkathon_android2.core.common.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.TimeZone

class RelativeTimeFormatterTest {
    private val now = 1_700_000_000_000L
    private val utc = TimeZone.getTimeZone("UTC")

    private fun formatAgo(seconds: Long): String {
        val timestamp = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").apply {
            timeZone = utc
        }.format(java.util.Date(now - seconds * 1000))
        return RelativeTimeFormatter.format(timestamp, now, displayTimeZone = utc)
    }

    @Test
    fun relativeTimeBoundaries() {
        assertEquals("방금 전", formatAgo(59))
        assertEquals("1분 전", formatAgo(60))
        assertEquals("59분 전", formatAgo(3599))
        assertEquals("1시간 전", formatAgo(3600))
        assertEquals("1일 전", formatAgo(86400))
        assertEquals("6일 전", formatAgo(6 * 86400))
        assertEquals("2023.11.07", formatAgo(7 * 86400))
        assertEquals("방금 전", formatAgo(-60))
    }

    @Test
    fun offsetsRepresentSameInstant() {
        val seoul = RelativeTimeFormatter.format("2023-11-15T06:13:20+09:00", now)
        val utcTime = RelativeTimeFormatter.format("2023-11-14T21:13:20.123456789Z", now)
        assertEquals("1시간 전", seoul)
        assertEquals("59분 전", utcTime)
    }

    @Test
    fun missingTimeZoneRequiresExplicitServerZone() {
        assertEquals("", RelativeTimeFormatter.format("2023-11-14T21:13:20", now))
        assertEquals("1시간 전", RelativeTimeFormatter.format("2023-11-14T21:13:20", now, utc))
    }

    @Test
    fun invalidTimestampDoesNotCrashOrAppearRecent() {
        for (value in listOf("", "invalid", "2023-02-30T00:00:00Z", "2023-11-14T25:00:00Z")) {
            assertEquals("", RelativeTimeFormatter.format(value, now))
        }
    }
}
