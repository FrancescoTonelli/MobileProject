package com.hitwaves.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.hitwaves.AppActivity
import com.hitwaves.LoginActivity
import com.hitwaves.R
import com.hitwaves.api.TokenManager
import com.hitwaves.ui.component.CustomMessageBox
import com.hitwaves.ui.component.CustomSnackBar
import com.hitwaves.ui.component.IconData
import com.hitwaves.ui.theme.*
import com.hitwaves.ui.viewModel.SplashScreenViewModel

private fun init(): SplashScreenViewModel = SplashScreenViewModel()

@Composable
fun SplashScreen() {
    val context = LocalContext.current
    val activity = context as Activity
    val splashViewModel = remember { SplashScreenViewModel() }
    val result by splashViewModel.autoLoginState
    val snackBarHostState = remember { SnackbarHostState() }

    var permissionChecked by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    val pendingPermissionRequest = remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val hasResumedOnce = remember { mutableStateOf(false) }


    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        permissionChecked = true
        if (!granted) {
            val showRationaleFine = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)
            val showRationaleCoarse = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
            if (!showRationaleFine && !showRationaleCoarse) {
                showSettingsDialog = true
            } else {
                pendingPermissionRequest.value = true
            }
        } else {
            if (TokenManager.getToken().isNullOrEmpty()) {
                context.startActivity(Intent(context, LoginActivity::class.java))
                activity.finish()
            } else {
                splashViewModel.handleSplash()
            }
        }
    }

    LaunchedEffect(pendingPermissionRequest.value) {
        if (pendingPermissionRequest.value) {
            pendingPermissionRequest.value = false
            permissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    LaunchedEffect(Unit) {
        val hasFine = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (hasFine || hasCoarse) {
            if (TokenManager.getToken().isNullOrEmpty()) {
                context.startActivity(Intent(context, LoginActivity::class.java))
                activity.finish()
            } else {
                permissionChecked = true
                splashViewModel.handleSplash()
            }
        } else {
            permissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (hasResumedOnce.value) {
                    if (!permissionChecked) {
                        val hasFine = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        val hasCoarse = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

                        if (hasFine || hasCoarse) {
                            if (TokenManager.getToken().isNullOrEmpty()) {
                                context.startActivity(Intent(context, LoginActivity::class.java))
                                activity.finish()
                            } else {
                                permissionChecked = true
                                splashViewModel.handleSplash()
                            }
                        } else {
                            pendingPermissionRequest.value = true
                        }
                    }
                } else {
                    hasResumedOnce.value = true
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    LaunchedEffect(result, permissionChecked) {
        if (!permissionChecked) return@LaunchedEffect

        if(TokenManager.getToken().isNullOrEmpty()) {
            context.startActivity(Intent(context, LoginActivity::class.java))
            activity.finish()
        }

        if (result.success && result.data != null) {
            TokenManager.saveToken(result.data!!.token)
            context.startActivity(Intent(context, AppActivity::class.java))
            activity.finish()
        } else if (!result.success && result.errorMessage != null) {
            snackBarHostState.showSnackbar(result.errorMessage!!)
            context.startActivity(Intent(context, LoginActivity::class.java))
            activity.finish()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val logo = IconData(
                route = "logo",
                label = "Logo",
                icon = ImageVector.vectorResource(id = R.drawable.logo)
            )

            Icon(
                imageVector = logo.icon,
                contentDescription = "Logo",
                modifier = Modifier.size(140.dp),
                tint = Color.White
            )

            Spacer(modifier = Modifier.size(28.dp))

            Text(
                text = "Hitwaves",
                style = Typography.titleLarge.copy(
                    fontSize = 48.sp,
                    color = Secondary,
                    drawStyle = Stroke(width = 4f)
                )
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = "Find your perfect wave!",
                style = Typography.labelSmall.copy(
                    fontSize = 20.sp,
                    color = Secondary
                ),
                modifier = Modifier.padding(bottom = 40.dp)
            )

            CircularProgressIndicator(
                color = Primary,
                strokeWidth = 4.dp
            )
        }
    }

    if (showSettingsDialog) {
        CustomMessageBox(
            title = "Permission denied",
            message = "To use the app, you must allow location access. Do you want to open the app settings?",
            onConfirm = {
                showSettingsDialog = false
                permissionChecked = false
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            },
            onDismiss = {
                activity.finish()
            }
        )
    }

    CustomSnackBar(snackBarHostState)
}
