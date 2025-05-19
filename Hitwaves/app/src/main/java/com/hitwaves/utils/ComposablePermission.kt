package com.hitwaves.utils

import android.content.pm.PackageManager
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat

enum class PermissionStatus{
    Unknown,
    Granted,
    Denied,
    PermanentlyDenied;

    val isDenied get() = this == Denied || this == PermanentlyDenied
    val isGranted get() = this == Granted
}

interface MultiplePermissionHandler{
    val statuses: Map<String, PermissionStatus>
    fun launchPermissionRequest()
}

@Composable
fun rememberMultiplePermissions(
    permissions: List<String>,
    onResult: (status: Map<String, PermissionStatus>) -> Unit
): MultiplePermissionHandler {
    val activity = LocalActivity.current!!
    var statuses by remember {
        mutableStateOf(
            permissions.associateWith { permission ->
                if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED){
                    PermissionStatus.Granted
                }
                else {
                    PermissionStatus.Unknown
                }
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { newPermission ->
        statuses = newPermission.mapValues { (permission, isGranted) ->
            when {
                isGranted -> PermissionStatus.Granted
                //l'utente ha negato il permesso ma non permanentemente -> mostrare i motivi per cui ci serve il permesso
                activity.shouldShowRequestPermissionRationale(permission) -> PermissionStatus.Denied
                else -> PermissionStatus.PermanentlyDenied
            }
        }
        onResult(statuses)
    }

    val permissionHandler = remember(permissionLauncher) {
        object : MultiplePermissionHandler{
            override val statuses: Map<String, PermissionStatus> get() = statuses

            override fun launchPermissionRequest() {
                permissionLauncher.launch(permissions.toTypedArray())
            }

        }
    }
    return permissionHandler
}