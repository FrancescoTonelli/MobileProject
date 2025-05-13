package com.hitwaves

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.hitwaves.component.IconData
import com.hitwaves.component.getBottomNavItems
import com.hitwaves.model.NavGraph
import com.hitwaves.ui.theme.*
import com.hitwaves.component.NotificationTopBar
import com.hitwaves.component.BottomNavigationBar

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HitwavesTheme {
                val navController = rememberNavController()
                val bottomNavItems = getBottomNavItems()
                val notificationItem = IconData(
                    route = "notifications",
                    label = "Notifications",
                    icon = ImageVector.vectorResource(id = R.drawable.notification_line),
                    selectedIcon = ImageVector.vectorResource(id = R.drawable.notification_fill)
                )

                Scaffold(
                    bottomBar = {
                        Column {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Primary.copy(alpha = 0f),
                                                Primary
                                            )
                                        )
                                    )
                            )

                            BottomNavigationBar(navController = navController, items = bottomNavItems)
                        }
                    },
                    topBar = {
                        Column {
                            NotificationTopBar(navController = navController, item = notificationItem)

                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Primary,
                                                Primary.copy(alpha = 0f)
                                            )
                                        )
                                    )
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier.padding(innerPadding)
                            .background(BgDark)
                    ) {
                        NavGraph(navController = navController)
                    }
                }
            }
        }
    }
}