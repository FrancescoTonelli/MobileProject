package com.hitwaves.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.hitwaves.R

data class NavItem(
    val route : String,
    val label : String,
    val icon : ImageVector,
    val selectedIcon: ImageVector
)

@Composable
fun getBottomNavItems(): List<NavItem> {
    return listOf(
        NavItem(
            route = "home",
            label = "Home",
            icon = ImageVector.vectorResource(id = R.drawable.home_line),
            selectedIcon = ImageVector.vectorResource(id = R.drawable.home_fill)
        ),
        NavItem(
            route = "likes",
            label = "Likes",
            icon = ImageVector.vectorResource(id = R.drawable.like_line),
            selectedIcon = ImageVector.vectorResource(id = R.drawable.like_fill)
        ),
        NavItem(
            route = "tickets",
            label = "Tickets",
            icon = ImageVector.vectorResource(id = R.drawable.ticket_line),
            selectedIcon = ImageVector.vectorResource(id = R.drawable.ticket_fill)
        ),
        NavItem(
            route = "account",
            label = "Account",
            icon = ImageVector.vectorResource(id = R.drawable.account_line),
            selectedIcon = ImageVector.vectorResource(id = R.drawable.account_fill)
        )

    )
}