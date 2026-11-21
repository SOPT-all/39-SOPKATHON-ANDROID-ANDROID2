package org.sopt.sopkathon_android2.data.mapper

import org.junit.Assert.assertEquals
import org.junit.Test
import org.sopt.sopkathon_android2.core.common.util.RelativeTimeFormatter
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class PostTimestampTest {
    @Test
    fun serverUtcClockWithKoreaOffsetAppearsOneMinuteAgo() {
        val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.parse("2026-11-21T21:51:19Z")!!.time
        val timestamp = "2026-11-21T21:50:00.581565+09:00".normalizePostTimestamp()
        assertEquals("1분 전", RelativeTimeFormatter.format(timestamp, now))
    }

    @Test
    fun explicitUtcAndOtherOffsetsArePreserved() {
        assertEquals("2026-11-21T21:50:00Z", "2026-11-21T21:50:00Z".normalizePostTimestamp())
        assertEquals("2026-11-21T21:50:00-05:00", "2026-11-21T21:50:00-05:00".normalizePostTimestamp())
        assertEquals("2026-11-21T21:50:00Z", "2026-11-21T21:50:00".normalizePostTimestamp())
    }
}
