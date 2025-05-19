package com.hitwaves

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import com.hitwaves.component.NotificationTopBar
import com.hitwaves.component.BottomNavigationBar
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


class AppActivity : ComponentActivity(), PermissionsListener {
    //private lateinit var permissionsManager: PermissionsManager
    private lateinit var locationService: LocationService

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.all { it }
        onPermissionResult(granted)
    }

    private fun requestLocationPermissions() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                            .fillMaxSize()
                    ) {
                        NavGraph(navController = navController)
                    }
                }
            }
        } else {
            //permissionsManager = PermissionsManager(this)
            requestLocationPermissions()
        }

    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        AlertDialog.Builder(this)
            .setTitle("Permissions required")
            .setMessage("The app needs location to work properly. Please grant permissions.")
            .setPositiveButton("Continue") { _, _ ->
                //permissionsManager = PermissionsManager(this)
                requestLocationPermissions()
            }
            .setNegativeButton("Denied") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableEdgeToEdge()
            setContent {
                HitwavesTheme {
                    MainContent()
                }
            }
        } else {
            AlertDialog.Builder(this)
                .setTitle("Permissions denied")
                .setMessage("Location permissions are required to use the app. You can enable them in settings.")
                .setPositiveButton("Open settings") { _, _ ->
                    locationService = LocationService(this)
                    locationService.openLocationSetting()
                }
                .setNegativeButton("Close") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
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