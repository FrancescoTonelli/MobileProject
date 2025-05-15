package com.hitwaves

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.hitwaves.utils.LoginNavGraph
import com.hitwaves.ui.theme.BgDark
import com.hitwaves.ui.theme.HitwavesTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HitwavesTheme {
                val navController = rememberNavController()
                Box(
                    modifier = Modifier
                        .background(BgDark)
                ) {
                    LoginNavGraph(navController = navController)
                }
            }
        }
    }
}