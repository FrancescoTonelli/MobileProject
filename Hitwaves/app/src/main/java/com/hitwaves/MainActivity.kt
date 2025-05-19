package com.hitwaves

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.lifecycleScope
import com.hitwaves.api.TokenManager
import com.hitwaves.api.apiAutomaticLogin
import com.hitwaves.ui.component.CustomSnackbar
import com.hitwaves.ui.screens.SplashScreen
import com.hitwaves.ui.theme.HitwavesTheme
import kotlinx.coroutines.launch

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