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
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.content.MediaType.Companion.Text
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.remember
import com.hitwaves.utils.LocationService

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
            modifier = Modifier
                .padding(innerPadding)
                .background(BgDark)
        ) {
            NavGraph(navController = navController)
        }
    }
}