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
import com.hitwaves.ui.component.IconData
import com.hitwaves.ui.component.getBottomNavItems
import com.hitwaves.utils.NavGraph
import com.hitwaves.ui.theme.*
import com.hitwaves.ui.component.NotificationTopBar
import com.hitwaves.ui.component.BottomNavigationBar
import androidx.compose.runtime.Composable
import androidx.navigation.compose.currentBackStackEntryAsState

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            HitwavesTheme {
                MainContent()
            }
        }


    }
}

@Composable
fun MainContent(){
    val navController = rememberNavController()
    val bottomNavItems = getBottomNavItems()
    val notificationItem = IconData(
        route = "notifications",
        label = "Notifications",
        icon = ImageVector.vectorResource(id = R.drawable.notification_line),
        selectedIcon = ImageVector.vectorResource(id = R.drawable.notification_fill)
    )


    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    val showBars = currentRoute != "map"

    Scaffold(
        bottomBar = {
            if (showBars) {
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
            }
        },
        topBar = {
            if (showBars) {
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
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .background(BgDark)
        ) {
            NavGraph(navController = navController, innerPadding = innerPadding)
        }
    }
}