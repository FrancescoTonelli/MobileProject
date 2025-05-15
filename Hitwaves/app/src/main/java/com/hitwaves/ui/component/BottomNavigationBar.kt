package com.hitwaves.ui.component

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hitwaves.ui.theme.FgDark
import com.hitwaves.ui.theme.Primary
import com.hitwaves.ui.theme.Secondary

@Composable
fun BottomNavigationBar(navController: NavHostController, items: List<IconData>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = FgDark,
        contentColor = Secondary
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected && item.selectedIcon != null) item.selectedIcon else item.icon,
                        contentDescription = item.label,
                        tint = if (isSelected) Primary else Secondary
                    )
                },
                label = {
                    if (isSelected) {
                        Text(
                            text = item.label,
                            fontSize = 15.sp,
                            color = Primary
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                    selectedIconColor = Primary,
                    unselectedIconColor = Secondary,
                    selectedTextColor = Primary,
                    unselectedTextColor = Secondary
                )
            )
        }
    }
}