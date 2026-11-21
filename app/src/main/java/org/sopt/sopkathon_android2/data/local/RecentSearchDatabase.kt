package org.sopt.sopkathon_android2.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

internal class RecentSearchDatabase(
    context: Context,
    name: String = "recent_search.db",
) : SQLiteOpenHelper(context.applicationContext, name, null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE recent_search (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                query TEXT NOT NULL COLLATE NOCASE UNIQUE
            )
            """.trimIndent(),
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit

    fun queries(): List<String> = readableDatabase.rawQuery(
        "SELECT query FROM recent_search ORDER BY id DESC",
        null,
    ).use { cursor ->
        buildList {
            while (cursor.moveToNext()) add(cursor.getString(0))
        }
    }

    fun record(query: String) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val values = ContentValues().apply { put("query", query) }
            db.insertWithOnConflict("recent_search", null, values, SQLiteDatabase.CONFLICT_REPLACE)
            db.execSQL(
                """
                DELETE FROM recent_search WHERE id NOT IN (
                    SELECT id FROM recent_search ORDER BY id DESC LIMIT $MAX_RECENT_QUERIES
                )
                """.trimIndent(),
            )
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun remove(query: String) {
        writableDatabase.delete("recent_search", "query = ?", arrayOf(query))
    }

    fun clear() {
        writableDatabase.delete("recent_search", null, null)
    }

    private companion object {
        const val MAX_RECENT_QUERIES = 5
    }
}
