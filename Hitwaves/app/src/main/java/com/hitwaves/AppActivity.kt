package com.hitwaves

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import com.hitwaves.ui.theme.HitwavesTheme

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HitwavesTheme {
                Text(text = "Sei nella AppActivity")
            }
        }
    }
}
