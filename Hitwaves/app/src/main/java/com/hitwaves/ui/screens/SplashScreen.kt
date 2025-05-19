package com.hitwaves.ui.screens

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hitwaves.AppActivity
import com.hitwaves.LoginActivity
import com.hitwaves.R
import com.hitwaves.api.ApiResult
import com.hitwaves.api.TokenManager
import com.hitwaves.api.TokenResponse
import com.hitwaves.ui.component.CustomSnackbar
import com.hitwaves.ui.component.IconData
import com.hitwaves.ui.theme.*
import com.hitwaves.ui.viewModel.SplashScreenViewModel
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.hitwaves.ui.component.CustomMessageBox

private fun init() : SplashScreenViewModel {
    return SplashScreenViewModel()
}

@Composable
fun SplashScreen() {

    val splashViewModel = remember { init() }
    val result: ApiResult<TokenResponse> by splashViewModel.autoLoginState
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val activity = context as Activity

    var showSettingsDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val token = TokenManager.getToken()
            if (token != null) {
                splashViewModel.handleSplash()
            } else {
                context.startActivity(Intent(context, LoginActivity::class.java))
                activity.finish()
            }
        } else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                showSettingsDialog = true
            }
        }
    }

    LaunchedEffect(Unit) {
        val permission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (permission == PackageManager.PERMISSION_GRANTED) {
            val token = TokenManager.getToken()
            if (token != null) {
                splashViewModel.handleSplash()
            } else {
                context.startActivity(Intent(context, LoginActivity::class.java))
                activity.finish()
            }
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(result) {
        if (result.success && result.data != null) {
            TokenManager.saveToken(result.data!!.token)
            context.startActivity(Intent(context, AppActivity::class.java))
            activity.finish()
        } else if (!result.success && result.errorMessage != null) {
            snackbarHostState.showSnackbar(result.errorMessage!!)
            context.startActivity(Intent(context, LoginActivity::class.java))
            activity.finish()
        }
    }

    LaunchedEffect(permissionLauncher) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            showSettingsDialog = true
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
            message = "To use the app, you must allow location access. Do you want to log out and open the settings?",
            onConfirm = {
                showSettingsDialog = false
                val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = android.net.Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                context.startActivity(intent)
            },
            onDismiss = {
                showSettingsDialog = false
                activity.finish()
            }
        )
    }

    CustomSnackbar(snackbarHostState)
}
