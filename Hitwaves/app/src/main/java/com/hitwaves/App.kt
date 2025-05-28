package com.hitwaves

import android.app.Application
import com.hitwaves.api.TokenManager
import com.hitwaves.utils.NotificationAsker

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        TokenManager.init(this)
        NotificationAsker.init(this)
    }
}