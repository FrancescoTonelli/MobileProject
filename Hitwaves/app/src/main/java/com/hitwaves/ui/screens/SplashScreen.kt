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
import kotlinx.coroutines.delay

private fun init(): SplashScreenViewModel = SplashScreenViewModel()

sealed class LocationPermissionState {
    object Unknown : LocationPermissionState()
    object Requesting : LocationPermissionState()
    object NeedsRationale : LocationPermissionState()
    object DeniedPermanently : LocationPermissionState()
    object Granted : LocationPermissionState()
}

@Composable
fun SplashScreen() {
    val context = LocalContext.current
    val activity = context as Activity
    val splashViewModel = remember { init() }
    val result by splashViewModel.autoLoginState
    val snackBarHostState = remember { SnackbarHostState() }

    var permissionState by remember { mutableStateOf<LocationPermissionState>(LocationPermissionState.Unknown) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var comingFromSettings by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val hasFine = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val hasCoarse = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (hasFine || hasCoarse) {
            permissionState = LocationPermissionState.Granted
        } else {
            val showRationaleFine = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)
            val showRationaleCoarse = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
            if (!showRationaleFine && !showRationaleCoarse) {
                permissionState = LocationPermissionState.DeniedPermanently
            } else {
                permissionState = LocationPermissionState.NeedsRationale
            }
        }
    }

    LaunchedEffect(Unit) {
        val hasFineNow = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarseNow = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (hasFineNow || hasCoarseNow) {
            permissionState = LocationPermissionState.Granted
        } else {
            permissionState = LocationPermissionState.Requesting
            permissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    LaunchedEffect(permissionState) {
        when (permissionState) {
            is LocationPermissionState.Granted -> {
                if (TokenManager.getToken().isNullOrEmpty()) {
                    context.startActivity(Intent(context, LoginActivity::class.java))
                    activity.finish()
                } else {
                    splashViewModel.handleSplash()
                }
            }
            is LocationPermissionState.NeedsRationale -> {
                delay(300)
                permissionState = LocationPermissionState.Requesting
                permissionLauncher.launch(arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ))
            }
            is LocationPermissionState.DeniedPermanently -> {
                showSettingsDialog = true
            }
            else -> { }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (comingFromSettings) {
                    comingFromSettings = false
                    permissionState = LocationPermissionState.Requesting
                    permissionLauncher.launch(arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ))
                } else {
                    val hasFineNow = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    val hasCoarseNow = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    if (hasFineNow || hasCoarseNow) {
                        permissionState = LocationPermissionState.Granted
                    } else {
                        val showRationaleFine = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                        val showRationaleCoarse = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                        permissionState = if (showRationaleFine || showRationaleCoarse) {
                            LocationPermissionState.NeedsRationale
                        } else {
                            LocationPermissionState.DeniedPermanently
                        }
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(result) {
        if (permissionState !is LocationPermissionState.Granted) return@LaunchedEffect
        if (TokenManager.getToken().isNullOrEmpty()) {
            context.startActivity(Intent(context, LoginActivity::class.java))
            activity.finish()
        } else if (result.success && result.data != null) {
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
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "Hitwaves",
                style = Typography.titleLarge.copy(
                    fontSize = 48.sp,
                    color = Secondary,
                    drawStyle = Stroke(width = 4f)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
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
                comingFromSettings = true
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
