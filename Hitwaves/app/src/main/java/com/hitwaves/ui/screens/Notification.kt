package com.hitwaves.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hitwaves.api.NotificationResponse
import com.hitwaves.model.Artist
import com.hitwaves.ui.component.CustomSnackbar
import com.hitwaves.ui.component.EventCard
import com.hitwaves.ui.component.NotificationCard
import com.hitwaves.ui.component.loadingIndicator
import com.hitwaves.ui.theme.*
import com.hitwaves.ui.viewModel.LikesViewModel
import com.hitwaves.ui.viewModel.NotificationViewModel

private fun init() : NotificationViewModel {
    return NotificationViewModel()
}

@Composable
fun Notification(navController: NavHostController) {

    val notifsToShow = remember { mutableStateOf<List<NotificationResponse>>(emptyList()) }

    val notificationViewModel = remember { init() }
    val notifs by notificationViewModel.notificationState
    val isLoading by notificationViewModel.isNotificationLoading
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        notificationViewModel.getNotifications()
        notifsToShow.value = notifs.data ?: emptyList()
    }

    LaunchedEffect(notifs) {
        if (notifs.success && notifs.data != null) {
            notifsToShow.value = notifs.data!!
        } else if (!notifs.success && notifs.errorMessage != null) {
            snackbarHostState.showSnackbar(notifs.errorMessage!!)
        }
    }

    Box(modifier = Modifier.fillMaxSize()){
        LazyColumn (
            modifier = Modifier.fillMaxSize().align(Alignment.TopCenter).fillMaxHeight(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (notifs.data.isNullOrEmpty()) {
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
                items(notifsToShow.value) { notification ->
                    NotificationCard(
                        id = notification.id,
                        title = notification.title,
                        description = notification.description,
                        isRead = notification.isRead == 1,
                        onClick = {
                            // Handle click event
                            //notificationViewModel.markNotificationAsRead(notification.id)
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.padding(16.dp))
            }

        }
    }

    CustomSnackbar(snackbarHostState)

    if (isLoading) {
        loadingIndicator()
    }
}