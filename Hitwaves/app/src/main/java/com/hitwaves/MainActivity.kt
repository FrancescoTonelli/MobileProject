package com.hitwaves

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.hitwaves.api.TokenManager
import com.hitwaves.api.apiAutomaticLogin
import com.hitwaves.ui.screens.SplashScreen
import com.hitwaves.ui.theme.HitwavesTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appDebug = false

        setContent {
            HitwavesTheme {
                SplashScreen()
            }
        }

        if (appDebug) {
            val intent = Intent(this@MainActivity, AppActivity::class.java)
            startActivity(intent)
            finish()
        }
        else {
            val token = TokenManager.getToken()

            if (token != null) {
                lifecycleScope.launch {
                    val (success, response) = apiAutomaticLogin()
                    if (success) {
                        val intent = Intent(this@MainActivity, AppActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            } else {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }


    }

}