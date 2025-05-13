package com.hitwaves

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.hitwaves.model.LoginNavGraph
import com.hitwaves.ui.theme.HitwavesTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HitwavesTheme {
                val navController = rememberNavController()
                LoginNavGraph(navController = navController)
            }
        }
    }
}