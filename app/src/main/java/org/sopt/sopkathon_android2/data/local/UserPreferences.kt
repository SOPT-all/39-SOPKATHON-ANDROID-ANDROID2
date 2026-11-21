package org.sopt.sopkathon_android2.data.local

import android.content.Context

// 프로필 생성 때 서버에서 받은 userId 보관
internal class UserPreferences(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences("user", Context.MODE_PRIVATE)

    var userId: Long?
        get() = preferences.getLong(KEY_USER_ID, 0L).takeIf { it > 0 }
        set(value) {
            preferences.edit().apply {
                if (value == null) remove(KEY_USER_ID) else putLong(KEY_USER_ID, value)
            }.apply()
        }

    private companion object {
        const val KEY_USER_ID = "user_id"
    }
}
