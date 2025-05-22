package com.hitwaves.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hitwaves.ui.component.CustomSnackBar
import com.hitwaves.ui.component.NotificationCard
import com.hitwaves.ui.component.LoadingIndicator
import com.hitwaves.ui.theme.*
import com.hitwaves.ui.viewModel.NotificationViewModel

private fun init() : NotificationViewModel {
    return NotificationViewModel()
}

@Composable
fun Notification(navController: NavHostController) {


    val notificationViewModel = remember { init() }
    val notifs by notificationViewModel.notificationState
    val isLoading by notificationViewModel.isNotificationLoading
    val readNotifs by notificationViewModel.readState
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        notificationViewModel.getNotifications()
    }

    LaunchedEffect(notifs) {
        if (!notifs.success && notifs.errorMessage != null) {
            snackBarHostState.showSnackbar(notifs.errorMessage!!)
        }
    }

    LaunchedEffect(readNotifs) {
        if (!readNotifs.success && readNotifs.errorMessage != null) {
            snackBarHostState.showSnackbar(readNotifs.errorMessage!!)
        }
    }

    Box(modifier = Modifier.fillMaxSize()){
        LazyColumn (
            modifier = Modifier.fillMaxSize().align(Alignment.TopCenter).fillMaxHeight(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (!notifs.success || notifs.data.isNullOrEmpty()) {
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
                items(notifs.data!!) { notification ->
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

                        }
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