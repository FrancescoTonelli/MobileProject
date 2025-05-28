package com.hitwaves.utils

import android.content.Context
import android.content.SharedPreferences
import com.hitwaves.api.apiGetAllNotifications
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object UnreadBadge {
    private const val PREF_NAME = "notification_badge_prefs"
    private const val KEY_UNREAD = "notification_requests"
    private lateinit var prefs: SharedPreferences

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> get() = _unreadCount

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        if (!prefs.contains(KEY_UNREAD)) {
            prefs.edit().putInt(KEY_UNREAD, 0).apply()
        }
        _unreadCount.value = getUnreadCount()
    }

    suspend fun update() {
        val result = apiGetAllNotifications()

        if (result.success && result.data != null) {
            prefs.edit().putInt(KEY_UNREAD, result.data.count { it.isRead == 0 }).apply()
            _unreadCount.value = getUnreadCount()
        }
    }

    private fun getUnreadCount(): Int {
        return prefs.getInt(KEY_UNREAD, 0)
    }
}