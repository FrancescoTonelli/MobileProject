package com.hitwaves.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hitwaves.R
import com.hitwaves.ui.theme.*
import com.hitwaves.ui.viewModel.NotificationViewModel
import com.hitwaves.ui.theme.FgDark
import com.hitwaves.ui.theme.Primary
import com.hitwaves.ui.theme.Secondary
import com.hitwaves.ui.theme.Typography

private fun init() : NotificationViewModel {
    return NotificationViewModel()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationTopBar(navController: NavHostController, item: IconData){
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isSelected = currentRoute == item.route

    val notificationViewModel = remember { init() }
    val notifs by notificationViewModel.notificationState
    val isLoading by notificationViewModel.isNotificationLoading
    val unreadNotificationCount = remember { mutableStateOf(0) }


    LaunchedEffect(Unit) {
        notificationViewModel.getNotifications()
    }

    LaunchedEffect(notifs) {
        if (notifs.success && notifs.data != null) {
            unreadNotificationCount.value = notifs.data?.count { it.isRead == 0 } ?: 0
        }
    }

    TopAppBar(
        title = {},
        navigationIcon = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween

            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                )
                {
                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        ImageVector.vectorResource(R.drawable.logo),
                        tint = Primary,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Hitwaves",
                        style = Typography.titleLarge.copy(
                            fontSize = 22.sp,
                            color = Primary,
                            drawStyle = Stroke(width = 3f)
                        )
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isSelected) {
                        Text(
                            text = item.label,
                            fontSize = 15.sp,
                            color = Primary
                        )
                    }
                    IconButton(
                        onClick = {
                            if (!isSelected) {
                                navController.navigate(item.route)
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotificationCount.value > 0) {
                                    Badge (
                                        containerColor = Primary,
                                        contentColor = Secondary
                                    ){
                                        Text(
                                            text = "${unreadNotificationCount.value}",
                                            style = Typography.bodyLarge.copy(
                                                fontSize = 11.sp,
                                                color = Secondary
                                            )
                                        )
                                    }
                                }
                            }
                        ){
                            Icon(
                                imageVector = if (isSelected && item.selectedIcon != null) item.selectedIcon else item.icon,
                                contentDescription = item.label,
                                tint = if (isSelected) Primary else Secondary
                            )
                        }

                    }
                }

            }
        },
        actions = {},
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = FgDark,
            navigationIconContentColor = Secondary,
            titleContentColor = Secondary
        )
    )
}