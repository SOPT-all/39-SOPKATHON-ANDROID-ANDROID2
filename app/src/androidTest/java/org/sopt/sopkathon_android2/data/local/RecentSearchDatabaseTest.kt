package org.sopt.sopkathon_android2.data.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecentSearchDatabaseTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun resetDatabase() {
        context.deleteDatabase(TEST_DATABASE)
    }

    @After
    fun removeDatabase() {
        context.deleteDatabase(TEST_DATABASE)
    }

    @Test
    fun retainsFiveNewestQueriesAcrossReopenAndPromotesDuplicates() {
        RecentSearchDatabase(context, TEST_DATABASE).use { database ->
            listOf("부장", "박정훈", "과장", "대리", "사원", "이준호").forEach(database::record)
            assertEquals(listOf("이준호", "사원", "대리", "과장", "박정훈"), database.queries())
            database.record("과장")
        }

        RecentSearchDatabase(context, TEST_DATABASE).use { database ->
            assertEquals(listOf("과장", "이준호", "사원", "대리", "박정훈"), database.queries())
            database.remove("사원")
            assertEquals(listOf("과장", "이준호", "대리", "박정훈"), database.queries())
            database.clear()
            assertEquals(emptyList<String>(), database.queries())
        }
    }

    private companion object {
        const val TEST_DATABASE = "recent_search_test.db"
    }
}
