package com.hitwaves.utils

import android.content.Context
import android.content.SharedPreferences

object NotificationAsker {
    private const val PREF_NAME = "notification_prefs"
    private const val KEY_REQUESTS = "notification_requests"
    private const val KEY_BLOCKED = "notification_blocked"
    private const val MAX_REQUESTS = 2
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        if (!prefs.contains(KEY_REQUESTS)) {
            prefs.edit().putInt(KEY_REQUESTS, 0).apply()
        }
    }

    fun incrementRequestCount() {
        val current = getRequestCount()
        prefs.edit().putInt(KEY_REQUESTS, current + 1).apply()
        if (getRequestCount() >= MAX_REQUESTS) {
            blockPermissionRequest()
        }
    }

    private fun getRequestCount(): Int {
        return prefs.getInt(KEY_REQUESTS, 0)
    }

    fun isBlocked(): Boolean {
        return prefs.getBoolean(KEY_BLOCKED, false)
    }

    fun blockPermissionRequest() {
        prefs.edit().putBoolean(KEY_BLOCKED, true).apply()
    }

}