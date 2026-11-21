package org.sopt.sopkathon_android2.presentation.feed

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.sopt.sopkathon_android2.presentation.feed.component.FeedScope
import org.sopt.sopkathon_android2.presentation.feed.model.DummyFeedPageSource

class DummyFeedPageSourceTest {
    @Test
    fun cursorPagesHaveUniqueIdsAndTerminate() = runBlocking {
        val ids = mutableListOf<Long>()
        var cursor: String? = null
        repeat(3) { index ->
            val page = DummyFeedPageSource.getPage(FeedScope.DEPARTMENT, cursor, 5)
            assertEquals(5, page.items.size)
            ids.addAll(page.items.map { it.postId })
            cursor = page.nextCursor
            if (index < 2) assertTrue(page.hasMore) else assertFalse(page.hasMore)
        }
        assertEquals(15, ids.distinct().size)
        assertNull(cursor)
        val last = DummyFeedPageSource.getPage(FeedScope.DEPARTMENT, "15", 5)
        assertTrue(last.items.isEmpty())
        assertFalse(last.hasMore)
    }
}
