package com.hitwaves

import android.app.Application
import com.hitwaves.api.TokenManager

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        TokenManager.init(this)
    }
}