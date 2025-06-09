package com.hitwaves.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.hitwaves.ui.component.CustomSnackBar
import com.hitwaves.ui.component.NotificationCard
import com.hitwaves.ui.component.LoadingIndicator
import com.hitwaves.ui.theme.*
import com.hitwaves.ui.viewModel.NotificationViewModel
import com.hitwaves.utils.UnreadBadge

private fun init() : NotificationViewModel {
    return NotificationViewModel()
}

@Composable
fun Notification(navController: NavHostController) {


    val notificationViewModel = remember { init() }
    val notificationState by notificationViewModel.notificationState
    val isLoading by notificationViewModel.isNotificationLoading
    val readState by notificationViewModel.readState
    val deleteState by notificationViewModel.deleteState
    val snackBarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        notificationViewModel.getNotifications()
    }

    LaunchedEffect(notificationState) {
        if (!notificationState.success && notificationState.errorMessage != null) {
            snackBarHostState.showSnackbar(notificationState.errorMessage!!)
        }
    }

    LaunchedEffect(readState) {
        if (!readState.success && readState.errorMessage != null) {
            snackBarHostState.showSnackbar(readState.errorMessage!!)
        }
    }

    LaunchedEffect(deleteState) {
        if (!deleteState.success && deleteState.errorMessage != null) {
            snackBarHostState.showSnackbar(deleteState.errorMessage!!)
        }
    }

    Box(modifier = Modifier.fillMaxSize()){
        LazyColumn (
            modifier = Modifier.fillMaxSize().align(Alignment.TopCenter).fillMaxHeight(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (!notificationState.success || notificationState.data.isNullOrEmpty()) {
                item {
                    Spacer(modifier = Modifier.padding(16.dp))
                    Text(
                        text = "No notifications",
                        style = Typography.bodyLarge.copy(
                            fontSize = 16.sp,
                            color = Secondary
                        )
                    )
                }
            }
            else {
                items(notificationState.data!!) { notification ->
                    NotificationCard(
                        title = notification.title,
                        isRead = notification.isRead == 1,
                        onClick = {
                            if (notification.isRead == 0) {
                                notificationViewModel.readNotification(notification.id)
                            }
                            val encodedTitle = Uri.encode(notification.title)
                            val encodedDescription = Uri.encode(notification.description)

                            navController.navigate("notificationDetails/$encodedTitle/$encodedDescription")
                        },
                        onDelete = {
                            notificationViewModel.deleteNotification(notification.id)
                        }
                    )
                }
            }

            if (
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
            ) {
                item {
                    Spacer(modifier = Modifier.padding(8.dp))
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = Secondary,
                        modifier = Modifier.fillMaxWidth(0.9f)
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(
                        text = "Notifications are disabled. Enable them to stay updated.",
                        style = Typography.bodyLarge.copy(color = Secondary),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.padding(16.dp))
            }


        }


    }

    CustomSnackBar(snackBarHostState)

    if (isLoading) {
        LoadingIndicator()
    }
}