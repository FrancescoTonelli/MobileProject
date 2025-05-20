package com.hitwaves

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hitwaves.api.TokenManager
import com.hitwaves.ui.screens.SplashScreen
import com.hitwaves.ui.theme.HitwavesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HitwavesTheme {
                SplashScreen()
            }
        }
    }

}